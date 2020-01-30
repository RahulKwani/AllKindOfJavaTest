package com.investigate.random.stuff;

import static com.investigate.random.stuff.BaseConfiguration.COL_FAMILY;
import static com.investigate.random.stuff.BaseConfiguration.PROJECT_ID;
import static com.investigate.random.stuff.BaseConfiguration.TABLE_ID;

import com.google.api.gax.batching.Batcher;
import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.BigtableDataClientFactory;
import com.google.cloud.bigtable.data.v2.models.ConditionalRowMutation;
import com.google.cloud.bigtable.data.v2.models.KeyOffset;
import com.google.cloud.bigtable.data.v2.models.Mutation;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.ReadModifyWriteRow;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import com.google.cloud.bigtable.data.v2.models.RowMutationEntry;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.StopWatch;

public class BasicService {

  private static final Logger logger = Logger.getLogger(BasicService.class.getName());

  private final BigtableDataClientFactory factory;
  private final BigtableDataClient client;

  public BasicService(BigtableDataClientFactory factory, BigtableDataClient dataClient) {
    this.factory = factory;
    this.client = dataClient;
  }

  private static String VALUE = RandomStringUtils.random(200);

  public String ping() {
    String threadDump = generateThreadDump();
    print(threadDump);
    return threadDump;
  }

  public String testWithOneClient() throws Exception {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    readRows(client, null);
    print(generateThreadDump());

    readRows(client, null);
    print(generateThreadDump());

    readRows(client, null);
    print(generateThreadDump());

    readRows(client, null);
    print(generateThreadDump());

    readRows(client, null);
    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }

  public String testWithMultipleClient() throws Exception {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    BigtableDataClient bigtableTestClient =
        factory.createForInstance(PROJECT_ID, "bigtableio-test");
    readRows(bigtableTestClient, null);
    logger.info("bitableIO test client");

    BigtableDataClient enduranceClient = factory.createForInstance(PROJECT_ID, "endurance");
    readRows(enduranceClient, null);
    // enduranceClient.close();
    logger.info("endurance client end");

    BigtableDataClient laljiTestClient =
        factory.createForInstance(PROJECT_ID, "lalji-test-instance");
    readRows(laljiTestClient, null);
    // laljiTestClient.close();
    logger.info("lalji client");

    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }
  /*
  cbt -instance=gcloud-tests-instance-0675bdc0 createtable 'Hello-Bigtable'
  cbt -instance=gcloud-tests-instance-0675bdc0 createfamily 'Hello-Bigtable' cf1
  cbt -instance='gcloud-tests-instance-0675bdc0' set 'Hello-Bigtable' 'second-row'  'cf1':'first-column'='Welcome to the  Bigtable, You are in gcloud-tests-instance-0675bdc0 instance'
   */

  public String testWithSequential() {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    String rowKey = "TOn31Jan2020-" + RandomStringUtils.random(5);

    sampling(client);
    print("------------------ #1");
    print(generateThreadDump());

    mutateRow(client, rowKey);
    readRows(client, rowKey);
    print("------------------ #2");
    print(generateThreadDump());

    checkAndMutate(client, rowKey);
    readRows(client, rowKey);
    print("------------------ #3");
    print(generateThreadDump());

    readModifyWrite(client, rowKey);
    readRows(client, rowKey);
    print("------------------ #4");
    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }

  public String testWithParallel() throws InterruptedException {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    final ExecutorService EXECUTOR = Executors.newFixedThreadPool(8);
    final String rowKey = "TOn31Jan2020-" + RandomStringUtils.random(5);

    EXECUTOR.submit(
        new Callable() {
          @Override
          public Void call() {
            sampling(client);
            return null;
          }
        });

    EXECUTOR.submit(
        new Callable() {

          @Override
          public Void call() {
            mutateRow(client, rowKey);
            return null;
          }
        });

    EXECUTOR.submit(
        new Callable() {

          @Override
          public Void call() {
            readRows(client, rowKey);
            return null;
          }
        });
    EXECUTOR.submit(
        new Callable() {

          @Override
          public Void call() {
            checkAndMutate(client, rowKey);
            return null;
          }
        });
    EXECUTOR.submit(
        new Callable() {

          @Override
          public Void call() {
            logger.info("Again print");
            readRows(client, rowKey);
            readModifyWrite(client, rowKey);
            return null;
          }
        });

    EXECUTOR.awaitTermination(100, TimeUnit.SECONDS);
    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }

  public String testWithLongRunningOps() throws Exception {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    print(generateThreadDump());
    Future task1 =
        EXECUTOR.submit(
            new Callable() {
              @Override
              public Void call() {
                try {
                  batcher(client);
                } catch (Exception e) {
                  print("EXCEPTION in testWithLongRunningOps#batcher");
                  e.printStackTrace();
                }
                return null;
              }
            });

    Future task2 =
        EXECUTOR.submit(
            new Callable() {
              @Override
              public Void call() {
                try {
                  longRunningRead(client);
                } catch (Exception e) {
                  print("EXCEPTION in testWithLongRunningOps#longRunningRead");
                  e.printStackTrace();
                }
                return null;
              }
            });

    logger.info("task1.isDone(): " + task1.isDone());
    logger.info("task2.isDone(): " + task2.isDone());

    logger.info("Both tasks are executed");
    EXECUTOR.awaitTermination(100, TimeUnit.SECONDS);
    print("------POST - Ops ------");
    print(generateThreadDump());

    logger.info("task1.isDone(): " + task1.isDone());
    logger.info("task2.isDone(): " + task2.isDone());

    EXECUTOR.shutdown();
    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }

