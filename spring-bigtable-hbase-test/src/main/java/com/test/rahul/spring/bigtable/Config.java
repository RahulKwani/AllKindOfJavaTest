package com.test.rahul.spring.bigtable;

import com.codahale.metrics.jmx.JmxReporter;
import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import com.google.cloud.bigtable.metrics.BigtableClientMetrics;
import com.google.cloud.bigtable.metrics.DropwizardMetricRegistry;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "datasource.bigtable")
public class Config {

  @Autowired private Environment env;

  @Value("${datasource.bigtable.projectId}")
  private String projectId;

  @Value("${datasource.bigtable.instanceId}")
  private String instanceId;

  @Value("${datasource.bigtable.useGCJ}")
  private Boolean useGCJ;

  private String some;

  @Bean(name = "bigtableConnection")
  public Connection getConnection() {

    if (StringUtils.isEmpty(projectId) || StringUtils.isEmpty(instanceId)) {
      return null;
    }

    DropwizardMetricRegistry registry = new DropwizardMetricRegistry();
    JmxReporter reporter =
        JmxReporter.forRegistry(registry.getRegistry())
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .convertRatesTo(TimeUnit.SECONDS)
            .build();
    reporter.start();
    BigtableClientMetrics.setMetricRegistry(registry);
    BigtableClientMetrics.setLevelToLog(BigtableClientMetrics.MetricLevel.Trace);

    //    final CsvReporter csvReporter =
    //        CsvReporter.forRegistry(registry.getRegistry())
    //            .formatFor(Locale.US)
    //            .convertRatesTo(TimeUnit.SECONDS)
    //            .convertDurationsTo(TimeUnit.MILLISECONDS)
    //            .build(new File("/Users/rahul/Documents/My_Home/GCP_Work/temp-bigtable/"));
    //    csvReporter.start(1, TimeUnit.SECONDS);
    //
    //    DropwizardMetricRegistry.createSlf4jReporter(
    //        registry, LoggerFactory.getLogger("com.test.rahul.spring.bigtable"), 1,
    // TimeUnit.SECONDS);

    org.apache.hadoop.conf.Configuration config =
        BigtableConfiguration.configure(projectId, instanceId);
    config.setBoolean(BigtableOptionsFactory.ENABLE_GRPC_RETRIES_KEY, true);
    config.setInt(BigtableOptionsFactory.MAX_SCAN_TIMEOUT_RETRIES, 3);
    config.setBoolean(BigtableOptionsFactory.BIGTABLE_USE_GCJ_CLIENT, useGCJ);
    return BigtableConfiguration.connect(config);
  }

  @Bean
  public Admin adminBean(Connection bigtableConnection) throws IOException {
    return bigtableConnection.getAdmin();
  }
}
