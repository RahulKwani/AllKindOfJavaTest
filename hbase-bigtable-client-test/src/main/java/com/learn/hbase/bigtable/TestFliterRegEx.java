package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;

/**
 *
 */
public class TestFliterRegEx {

  private final HBaseBoot boot;

  TestFliterRegEx() throws Exception {
    boot = new HBaseBoot(63264);
  }

  public static void main(String[] args) throws Exception {
    TestFliterRegEx regTest = new TestFliterRegEx();
//    IntStream.range(1, 10).forEach( x -> {
//      try {
//        regTest.createMultipleTable();
//      } catch (IOException e) {
//        e.printStackTrace();
//      }
//    });
    for (HTableDescriptor tableName : regTest.boot.admin.deleteTables((Pattern) null)) {
      System.out.println(tableName.getNameAsString());
    }
  }

  public void createMultipleTable() throws IOException {
    ColumnFamilyDescriptor colFamily = ColumnFamilyDescriptorBuilder.of("Cf");
    String myTableName = "My-Table-" + RandomStringUtils.randomAlphanumeric(10);

    TableDescriptor des =
        TableDescriptorBuilder.newBuilder(TableName.valueOf(myTableName))
            .setColumnFamily(colFamily)
            .build();

    boot.admin.createTable(des);
  }
}
