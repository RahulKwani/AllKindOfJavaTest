package com.learn.hbase.bigtable;

import static com.learn.hbase.bigtable.util.MyUtil.qualifier;
import static com.learn.hbase.bigtable.util.MyUtil.timeInLong;
import static com.learn.hbase.bigtable.util.MyUtil.value;

import com.google.cloud.bigtable.config.Logger;
import com.google.common.collect.ImmutableList;
import com.learn.hbase.bigtable.util.HBaseBoot;
import java.io.IOException;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * This is meant to understand <a
 * href="https://stackoverflow.com/questions/56979965/bigtable-column-family-time-range-scan-returning-all-rows-regardless-of-timestam">this</a>
 * question.
 *
 * <p>I am trying to use a ColumnFamilyTimeRange on my Scan to read only recent rows from Bigtable.
 * However, the scan returns all rows no matter what I set the time range to.
 *
 * <p>I have one column family. Here's what I'm seeing: I add a new row with a value for that column
 * family, then wait, then add another new row. I then do a read from Bigtable with a Scan with an
 * ordinary (ie not column family specific) TimeRange set. It correctly returns only the recently
 * added row.
 *
 * <p>However, when I change that TimeRange to a ColumnFamilyTimeRange with the same timestamp
 * bounds and the only column family I have, I get back every row. Even when I set the timestamp
 * bounds to something nonsensical (such as before I even created the table), I still get back every
 * row.
 *
 * <p>Is this a bug or am I completely missing how ColumnFamilyTimeRange is meant to work?
 */
public class ColumnTimeRangeLearn {

  private static final int TOTAL_COL = 5;
  private static final String TABLE_ID = "MyTestTable";
  private final HBaseBoot client;

  private static final String rowKey1 = "rowkey_1";
  private static final String rowKey2 = "rowkey_2";
  private static final String rowKey3 = "rowkey_3";

  private Table table;

  ColumnTimeRangeLearn() throws Exception {
    //    client = new HBaseBoot(59327);
    client = new HBaseBoot("grass-clump-479", "connectors");
    if (!client.admin.tableExists(TableName.valueOf(TABLE_ID))) {
      createAndAdd(client);
    }
    //    table = client.connection.getTable(TableName.valueOf(TABLE_ID));

  }

  static void createAndAdd(HBaseBoot client) throws IOException {
    HBaseBoot.createTable(client.admin, TABLE_ID, "cf1", "cf2", "cf3");

    try (Table table = client.connection.getTable(TableName.valueOf(TABLE_ID))) {
      // Adding some data for test
      addFiveCols(table, rowKey1);
      addFiveCols(table, rowKey2);
      addFiveCols(table, rowKey3);

      addFiveCols(table, rowKey1);
      addFiveCols(table, rowKey2);
      addFiveCols(table, rowKey3);
    }
  }

  static void addFiveCols(Table table, String rowKey) throws IOException {
    Put put = new Put(rowKey.getBytes());

    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf1".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf2".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf3".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    table.put(put);
  }

  private void scanTable() throws IOException {
    Scan scan = new Scan();
    //    scan.withStartRow(rowKey1.getBytes()).withStopRow(rowKey3.getBytes());
    printResultScanner(table.getScanner(scan));
  }

  private void performBatchCallBack() throws Exception {
    Put put = new Put(rowKey1.getBytes());
    for (int i = 0; i < TOTAL_COL; i++) {
      put.addColumn("cf1".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }

    Put put2 = new Put(rowKey1.getBytes());
    for (int i = 0; i < TOTAL_COL; i++) {
      put2.addColumn("cf1".getBytes(), qualifier().getBytes(), timeInLong(), value());
    }
    table.batchCallback(
        ImmutableList.of(put),
        new Object[1],
        new Batch.Callback<Result>() {
          int counter = 0;

          @Override
          public void update(byte[] region, byte[] row, Result result) {
            counter++;
            System.out.println("COUNTER: " + counter);
          }
        });
  }

  static void printResultScanner(ResultScanner rs) throws IOException {
    Result result = rs.next();

    while (result != null) {
      int cellCounter = 0;
      System.out.println("Row: " + Bytes.toString(result.getRow()));
      for (Cell cell : result.rawCells()) {
        System.out.println(
            "Family: "
                + Bytes.toString(cell.getFamilyArray())
                + "\tQualifier: "
                + Bytes.toString(cell.getQualifierArray())
                + "\tTimestamp: "
                + cell.getTimestamp()
                + "\tValue:"
                + Bytes.toString(cell.getValueArray()));
        cellCounter++;
        System.out.println();
      }
      System.out.println("Total Cells====>:s " + cellCounter);
      result = rs.next();
    }
  }

  private void printTimestampOnly(ResultScanner rs) throws IOException {
    Result result = rs.next();
    int counter = 0;
    while (result != null) {
      System.out.println("Row: " + Bytes.toString(result.getRow()));
      for (Cell cell : result.rawCells()) {
        System.out.println(++counter);
        System.out.println(
            "\tFamily: "
                + Bytes.toString(cell.getFamilyArray())
                + "\n \tTimestamp: "
                + cell.getTimestamp());
        System.out.println();
      }
      result = rs.next();
    }
  }

  private void scanTimeStamp() throws IOException {
    Scan scan = new Scan().setTimeRange(1503000000000L, 1503999999999L);
    printResultScanner(table.getScanner(scan));
  }

  private void scanTimeStampWithCF() throws IOException {
    Scan scan =

        // Bigtable
        new Scan()
            .setColumnFamilyTimeRange("cf1".getBytes(), 942517717954L, 942517752782L)
            .setColumnFamilyTimeRange("cf2".getBytes(), 1503167215265L, 1503167283429L)
            .setColumnFamilyTimeRange("cf3".getBytes(), 1554316104295L, 1554316199999L)
            .setColumnFamilyTimeRange("cf3".getBytes(), 1503167212213L, 1503167375633L);

    // HBase
    //        new Scan()
    //            .setColumnFamilyTimeRange("cf1".getBytes(), 1503167301508L, 1503167390224L)
    //            .setColumnFamilyTimeRange("cf2".getBytes(), 942517705182L, 942517799354L)
    //            .setColumnFamilyTimeRange("cf3".getBytes(), 1554316100523L, 1554316152312L)
    //            .setColumnFamilyTimeRange("cf3".getBytes(), 1503167296765L, 1503167362401L);

    printTimestampOnly(table.getScanner(scan));
  }

  public static void main2(String[] args) throws Exception {
    ColumnTimeRangeLearn ct = new ColumnTimeRangeLearn();
    //     Admin operations

    // ct.client.admin.deleteTable(TableName.valueOf(TABLE_ID));

    //    ct.table = ct.client.connection.getTable(TableName.valueOf(TABLE_ID));

    //     simple table scan
    ct.scanTable();
    System.out.println("--------------------------------");

    ct.scanTimeStampWithCF();
    //    System.out.println("--------------------------------");
    //    ct.scanTimeStamp();
    //    ct.performBatchCallBack();
    //
    //    HTableDescriptor[] descriptors =
    //        ct.client.admin.getTableDescriptorsByTableName(ImmutableList.of());
    //    for (HTableDescriptor d : descriptors) {
    //      System.out.println("GTDBT-> " + d.getNameAsString());
    //    }
  }

  public static void main(String[] args) {
    Logger logger = new Logger(ColumnTimeRangeLearn.class);
    logger.info("INFO : sdkjf jfkljd sfkljdsfl kldsfdsklf");
    logger.debug("DEBUG : gdsfkljdslkfj kjflkdjsf k");
    logger.warn("WARNNING : ");
  }
}
