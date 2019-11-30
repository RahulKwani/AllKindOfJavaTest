package com.learn.hbase.bigtable.util;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.google.cloud.bigtable.hbase.BigtableOptionsFactory;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

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

  /* TO run against the HBase minicluster */
  public HBaseBoot(int clientPort) throws Exception {
    Configuration configuration = HBaseConfiguration.create();
    configuration.set("hbase.zookeeper.quorum", "localhost");
    configuration.set("hbase.zookeeper.property.clientPort", String.valueOf(clientPort));
    connection = ConnectionFactory.createConnection(configuration);
    admin = connection.getAdmin();
  }

  public static void createTable(Admin admin, String tableId, String... columnFamilies)
      throws IOException {
    HTableDescriptor tableBuilder = new HTableDescriptor(TableName.valueOf(tableId));

    for (String cf : columnFamilies) {
      tableBuilder.addFamily(new HColumnDescriptor(cf.getBytes()));
    }

    admin.createTable(tableBuilder);
  }
}
