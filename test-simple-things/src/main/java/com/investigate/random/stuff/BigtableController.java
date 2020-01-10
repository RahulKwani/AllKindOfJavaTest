package com.investigate.random.stuff;

import static com.investigate.random.stuff.BaseConfiguration.TABLE_ID;

import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BigtableController {
  private static final Logger logger = Logger.getLogger(BigtableController.class.getName());

  @Autowired private BigtableDataClient client;

  @GetMapping("status")
  public Boolean getAppStatus() {
    logger.info("Application is up and running!!!");
    return Boolean.TRUE;
  }

  @GetMapping("printThreadDump")
  public void printThreadDump() {
    logger.info(" <------- Threads Dump -------> ");
    logger.info("No of processors: " + Runtime.getRuntime().availableProcessors());

    logger.info(generateThreadDump());
  }

  @GetMapping("/rows/{num}")
  public List<RowModel> fetchSomeRows(@PathVariable("num") Integer num) {
    List<RowModel> models = new ArrayList<>();
    for (Row row : client.readRows(Query.create(TABLE_ID).limit(num))) {
      models.add(new RowModel(row.getKey(), row.getCells()));
    }
    return models;
  }

  private static String generateThreadDump() {
    final StringBuilder dump = new StringBuilder();
    final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    final ThreadInfo[] threadInfos =
        threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
    for (ThreadInfo threadInfo : threadInfos) {
      dump.append('"');
      dump.append(threadInfo.getThreadName());
      dump.append("\" ");
      final Thread.State state = threadInfo.getThreadState();
      dump.append("\n   java.lang.Thread.State: ");
      dump.append(state);
      final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
      for (final StackTraceElement stackTraceElement : stackTraceElements) {
        dump.append("\n        at ");
        dump.append(stackTraceElement);
      }
      dump.append("\n\n");
    }
    return dump.toString();
  }
}
