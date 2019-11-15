package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import com.learn.hbase.bigtable.util.MyUtil;
import java.io.IOException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

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
