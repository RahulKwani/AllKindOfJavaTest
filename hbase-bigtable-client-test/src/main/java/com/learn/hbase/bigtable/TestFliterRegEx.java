package com.learn.hbase.bigtable;

import com.learn.hbase.bigtable.util.HBaseBoot;
import org.apache.hadoop.hbase.TableName;

public class TestFliterRegEx {

  private final HBaseBoot boot;

  TestFliterRegEx() throws Exception {
    boot = new HBaseBoot(true);
  }

  public static void main(String[] args) throws Exception {
    TestFliterRegEx regTest = new TestFliterRegEx();
    System.out.println(regTest.boot.admin.tableExists(TableName.valueOf("TEMP")));
  }
}
