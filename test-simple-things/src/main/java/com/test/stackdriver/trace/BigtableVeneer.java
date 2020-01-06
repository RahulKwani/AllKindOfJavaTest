package com.test.stackdriver.trace;

import com.google.api.gax.batching.Batcher;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.admin.v2.models.CreateTableRequest;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutationEntry;
import com.google.protobuf.ByteString;
import io.opencensus.common.Scope;
import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.stats.Measure;
import io.opencensus.stats.Stats;
import io.opencensus.stats.StatsRecorder;
import io.opencensus.tags.TagKey;
import io.opencensus.trace.Sampler;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BigtableVeneer {
  private static final Logger logger = Logger.getLogger(BigtableVeneer.class);

  // [START configChanges]
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final String INSTANCE_ID = System.getenv("INSTANCE_ID");
  // [END configChanges]
  // Refer to table metadata names by byte array in the HBase API
  private static final String TABLE_ID = "Hello-Bigtable";
  private static final String COLUMN_FAMILY_NAME = "cf1";
  private static final String COLUMN_NAME = "greeting";

  // The read latency in milliseconds
  private static final Measure.MeasureDouble M_READ_LATENCY_MS =
      Measure.MeasureDouble.create(
          "btapp/read_latency", "The latency in milliseconds for read", "ms");

  // [START config_oc_write_latency_measure]
  // The write latency in milliseconds
  private static final Measure.MeasureDouble M_WRITE_LATENCY_MS =
      Measure.MeasureDouble.create(
          "btapp/write_latency", "The latency in milliseconds for write", "ms");
  // [END config_oc_write_latency_measure]

  // Counts the number of transactions
  private static final Measure.MeasureLong M_TRANSACTION_SETS =
      Measure.MeasureLong.create("btapp/transaction_set_count", "The count of transactions", "1");

  // Define the tags for potential grouping
  private static final TagKey KEY_LATENCY = TagKey.create("latency");
  private static final TagKey KEY_TRANSACTIONS = TagKey.create("transactions");

  private static final StatsRecorder STATS_RECORDER = Stats.getStatsRecorder();

  private static final Tracer tracer = Tracing.getTracer();

  // Write some friendly greetings to Cloud Bigtable
  private static final String[] GREETINGS = {
    "Hello World!", "Hello Cloud Bigtable!", "Hello HBase!"
  };

  // [START config_oc_stackdriver_export]
  private static void configureOpenCensusExporters(Sampler sampler) throws IOException {
    TraceConfig traceConfig = Tracing.getTraceConfig();

    // For demo purposes, lets always sample.

    traceConfig.updateActiveTraceParams(
        traceConfig.getActiveTraceParams().toBuilder().setSampler(sampler).build());

    // Create the Stackdriver trace exporter
    StackdriverTraceExporter.createAndRegister(
        StackdriverTraceConfiguration.builder().setProjectId(PROJECT_ID).build());

    // [Start Stackdriver Monitoring]
    StackdriverStatsExporter.createAndRegister();

    // [END config_oc_stackdriver_export]

    // -------------------------------------------------------------------------------------------
    // Register all the gRPC views
    // OC will automatically go and instrument gRPC. It's going to capture app level metrics
    // like latency, req/res bytes, count of req/res messages, started rpc etc.
    // -------------------------------------------------------------------------------------------
    RpcViews.registerAllGrpcViews();
  }

  /** Connects to Cloud Bigtable, runs some basic operations and prints the results. */
  private static void doBigTableOperations(String projectId, String instanceId, int demoRowCount) {
    long startRead;
    long endRead;
    long startWrite;
    long endWrite;

    try (BigtableTableAdminClient admin = BigtableTableAdminClient.create(projectId, instanceId);
        BigtableDataClient client = BigtableDataClient.create(projectId, instanceId)) {

      if (!admin.exists(TABLE_ID)) {
        // [START creating_a_table]
        // Create a table with a single column family
        admin.createTable(CreateTableRequest.of(TABLE_ID).addFamily(COLUMN_FAMILY_NAME));
        logger.info("Create table " + TABLE_ID);
        // [END creating_a_table]
      }

      // [START writing_rows]
      // Retrieve the table we just created so we can do some reads and writes
      Batcher<RowMutationEntry, Void> batcher = client.newBulkMutationBatcher(TABLE_ID);
      for (int i = 0; i < demoRowCount; i++) {
        // [START opencensus_scope_main]
        try (Scope ss = tracer.spanBuilder("verify.Bigtable.Tutorial").startScopedSpan()) {

          // generate unique UUID
          UUID uuid = UUID.randomUUID();
          String randomUUIDString = uuid.toString();

          startRead = System.currentTimeMillis();
          // write to Bigtable
          writeRows(batcher, randomUUIDString);
          endRead = System.currentTimeMillis();

          startWrite = System.currentTimeMillis();
          // read from Bigtable
          readRows(client, randomUUIDString);
          endWrite = System.currentTimeMillis();

          // [END opencensus_scope_main]
          // [START opencensus_metric_record]
          // record read, write latency metrics and count
          STATS_RECORDER
              .newMeasureMap()
              .put(M_READ_LATENCY_MS, endRead - startRead)
              .put(M_WRITE_LATENCY_MS, endWrite - startWrite)
              .put(M_TRANSACTION_SETS, 1)
              .record();
          // [END opencensus_metric_record]

        }
      }
    } catch (IOException e) {
      logger.error("Exception while running HelloWorld: " + e.getMessage());
      StringWriter stackTraceString = new StringWriter();
      e.printStackTrace(new PrintWriter(stackTraceString));
      logger.error(stackTraceString.toString());
      System.exit(1);
    }
  }

  private static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (Exception e) {
      logger.error("Exception while sleeping " + e.getMessage());
    }
  }

  private static void writeRows(Batcher<RowMutationEntry, Void> batcher, String prefix) {
    try (Scope ss = tracer.spanBuilder("WriteRows").startScopedSpan()) {
      // Write some rows to the table
      Span span = tracer.getCurrentSpan();
      span.addAnnotation("Writing greetings to the table...");
      logger.debug("Write some greetings to the table");
      for (int i = 0; i < GREETINGS.length; i++) {
        // Each row has a unique row key.
        //
        // Note: This example uses sequential numeric IDs for simplicity, but
        // this can result in poor performance in a production application.
        // Since rows are stored in sorted order by key, sequential keys can
        // result in poor distribution of operations across nodes.
        //
        // For more information about how to design a Bigtable schema for the
        // best performance, see the documentation:
        //
        //     https://cloud.google.c"#greeting"om/bigtable/docs/schema-design
        String rowKey = prefix + +i;

        // Put a single row into the table. We could also pass a list of Puts to write a batch.
        batcher.add(
            RowMutationEntry.create(rowKey).setCell(COLUMN_FAMILY_NAME, COLUMN_NAME, GREETINGS[i]));
      }
    }
  }

  private static void readRows(BigtableDataClient client, String prefix) throws IOException {
    try (Scope ss = tracer.spanBuilder("ReadRows").startScopedSpan()) {
      // [START getting_a_row]
      // Get the first greeting by row key
      String rowKey = prefix + "#greeting0";
      Row rowResult = client.readRow(TABLE_ID, rowKey);
      String greeting = rowResult.getCells().get(0).getValue().toStringUtf8();
      logger.debug("Get a single greeting by row key");
      logger.debug(String.format("\t%s = %s\n", rowKey, greeting));
      // [END getting_a_row]

      // [START scanning_all_rows]
      // Now scan across all rows for UUID prefix.
      byte[] startRow = (prefix + "#greeting0").getBytes();
      byte[] stopRow = (prefix + "#greeting3").getBytes();
      Scan scan = new Scan().withStartRow(startRow).withStopRow(stopRow);

      Query query = Query.create(TABLE_ID).prefix(prefix).range("#greeting0", "#greeting3");
      logger.debug("Scan for all greetings:");
      ServerStream<Row> stream = client.readRows(query);
      for (Row row : stream) {
        ByteString valueBytes = row.getCells().get(0).getValue();
        logger.debug('\t' + valueBytes.toStringUtf8());
      }
      // [END scanning_all_rows]
    }
  }

  public static void main(String[] args) throws IOException {
    logger.setLevel(Level.INFO);

    // [START config_oc_trace_sample]
    // sample every 1000 transactions
    configureOpenCensusExporters(Samplers.probabilitySampler(1 / 1000.0));
    // [END config_oc_trace_sample]

    doBigTableOperations(PROJECT_ID, INSTANCE_ID, 10000);

    // IMPORTANT: do NOT exit right away. Wait for a duration longer than reporting
    // duration (5s) to ensure spans are exported. Spans are exported every 5 seconds.
    sleep(5100);
  }
}
