package com.learn.opencensus.bigtable.it;

import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.zipkin.ZipkinExporterConfiguration;
import io.opencensus.exporter.trace.zipkin.ZipkinTraceExporter;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;
import java.util.HashMap;
import java.util.Map;

/** This class tests OpenCensus on local machine. */
public class TestOpenCensus {

  public static void main(String[] args) {

    // 1. Configure exporter to export traces to Zipkin.
    ZipkinTraceExporter.createAndRegister(
        ZipkinExporterConfiguration.builder()
            .setV2Url("http://localhost:9411/api/v2/spans")
            .setServiceName("zipkin-test-service2")
            .build());

    TraceConfig traceConfig = Tracing.getTraceConfig();
    TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
    traceConfig.updateActiveTraceParams(
        activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());

    // 3. Get the global singleton Tracer object.
    Tracer tracer = Tracing.getTracer();

    // 4. Create a scoped span, a scoped span will automatically end when closed.
    // It implements AutoClosable, so it'll be closed when the try block ends.
    try (Scope ss = Tracing.getTracer().spanBuilder("some other").startScopedSpan()) {
      System.out.println("Temp");
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    try (Scope scope = Tracing.getTracer().spanBuilder("oadfa").startScopedSpan()) {
      System.out.println("About to do some busy work...");
      for (int i = 0; i < 10; i++) {
        final int val = i;
        doWork(i);
      }
    }

    // 5. Gracefully shutdown the exporter, so that it'll flush queued traces to Zipkin.
    Tracing.getExportComponent().shutdown();
  }

  private static void doWork(int i) {
    Tracer tracer = Tracing.getTracer();
    tracer.getCurrentSpan().setStatus(Status.DATA_LOSS);

    // 7. Start another span. If another span was already started, it'll use that span as the parent
    // span.
    // In this example, the main method already started a span, so that'll be the parent span, and
    // this will be
    // a child span.
    try (Scope scope = tracer.spanBuilder("hjkbkjm").startScopedSpan()) {
      System.out.println("No:---> " + i);
      // Simulate some work.
      Span span = tracer.getCurrentSpan();

      try {
        System.out.println("doing busy work");
        Thread.sleep(100L);
      } catch (InterruptedException e) {
        // 6. Set status upon error
        span.setStatus(Status.INTERNAL.withDescription(e.toString()));
      }

      // 7. Annotate our span to capture metadata about our operation
      Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
      attributes.put("use", AttributeValue.stringAttributeValue("demo"));
      span.addAnnotation("Invoking doWork", attributes);
    }

    try (Scope scope = Tracing.getTracer().spanBuilder("another Span").startScopedSpan()) {
      System.out.println("again mimic the work");
      anotherMethod();
      Thread.sleep(100L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static void anotherMethod() {
    System.out.println("Another temp method");

    try (Scope ss = Tracing.getTracer().spanBuilder("AnotherMeth").startScopedSpan()) {
      Thread.sleep(50);
    } catch (Exception e) {
      System.out.println("Exception occured");
      e.printStackTrace();
    }
  }
}
