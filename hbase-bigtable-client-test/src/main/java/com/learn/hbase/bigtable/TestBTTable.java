package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import com.learn.hbase.bigtable.util.MyUtil;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;

public class TestBTTable {

  private static final String tableId = "BeamCloudBigtableIOIntegrationTest";

  public static void main(String[] args) throws IOException {
    HBaseBoot hbaseClient = new HBaseBoot("grass-clump-479", "bigtableio-test");

    System.out.println(hbaseClient.admin.tableExists(TableName.valueOf(tableId)));

    Table table = hbaseClient.connection.getTable(TableName.valueOf(tableId));
    byte[] value = new byte[20490];
    new Random().nextBytes(value);

    ValueFilter valueFilter =
        new ValueFilter(CompareFilter.CompareOp.NOT_EQUAL, new BinaryComparator(value));
    Scan scan = new Scan().setOneRowLimit().setFilter(valueFilter);

    MyUtil.printHBaseResScan(table.getScanner(scan));
  }
}
