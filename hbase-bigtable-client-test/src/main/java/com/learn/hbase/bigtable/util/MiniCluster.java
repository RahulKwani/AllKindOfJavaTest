package com.learn.hbase.bigtable.util;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;

public class MiniCluster {

  public final MiniHBaseCluster miniClus;

  MiniCluster() throws Exception {
    HBaseTestingUtility helper = new HBaseTestingUtility();
    miniClus = helper.startMiniCluster();
  }

  public Configuration getConfiguration() {
    return miniClus.getConfiguration();
  }

  public static void main(String[] args) throws Exception {
    HBaseTestingUtility helper = new HBaseTestingUtility();
    MiniHBaseCluster hbase = helper.startMiniCluster();

    System.out.println("____________________");
    System.out.println("###########@@@@@@@@@@");
    System.out.println(hbase.getConfiguration());
    System.out.println(hbase.getConfiguration().get("hbase.zookeeper.quorum"));
    System.out.println(hbase.getConfiguration().get("hbase.zookeeper.property.clientPorthbase.zookeeper.property.clientPort"));
  }
}
