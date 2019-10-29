package com.learn.opencensus.bigtable.it;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;

/** This is to fix the GCJ#issue#ReadRows cancelled. */
public class ReadRowTracesProblem {

  public static void main(String[] args) throws IOException {

    BigtableDataSettings.enableOpenCensusStats();

    // 1. Configure exporter to export traces to Zipkin.
    ZipkinTraceExporter.createAndRegister(
        "http://localhost:9411/api/v2/spans", "Another-service-tracing");

    //    ZipkinTraceExporter.createAndRegister(ZipkinExporterConfiguration.builder()
    //        .setV2Url("http://localhost:9411/api/v2/spans").build());
    // 2. Configure 100% sample rate, otherwise, few traces will be sampled.

    TraceConfig traceConfig = Tracing.getTraceConfig();
    TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
    traceConfig.updateActiveTraceParams(
        activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());

    //    LoggingTraceExporter.register();
    BigtableDataSettings.Builder settingsB =
        BigtableDataSettings.newBuilder()
            .setProjectId("grass-clump-479")
            .setInstanceId("bigtableio-test");

    // 3. Get the global singleton Tracer object.
    Tracer tracer = Tracing.getTracer();

    BigtableDataClient dataClient = BigtableDataClient.create(settingsB.build());

    // 4. Create a scoped span, a scoped span will automatically end when closed.
    // It implements AutoClosable, so it'll be closed when the try block ends.
    try (Scope scope = tracer.spanBuilder("main").startScopedSpan()) {
      System.out.println("About to do some busy work...");
      doWork(10000, dataClient);
    }

    dataClient.close();
  }

  private static void doWork(int i, BigtableDataClient dataClient) {
    System.out.println("printing data " + i);

    Tracer tr = Tracing.getTracer();

    try {

      try (Scope another = tr.spanBuilder("Something").startScopedSpan()) {
        System.out.println("pretending to work");
        Thread.sleep(400);
        System.out.println("finished work!!");
      }

      Thread.sleep(200);
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.out.println("INTERRUPTED");
    }
  }
}
