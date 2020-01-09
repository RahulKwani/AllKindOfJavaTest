package com.local.learn.logging.lessionOne;

import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.LoggingEnhancer;

public class ExampleEnhancer implements LoggingEnhancer {

  @Override
  public void enhanceLogEntry(LogEntry.Builder logEntry) {
    logEntry.addLabel("test-rahul-lable-1", "test-value-will-be-rahul");
  }
}
