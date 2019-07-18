package com.learn.hbase.bigtable.util;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;

public class HBaseBoot {

  public final Connection connection;
  public final Admin admin;

  public HBaseBoot() throws IOException {
    this("ignore", "ignore");
  }

  public HBaseBoot(String projectId, String instanceId) throws IOException {
    Configuration config = BigtableConfiguration.configure(projectId, instanceId);
    if ("ignore".equals(projectId)) {
      config.set(BigtableOptionsFactory.BIGTABLE_EMULATOR_HOST_KEY, "localhost:8086");
      config.set(BigtableOptionsFactory.BIGTABLE_ADMIN_HOST_KEY, "localhost:8086");
    }
    connection = ConnectionFactory.createConnection(config);
    admin = connection.getAdmin();
  }

  public HBaseBoot(boolean flag) throws Exception {
    Configuration configuration = HBaseConfiguration.create();
    configuration.set("hbase.zookeeper.quorum", "localhost");
    configuration.set("hbase.zookeeper.property.clientPort", "63787");
   connection = ConnectionFactory.createConnection(configuration);
   admin = connection.getAdmin();
  }

  public void createTable(String tableId, String... columnFamilies) throws IOException {
    TableDescriptorBuilder tableBuilder =
        TableDescriptorBuilder.newBuilder(TableName.valueOf(tableId));

    for (String cf : columnFamilies) {
      tableBuilder.setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(cf.getBytes()).build());
    }

    admin.createTable(tableBuilder.build());
  }
}