  public String testMultipleConn() throws Exception {
    logger.info("!------ START ------!");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    readRows(client, null);
    print("____________#####__________##1");
    print(generateThreadDump());

    final BigtableDataClient client2 = factory.createForInstance(PROJECT_ID, "bigtableio-test");
    readRows(client2, null);
    print("____________#####__________##2");
    print(generateThreadDump());

    final BigtableDataClient client3 = factory.createForInstance(PROJECT_ID, "endurance");
    readRows(client3, null);
    print("____________#####__________##3");
    print(generateThreadDump());

    final BigtableDataClient client4 = factory.createForInstance(PROJECT_ID, "lalji-test-instance");
    readRows(client4, null);
    print("____________#####__________##4");
    print(generateThreadDump());

    EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            readRows(client, null);
          }
        });
    EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            readRows(client2, null);
          }
        });
    EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            readRows(client3, null);
          }
        });
    EXECUTOR.execute(
        new Runnable() {
          @Override
          public void run() {
            readRows(client4, null);
          }
        });

    logger.info(generateThreadDump());

    logger.info("____________#####__________ AwaitTermination");
    EXECUTOR.awaitTermination(40, TimeUnit.SECONDS);
    logger.info(generateThreadDump());
    client2.close();
    client3.close();
    client4.close();
    String threadDump = generateThreadDump(stopWatch);
    print(threadDump);
    logger.info("!------ END ------!");
    return threadDump;
  }

  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------
  // --------------------------------------------------------------------------

  private static void readRows(BigtableDataClient client, String rowKey) {
    Query query = Query.create(TABLE_ID).limit(1);
    if (rowKey != null && !rowKey.isEmpty()) {
      query.rowKey(rowKey);
    }

    ServerStream<Row> stream = client.readRows(query);

    for (Row row : stream) {
      printRow(row);
    }
    logger.info("\t\t------");
  }

  private static void printRow(Row row) {
    String rowK = row.getKey().toStringUtf8();
    print("RowKey: " + rowK);
    for (RowCell rc : row.getCells()) {
      print(
          String.format(
              "\t Family: %s, Qualifier: %s, Value: %s\n",
              rc.getFamily(), rc.getQualifier(), rc.getValue()));
    }
  }

  private static void mutateRow(BigtableDataClient client, String rowKey) {
    String qualifier = RandomStringUtils.random(5);

    client.mutateRow(
        RowMutation.create(TABLE_ID, rowKey)
            .setCell(COL_FAMILY, qualifier, "This is temporary value"));
    print("\t\t------");
  }

  private static void sampling(BigtableDataClient client) {
    List<KeyOffset> offsetList = client.sampleRowKeys(TABLE_ID);
    for (KeyOffset keyOffset : offsetList) {
      print(keyOffset.getKey().toStringUtf8());
      print(keyOffset.toString());
    }
  }

  private static void checkAndMutate(BigtableDataClient client, String rowKey) {
    String qualifier = "new-qual-" + RandomStringUtils.random(5);
    client.checkAndMutateRow(
        ConditionalRowMutation.create(TABLE_ID, rowKey)
            .then(Mutation.create().setCell(COL_FAMILY, qualifier, "Added another value"))
            .otherwise(
                Mutation.create()
                    .setCell(
                        COL_FAMILY,
                        qualifier,
                        "Fresh Row added, this is first value in the table")));
    print("Done checkAndMutate");
  }

  private static void readModifyWrite(BigtableDataClient client, String rowKey) {
    client.readModifyWriteRow(
        ReadModifyWriteRow.create(TABLE_ID, rowKey)
            .append(
                COL_FAMILY,
                "I don't know this",
                "This is a value added with readModifyRow but I don't know what this does"));
    print("Done readModifyWrite");
  }

  private static void batcher(BigtableDataClient client) throws Exception {
    Batcher<RowMutationEntry, Void> batcher = client.newBulkMutationBatcher(TABLE_ID);
    for (int i = 0; i < 400; i++) {
      String rowKey = "TOn17Jan2020-" + RandomStringUtils.random(3);
      batcher.add(
          RowMutationEntry.create(rowKey)
              .setCell(COL_FAMILY, "qual-" + String.format("%08d", i), VALUE));

      if (i % 30 == 0) {
        TimeUnit.SECONDS.sleep(3);
        print("One Batch of Batching Completed");
      }
    }
  }

  private static void longRunningRead(BigtableDataClient client) throws Exception {
    ServerStream<Row> rows = client.readRows(Query.create(TABLE_ID).limit(200));
    int i = 0;
    for (Row row : rows) {
      logger.info("RowKey: " + row.getKey());

      i++;
      if (i % 100 == 0) {
        TimeUnit.SECONDS.sleep(2);
        print("One Batch of Read Completed");
      }
    }
  }

  static String generateThreadDump() {
    return generateThreadDump(null);
  }

  private static String generateThreadDump(@Nullable StopWatch stopWatch) {
    final StringBuilder dump = new StringBuilder();

    if (stopWatch != null) {
      stopWatch.stop();
      dump.append("TotalTimeTaken: ").append(stopWatch.getTotalTimeMillis()).append("\n");
    }

    final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    final ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 10);

    Arrays.stream(threadInfos)
        .sorted(Comparator.comparing(ThreadInfo::getThreadName))
        .forEach(
            thInfo ->
                dump.append('"')
                    .append(thInfo.getThreadName())
                    .append("\" ")
                    .append("\n   java.lang.Thread.State: ")
                    .append(thInfo.getThreadState())
                    .append("\n\n"));

    return dump.toString();
  }

  public static void print(String messageToLog) {
    logger.info(messageToLog);
  }
}
