package com.local.gcj.bigtable.first;

import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.ServerStream;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Filters;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.common.annotations.VisibleForTesting;
import com.local.gcj.bigtable.GCJBigtableInit;

public class ReadRowsAsyncTest {

  public static void main(String[] args) throws Throwable {
    GCJBigtableInit conn = new GCJBigtableInit();

    class ClientObserver implements ResponseObserver<Row> {
      private StreamController streamController;

      public void onStart(StreamController streamController) {
        this.streamController = streamController;
        // Other initialization
      }

      public void onResponse(Row row) {
        // Do something with Row
      }

      public void onError(Throwable t) {
        if (t instanceof NotFoundException) {
          System.out.println("Tried to read a non-existent table");
        } else {
          t.printStackTrace();
        }
      }

      public void onComplete() {
        // Handle stream completion
      }

      public void cancel() {
        streamController.cancel(); // Stream will be cancelled now.
      }
    }

    try (BigtableDataClient client = BigtableDataClient.create("[PROJECT]", "[INSTANCE]")) {
      Query request =
          Query.create("[TABLE]")
              .range("[START KEY]", "[END KEY]")
              .filter(Filters.FILTERS.qualifier().regex("[COLUMN PREFIX]."));

      ClientObserver observer = new ClientObserver();

      client.readRowsAsync(request, observer);

      // Cancels the stream.
      observer.cancel();
    }
  }

  @VisibleForTesting
  static void printRow(Row row) {
    System.out.println("RowKey " + row.getKey().toStringUtf8());
    row.getCells()
        .forEach(
            c -> {
              System.out.printf(
                  "    family: %s, Qualifier: %s, Value: %s\n",
                  c.getFamily(), c.getQualifier(), c.getValue());
            });
  }

  public static void test() throws Exception {
    try (BigtableDataClient bigtableDataClient =
        BigtableDataClient.create("[PROJECT]", "[INSTANCE]")) {
      String tableId = "[TABLE]";

      Query query =
          Query.create(tableId)
              .range("[START KEY]", "[END KEY]")
              .filter(Filters.FILTERS.qualifier().regex("[COLUMN PREFIX]."));

      // Iterator style
      try {
        ServerStream<Row> stream = bigtableDataClient.readRows(query);
        int count = 0;
        for (Row row : stream) {
          if (++count > 10) {
            stream.cancel();
            break;
          }
          // Do something with row
        }
      } catch (NotFoundException e) {
        System.out.println("Tried to read a non-existent table");
      } catch (RuntimeException e) {
        e.printStackTrace();
      }
    }
  }
}
