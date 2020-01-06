package com.test.stackdriver.trace;

import com.google.api.gax.batching.Batcher;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutationEntry;
import io.opencensus.common.Scope;
import io.opencensus.contrib.grpc.metrics.RpcViews;
import io.opencensus.exporter.stats.stackdriver.StackdriverStatsExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Sampler;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.UUID;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class SimpleMutationRead {
  private static final Logger logger = Logger.getLogger(BigtableVeneer.class);

  // [START configChanges]
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final String INSTANCE_ID = System.getenv("INSTANCE_ID");
  // [END configChanges]

  // Refer to table metadata names by byte array in the HBase API
  private static final String TABLE_ID = "Hello-Bigtable";
  private static final String FAMILY_ID = "cf1";

  private static final Tracer tracer = Tracing.getTracer();

  // 3. Trace it and check if some of those are failing with Cancelled?
  public static void main(String[] args) throws IOException, InterruptedException {
    logger.setLevel(Level.INFO);
    configureOpenCensusExporters(Samplers.alwaysSample());

    try (BigtableDataClient client = BigtableDataClient.create(PROJECT_ID, INSTANCE_ID)) {
      try (Scope ss = tracer.spanBuilder("bigtable.readRow.op").startScopedSpan()) {

        String rowPrefix = UUID.randomUUID().toString();
        try (Batcher<RowMutationEntry, Void> batcher = client.newBulkMutationBatcher(TABLE_ID);
            Scope writeScope = tracer.spanBuilder("WriteRows").startScopedSpan()) {

          Span span = tracer.getCurrentSpan();
          span.addAnnotation("Writing to the table...");

          for (int i = 0; i < 10; i++) {
            batcher.add(
                RowMutationEntry.create(rowPrefix + "-" + i)
                    .setCell(FAMILY_ID, "qualifier", 10_000L, "value-" + i));
          }
        }

        for (int i = 0; i < 10; i++) {
          try (Scope readScope = tracer.spanBuilder("ReadRow").startScopedSpan()) {
            Row row = client.readRow(TABLE_ID, rowPrefix + "-" + i);
            logger.debug("Row: " + row.getKey().toStringUtf8());
          }
        }
      }
    }
    BigtableVeneer.sleep(5100);
  }

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
}
