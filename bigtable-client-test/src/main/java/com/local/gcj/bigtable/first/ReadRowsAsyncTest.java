package com.local.gcj.bigtable.first;

import com.google.api.gax.rpc.ServerStream;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.local.gcj.bigtable.GCJBigtableInit;
import java.io.IOException;

public class ReadRowsAsyncTest {

  public static void main(String[] args) throws IOException {
    GCJBigtableInit conn = new GCJBigtableInit();
    Query request = Query
        .create("AppEngineTestTable")
        .rowKey("first-key")
        .rowKey("test-row")
        .rowKey("another-row");


    ServerStream<Row> stream = conn.getDataClient().readRows(request);
    stream.forEach(x -> {
      System.out.println("RowKey " + x.getKey().toStringUtf8());

      x.getCells().forEach(c->{
        System.out.printf("    family: %s, Qualifier: %s, Value: %s\n",
            c.getFamily(), c.getQualifier(), c.getValue());
      });
    });
  }
}
