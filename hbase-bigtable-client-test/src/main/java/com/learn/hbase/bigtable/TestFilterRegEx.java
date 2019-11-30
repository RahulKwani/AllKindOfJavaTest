package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.SnapshotDescription;

/** */
public class TestFilterRegEx {

  private final HBaseBoot client;

  TestFilterRegEx() throws Exception {
    client = new HBaseBoot(64262);
    //    client = new HBaseBoot("grass-clump-479", "connectors");
  }

  public static void main(String[] args) throws Exception {
    TestFilterRegEx regTest = new TestFilterRegEx();
    //    System.out.println("BEFORE");
    //    regTest.printSnapshots(null);

    IntStream.range(1, 20)
        .forEach(
            index -> {
              TableName tableName = regTest.createMultipleTable();
              if (index % 3 == 0) {
                String snapshotName = "my-snap-" + RandomStringUtils.randomAlphanumeric(10);
                try {
                  regTest.client.admin.snapshot(snapshotName, tableName);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
            });

    System.out.println("AFTER");
    regTest.printSnapshots(null);
    //    regTest.checkAndDisable("");
  }

  private void checkAndDisable(String tableName) throws IOException {
    client.admin.disableTable(TableName.valueOf("test_table2-0000016e6f6422ac-c755e199af046e04"));
  }

  private void printDisableTables(String pattern) throws IOException {
    System.out.println("PRINTING disableTables");
    for (HTableDescriptor tableName : client.admin.disableTables(pattern)) {
      System.out.println(tableName.getNameAsString());
    }
  }

  private void printSnapshots(Pattern pattern) throws IOException {
    System.out.println("PRINTING listSnapshots");
    client.admin.listSnapshots();
    for (SnapshotDescription snpDes : client.admin.listSnapshots()) {
      System.out.println(snpDes);
    }
  }

  private TableName createMultipleTable() {
    HColumnDescriptor colFamily = new HColumnDescriptor("Cf");
    TableName myTableName =
        TableName.valueOf("My-Table-" + RandomStringUtils.randomAlphanumeric(10));

    HTableDescriptor des = new HTableDescriptor(myTableName).addFamily(colFamily);
    try {
      client.admin.createTable(des);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return myTableName;
  }
}
