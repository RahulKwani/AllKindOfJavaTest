package com.test.stackdriver.trace;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.Date;
import org.joda.time.DateTime;

public class TraceSample {

  // [START trace_setup_java_custom_span]
  private static final Tracer tracer = Tracing.getTracer();

  public static void doWork() {
    // Create a child Span of the current Span.
    try (Scope ss = tracer.spanBuilder("TestSampling-MyChildWorkSpan").startScopedSpan()) {
      doInitialWork();
      tracer.getCurrentSpan().addAnnotation("Finished initial work");
      doFinalWork();
    }
  }

  public static void main(String[] args) throws Exception {
    createAndRegisterGoogleCloudPlatform("grass-clump-479");
    System.out.println("RUNNING withing grass-clump");
    doWorkFullSampled();
  }

  private static void doInitialWork() {
    // ...
    tracer.getCurrentSpan().addAnnotation("Doing initial work");
    // ...
  }

  private static void doFinalWork() {
    // ...
    tracer.getCurrentSpan().addAnnotation("Hello world!");
    // ...
  }
  // [END trace_setup_java_custom_span]

  // [START trace_setup_java_full_sampling]
  public static void doWorkFullSampled() {
    try (Scope ss =
        tracer
            .spanBuilder("TestSampling-MyChildWorkSpan")
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      doInitialWork();
      tracer.getCurrentSpan().addAnnotation("Finished initial work");
      doFinalWork();
    }
  }
  // [END trace_setup_java_full_sampling]

  // [START trace_setup_java_create_and_register]
  public static void createAndRegister() throws IOException {
    StackdriverTraceExporter.createAndRegister(StackdriverTraceConfiguration.builder().build());
  }
  // [END trace_setup_java_create_and_register]

  // [START trace_setup_java_create_and_register_with_token]
  public static void createAndRegisterWithToken(String accessToken) throws IOException {
    Date expirationTime = DateTime.now().plusSeconds(60).toDate();

    GoogleCredentials credentials =
        GoogleCredentials.create(new AccessToken(accessToken, expirationTime));
    StackdriverTraceExporter.createAndRegister(
        StackdriverTraceConfiguration.builder()
            .setProjectId("MyStackdriverProjectId")
            .setCredentials(credentials)
            .build());
  }
  // [END trace_setup_java_create_and_register_with_token]

  // [START trace_setup_java_register_exporter]
  public static void createAndRegisterGoogleCloudPlatform(String projectId) throws IOException {
    StackdriverTraceExporter.createAndRegister(
        StackdriverTraceConfiguration.builder().setProjectId(projectId).build());
  }
  // [END trace_setup_java_register_exporter]
}
