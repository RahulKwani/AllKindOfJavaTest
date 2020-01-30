package com.investigate.random.stuff;

import static com.investigate.random.stuff.BaseConfiguration.TABLE_ID;
import static com.investigate.random.stuff.BasicService.generateThreadDump;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BigtableController {

  private static final Logger LOGGER = Logger.getLogger(BigtableController.class.getName());

  @Autowired private BigtableDataClient dataClient;

  @Autowired private BasicService service;

  @GetMapping("status")
  public Boolean getAppStatus() {
    LOGGER.info("Application is up and running!!!");
    return Boolean.TRUE;
  }

  @GetMapping("cpus")
  public long availableProcessors() {
    LOGGER.info("No of processors: " + Runtime.getRuntime().availableProcessors());
    return Runtime.getRuntime().availableProcessors();
  }

  @GetMapping("threadDump")
  public String printThreadDump() {
    LOGGER.info(" <------- Threads Dump -------> ");
    LOGGER.info("No of processors: " + Runtime.getRuntime().availableProcessors());

    String threadDump = generateThreadDump();
    LOGGER.info(threadDump);
    return threadDump;
  }

  @GetMapping("/rows/{num}")
  public List<RowModel> fetchSomeRows(@PathVariable("num") Integer num) {
    List<RowModel> models = new ArrayList<>();

    for (Row row : dataClient.readRows(Query.create(TABLE_ID).limit(num))) {
      models.add(new RowModel(row.getKey(), row.getCells()));
    }
    return models;
  }

  @GetMapping("/confirmService")
  public String confirmService() {
    return service.ping();
  }

  @GetMapping("/testWithOneClient")
  public String testWithOneClient() throws Exception {
    return service.testWithOneClient();
  }

  @GetMapping("/testWithSequential")
  public String testWithSequential() throws Exception {
    return service.testWithSequential();
  }

  @GetMapping("/testWithParallel")
  public String testWithParallel() throws Exception {
    return service.testWithParallel();
  }

  @GetMapping("/testMultipleConn")
  public String testMultipleConn() throws Exception {
    return service.testMultipleConn();
  }

  @GetMapping("/testWithLongRunningOps")
  public String testWithLongRunningOps() throws Exception {
    return service.testWithLongRunningOps();
  }

  @GetMapping("/testWithMultipleClient")
  public String testWithMultipleClient() throws Exception {
    return service.testWithMultipleClient();
  }
}
