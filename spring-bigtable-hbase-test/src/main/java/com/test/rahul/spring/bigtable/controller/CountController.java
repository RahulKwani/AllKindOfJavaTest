package com.test.rahul.spring.bigtable.controller;

import com.test.rahul.spring.bigtable.dao.BigtableConnectionDao;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.hbase.TableName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountController {

  private static final Logger LOG = LoggerFactory.getLogger(CountController.class);
  private static final TableName tableName = TableName.valueOf("UnderstandMetricsTestTable");

  @Autowired private BigtableConnectionDao dao;

  @GetMapping
  public String rootAccess() {
    return "This is root of this app";
  }

  @GetMapping("Hi/{n}")
  public MyBean sayHi(@PathVariable(name = "n", required = true) String name) {
    return new MyBean(new Random().nextInt(), name);
  }

  @GetMapping("Hello")
  public MyBean sayHello(@RequestParam("name") String name) {
    return new MyBean(new Random().nextInt(), name);
  }

  @GetMapping("bigtable/{tableId}")
  public boolean bigtableTableExisted(@PathVariable("tableId") String tableId) {
    try {
      return dao.isTableExist(tableId);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  @GetMapping("bigtable/{tableId}/{keyPrefix}")
  public int getCountOfKey(
      @PathVariable("tableId") String tableId, @PathVariable("keyPrefix") String keyPrefix) {
    try {
      return dao.getCount(tableId, keyPrefix);
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

  @GetMapping("/putValues/{rowKey}")
  public void putValues(@PathVariable("rowKey") String rowKey) throws IOException {
    dao.addRowsAndCols(tableName, rowKey, 3);
  }

  @GetMapping("/scan/{rowKey}")
  public List<String> getScanner(@PathVariable("rowKey") String rowKey) throws IOException {
    return dao.scanTable(tableName, rowKey);
  }

  @GetMapping("/check/channelPool/{tableId}")
  public boolean checkChannelPool(@PathVariable("tableId") String tableId) throws IOException {
    return dao.checkChannelPool(TableName.valueOf(tableId));
  }

  @GetMapping("/batch/{times}")
  public void batchWithTypeAndTimes(@PathVariable("times") Integer times)
      throws IOException, InterruptedException {
    dao.sendBatch(tableName, times);
  }

  static class MyBean {
    private Integer id;
    private String name;

    MyBean() {}

    MyBean(Integer id, String name) {
      this.id = id;
      this.name = name;
    }

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
