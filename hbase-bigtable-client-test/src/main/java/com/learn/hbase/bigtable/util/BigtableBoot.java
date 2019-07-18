package com.learn.hbase.bigtable.util;

import com.google.cloud.bigtable.config.BigtableOptions;
import com.google.cloud.bigtable.core.IBigtableDataClient;
import com.google.cloud.bigtable.core.IBigtableTableAdminClient;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.grpc.BigtableSession;
import com.google.cloud.bigtable.grpc.scanner.FlatRow;
import com.google.cloud.bigtable.grpc.scanner.ResultScanner;
import java.io.IOException;

public class BigtableBoot {
  private final BigtableSession session;
  public final IBigtableDataClient dataClient;
  public final IBigtableTableAdminClient adminClient;

  public BigtableBoot() throws IOException {
    BigtableOptions options =
        BigtableOptions.builder()
            .setProjectId("ignore")
            .setInstanceId("ignore")
            .setUserAgent("my0test")
            .setDataHost("localhost")
            .setAdminHost("localhost")
            .setPort(8086)
            .build();

    session = new BigtableSession(options);
    dataClient = session.getDataClientWrapper();
    adminClient = session.getTableAdminClientWrapper();
  }

  public void scanningTable() throws IOException {
    ResultScanner<FlatRow> scanner =
        dataClient.readFlatRows(
            Query.create("MyTestTable")
                .filter(
                    Filters.FILTERS
                        .timestamp()
                        .range()
                        .startClosed(1563000000000L)
                        .endClosed(1563999999999L)));
    printResult(scanner);
  }

  private void printResult(ResultScanner<FlatRow> rs) throws IOException {
    FlatRow fr = rs.next();
    while (fr != null) {
      System.out.println("RowKey: " + fr.getRowKey());
      for (FlatRow.Cell cell : fr.getCells()) {
        System.out.println(
            "\tFamily: "
                + cell.getFamily()
                + "\tQualifier: "
                + cell.getQualifier().toStringUtf8()
                + "\t Timestamp: "
                + cell.getTimestamp()
                + "\tValue:"
                + cell.getValue().toStringUtf8());
      }
      fr = rs.next();
    }
  }

  public static void main(String[] args) throws IOException {
    BigtableBoot boot = new BigtableBoot();
    boot.scanningTable();
  }

  @Override
  protected void finalize() throws Throwable {
    session.close();
  }
}
