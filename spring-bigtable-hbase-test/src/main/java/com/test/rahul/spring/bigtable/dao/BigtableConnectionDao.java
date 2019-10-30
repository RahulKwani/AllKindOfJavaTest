package com.test.rahul.spring.bigtable.dao;

import java.io.IOException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
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

  @Autowired(required = false)
  @Qualifier("bigtableConnection")
  private Connection con;

  public void isTableExist(String tableId) {}

  public Integer getCount(String tableName, String keyPrefix) throws IOException {
    if (con == null) {
      return 0;
    }

    int count = 0;
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
}
