package com.investigate.random.stuff;

import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.ServiceOptions;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataClientFactory;
import com.google.cloud.bigtable.data.v2.BigtableDataSettings;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfiguration {

  static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();
  static final String INSTANCE_ID = "connectors";

  static final String TABLE_ID = "Hello-Bigtable";
  static final String COL_FAMILY = "cf1";

  @Bean
  public BigtableDataClientFactory dataClientFactory() throws IOException {
    return BigtableDataClientFactory.create(getDataSettings());
  }

  @Bean
  public BigtableDataClient dataClient(BigtableDataClientFactory dataClientFactory) {
    return dataClientFactory.createDefault();
  }

  @Bean
  public BasicService service(
      BigtableDataClientFactory dataClientFactory, BigtableDataClient dataClient) {
    return new BasicService(dataClientFactory, dataClient);
  }

  private static BigtableDataSettings getDataSettings() {
    BigtableDataSettings.Builder settings =
        BigtableDataSettings.newBuilder().setProjectId(PROJECT_ID).setInstanceId(INSTANCE_ID);

    InstantiatingExecutorProvider executorProvider =
        InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(20).build();
    settings.stubSettings().setExecutorProvider(executorProvider);

    return settings.build();
  }
}
