package com.investigate.random.stuff;

import com.google.cloud.ServiceOptions;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {

  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  private static final String INSTANCE_ID = "connectors";

  static final String TABLE_ID = "Hello-Bigtable";
  static final String COL_FAMILY = "cf1";

  @Bean
  public BigtableDataClient dataClient() throws IOException {
    try {
      return BigtableDataClient.create(PROJECT_ID, INSTANCE_ID);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw ex;
    }
  }
}
