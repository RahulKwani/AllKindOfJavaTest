package com.test.rahul.spring.bigtable;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import java.sql.Connection;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "datasource.bigtable")
public class Config {

  private String projectId;
  private String instanceId;


  @Bean(name = "bigtableConnection")
  public Connection getConnection() {
    if(StringUtils.isEmpty(projectId) || StringUtils.isEmpty(instanceId)) {
      return null;
    }

    org.apache.hadoop.conf.Configuration config = BigtableConfiguration.configure(projectId,
        instanceId);
    config.setBoolean(BigtableOptionsFactory.ENABLE_GRPC_RETRIES_KEY, true);
    config.setInt(BigtableOptionsFactory.MAX_SCAN_TIMEOUT_RETRIES, 3);
    return BigtableConfiguration.connect(config);
  }

}
