package com.test.rahul.spring.bigtable.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class BigtableConnectionDao {

  private final Logger logger = LoggerFactory.getLogger(BigtableConnectionDao.class.getName());

  private static final long TIME = System.currentTimeMillis();

  @Autowired(required = false)
  @Qualifier("bigtableConnection")
  private Connection con;

  @Autowired private Admin admin;

  public boolean isTableExist(String tableId) throws IOException {
    if (con == null) {
      throw new RuntimeException("Conn is null");
    }
    try (Admin admin = con.getAdmin()) {
      return admin.tableExists(TableName.valueOf(tableId));
    }
  }

  public Integer getCount(String tableName, String keyPrefix) throws IOException {
    if (con == null) {
      return 0;
    }

    int count = 0;
    int cellsCount = 0;
    try (Table table = con.getTable(TableName.valueOf(tableName))) {
      Scan scan = new Scan();
      scan.setRowPrefixFilter(Bytes.toBytes(keyPrefix));

      try (ResultScanner scanner = table.getScanner(scan)) {
        for (Result row : scanner) {
          count++;
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    return count;
  }

  public void addRowsAndCols(TableName tableName, String rowKey, int colNum) throws IOException {
    try (Table table = con.getTable(tableName)) {
      Put put = new Put(rowKey.getBytes());

      for (int i = 0; i < colNum; i++) {
        put.addColumn(
            "cf".getBytes(), ranStr("qual-").getBytes(), TIME, ranStr("value-").getBytes());
      }
      table.put(put);
    }
  }

  public List<String> scanTable(TableName tableName, String rowKey) throws IOException {
    Scan scan = new Scan();
    if (rowKey != null && rowKey.isEmpty()) {
      scan.setRowPrefixFilter(rowKey.getBytes());
    }

    List<String> rows = new ArrayList<>();
    try (Table table = con.getTable(tableName)) {
      ResultScanner rs = table.getScanner(new Scan());
      for (Result result : rs) {
        rows.add(Bytes.toString(result.getRow()));
      }
    }

    return rows;
  }

  private static String ranStr(String prefix) {
    return prefix + RandomStringUtils.randomAlphanumeric(5);
  }

  public boolean checkChannelPool(TableName tableName) throws IOException {
    HTableDescriptor tableDescriptor = admin.getTableDescriptor(tableName);
    System.out.println("table is: " + tableDescriptor.getNameAsString());

    try (Table table = con.getTable(tableName)) {
      RegionLocator rl = con.getRegionLocator(tableName);
      for (HRegionLocation region : rl.getAllRegionLocations()) {
        System.out.println(region);
      }

      byte[] values = ranStr("values-").getBytes();
      Table.CheckAndMutateBuilder checkAndMutateBuilder =
          table.checkAndMutate("rowkey-to-add".getBytes(), "cf".getBytes());
      checkAndMutateBuilder
          .ifEquals(values)
          .thenPut(
              new Put("rowkey-to-add".getBytes())
                  .addColumn("cf".getBytes(), ranStr("qualifier-").getBytes(), values));
    }

    return tableDescriptor.getFamilies() != null;
  }

  public void sendBatch(TableName tableName, int times) throws IOException, InterruptedException {
    List<Get> gets = new ArrayList<>(times);
    List<Put> puts = new ArrayList<>(times);
    IntStream.range(0, times)
        .forEach(
            ind -> {
              byte[] rowkey = Bytes.toBytes(ranStr("rowkey") + ind);

              puts.add(
                  new Put(rowkey)
                      .addColumn(
                          "cf".getBytes(),
                          ranStr("qual-" + ind).getBytes(),
                          ranStr("VALUES").getBytes()));

              gets.add(new Get(rowkey));
            });

    try (Table table = con.getTable(tableName)) {
      Object[] mutationResponse = new Object[times];
      table.batch(puts, mutationResponse);
      System.out.println("Mutation batch is done");

      Object[] readResponse = new Object[times];
      table.batch(gets, readResponse);
      System.out.println("BulkRead is done");
      for (Object row : readResponse) {
        System.out.println("ROW::" + row);
      }
    }
  }
}
