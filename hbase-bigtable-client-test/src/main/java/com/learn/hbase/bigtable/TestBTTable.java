package com.learn.hbase.bigtable;

import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.learn.hbase.bigtable.util.BigtableBoot;
import com.learn.hbase.bigtable.util.HBaseBoot;
import com.learn.hbase.bigtable.util.MyUtil;
import java.io.IOException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnCountGetFilter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

public class TestBTTable {

  private static final String tableId = "BeamCloudBigtableIOIntegrationTest";

  public static void main(String[] args) throws IOException {
    HBaseBoot hBoot = new HBaseBoot("ignore", "ignore");

    System.out.println(hBoot.admin.tableExists(TableName.valueOf(tableId)));
    Table table = hBoot.connection.getTable(TableName.valueOf(tableId));

    Scan scan = new Scan().setMaxResultSize(10);

    MyUtil.printHBaseResScan(table.getScanner(scan));
  }
}
