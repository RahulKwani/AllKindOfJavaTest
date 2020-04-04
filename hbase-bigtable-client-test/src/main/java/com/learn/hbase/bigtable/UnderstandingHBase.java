package com.learn.hbase.bigtable;

import com.google.common.collect.ImmutableList;
import com.learn.hbase.bigtable.util.HBaseBoot;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

public class UnderstandingHBase {

  private final HBaseBoot client;

  UnderstandingHBase() throws Exception {
    client = new HBaseBoot(62818);
    //    client = new HBaseBoot("grass-clump-479", "connectors");
  }

  //  private void learnSnapshots(String pattern) throws Exception {
  //    System.out.println("LEARN snapshots");
  //
  //    try (Admin admin = client.connection.getAdmin()) {
  //      try {
  //        System.out.println("admin.listSnapshots");
  //        int listCount = 0;
  //        for (HBaseProtos.SnapshotDescription description : admin.listSnapshots(pattern)) {
  //          System.out.println(description);
  //          listCount++;
  //        }
  //        System.out.println("TOTAL:===> " + listCount);
  //
  //      } catch (Exception ex) {
  //        ex.printStackTrace(System.out);
  //      }
  //
  //      try {
  //        System.out.println("admin.listTableSnapshots");
  //        int listCount = 0;
  //        for (HBaseProtos.SnapshotDescription description :
  //            admin.listTableSnapshots(pattern, pattern)) {
  //          System.out.println(description);
  //          listCount++;
  //        }
  //        System.out.println("TOTAL:===> " + listCount);
  //
  //      } catch (Exception ex) {
  //        ex.printStackTrace(System.out);
  //      }
  //
  //      try {
  //        System.out.println("admin.deleteSnapshots:===> ");
  //        admin.deleteSnapshots(pattern);
  //        System.out.println("End");
  //
  //      } catch (Exception ex) {
  //        ex.printStackTrace(System.out);
  //      }
  //
  //      try {
  //        System.out.println("admin.deleteSnapshots:===> ");
  //        admin.deleteTableSnapshots(pattern, pattern);
  //        System.out.println("End");
  //      } catch (Exception ex) {
  //        ex.printStackTrace(System.out);
  //      }
  //    }
  //  }

  //  private void learnSnapshots2() throws Exception {
  //    try (Admin admin = client.connection.getAdmin()) {
  //      try {
  //        System.out.println("admin.listSnapshots");
  //        int listCount = 0;
  //        for (HBaseProtos.SnapshotDescription description :
  //            admin.listTableSnapshots("My-Table-tSGO6xPghd", "")) {
  //          System.out.println(description);
  //          listCount++;
  //        }
  //        System.out.println("TOTAL:===> " + listCount);
  //
  //      } catch (Exception ex) {
  //        ex.printStackTrace(System.out);
  //      }
  //    }
  //  }

  private void learnScanner(TableName tableName) throws Exception {
    System.out.println("Learn Scanner");
    try (Table table = client.connection.getTable(tableName)) {
      if (!client.admin.tableExists(tableName)) {
        HBaseBoot.createTable(client.admin, tableName.getNameAsString(), "cf1", "cf2", "cf3");

        ColumnTimeRangeLearn.addFiveCols(table, "rowkey1");
        ColumnTimeRangeLearn.addFiveCols(table, "rowkey2");
        ColumnTimeRangeLearn.addFiveCols(table, "rowkey3");
      }

      Result[] res = table.get(ImmutableList.of());
      System.out.println("RESULT Of exists:" + Arrays.toString(res));
      System.out.println("RESULT Of exists:" + res.length);

      table.put(ImmutableList.of());
      Scan scan = new Scan();
      ResultScanner scanner = table.getScanner(scan);
      ColumnTimeRangeLearn.printResultScanner(scanner);
    }
  }

  //  private void learnAsyncTable(TableName tableName) throws Exception {
  //    CompletableFuture<AsyncConnection> ac =
  //        ConnectionFactory.createAsyncConnection(client.connection.getConfiguration());
  //    //    ac.thenApply(c -> c.getTable(tableName))
  //    //        .thenApply(
  //    //            at -> {
  //    //              try {
  //    //                System.out.println("Started mutation");
  //    //                return at.mutateRow(new RowMutations(new byte[1])).get();
  //    //              } catch (InterruptedException | ExecutionException e) {
  //    //                throw new RuntimeException(e);
  //    //              }
  //    //            })
  //    //        .thenAccept(
  //    //            end -> {
  //    //              System.out.println("Result is this");
  //    //            })
  //    //        .get();
  //    ac.thenApply(c -> c.getTable(tableName))
  //        .thenApply(this::asyncScan)
  //        .thenAccept(
  //            x -> {
  //              System.out.println("END");
  //              printLine("END=2");
  //            })
  //        .get();
  //  }
  //
  //  private CompletableFuture<Void> asyncScan(AsyncTable<AdvancedScanResultConsumer> asyncTable) {
  //    SettableFuture future = SettableFuture.create();
  //    AtomicInteger counter = new AtomicInteger();
  //    asyncTable.scan(
  //        null,
  //        new AdvancedScanResultConsumer() {
  //          @Override
  //          public void onError(Throwable error) {
  //            printLine("onError");
  //            error.printStackTrace(System.out);
  //            future.setException(error);
  //          }
  //
  //          @Override
  //          public void onComplete() {
  //            future.set(null);
  //            printLine("onComplete");
  //          }
  //
  //          @Override
  //          public void onNext(Result[] results, ScanController controller) {
  //
  //            printLine("counter.incrementAndGet() " + counter.incrementAndGet());
  //
  //            printLine("onNext() has got--> " + results.length);
  //            for (Result res : results) {
  //              printLine("Result: " + Bytes.toString(res.getRow()));
  //            }
  //          }
  //        });
  //    System.out.println("waIting");
  //    try {
  //      future.get();
  //    } catch (InterruptedException | ExecutionException e) {
  //      throw new RuntimeException(e);
  //    }
  //    System.out.println("FINISHEd");
  //    return CompletableFuture.completedFuture(null);
  //  }

  private static void printLine(String content) {
    System.out.println(content);
  }

  private void learnTable(TableName tableName) throws Exception {
    Table table = client.connection.getTable(tableName);

    System.out.println("with Object[0]");
    table.mutateRow(new RowMutations(new byte[0]));

    System.out.println("with Object[0]");
    table.batch(ImmutableList.<Row>of(), new Object[0]);

    System.out.println("with Object[1]");
    table.batch(ImmutableList.<Row>of(), new Object[1]);

    System.out.println("with put((List<Put>) null);");
    table.put((List<Put>) null);

    System.out.println("with table.get((List<Get>) null);");
    table.get((List<Get>) null);

    System.out.println("FINISHED");
  }

  public static void main(String[] args) throws Exception {
    UnderstandingHBase hBase = new UnderstandingHBase();
    //    hBase.learnScanner(TableName.valueOf("My-Table-ZTtQKL9Imy"));
    //  hBase.learnAsyncTable(TableName.valueOf("My-Table-ZTtQKL9Imy"));
    //    hBase.learnTable(TableName.valueOf("My-Table-ZTtQKL9Imy"));

    //
    //    Pattern p = null;
    //    hBase1.client.admin.deleteTableSnapshots("My-Table-ZTtQKL9Imy", "");
    //
    //    hBase1.learnSnapshots("");

    //    hBase.client.admin.getTableDescriptor(TableName.valueOf("My-Table-ZTtQKL"));
  }
}
