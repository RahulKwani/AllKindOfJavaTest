package com.local.gcj.bigtable;

import com.google.cloud.bigtable.admin.v2.BigtableTableAdminClient;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import java.io.IOException;

public class GCJBigtableInit {
  private static final String PROJECT_ID = "grass-clump-479";
  private static final String INSTANCE_ID = "shared-perf-2";

  private BigtableTableAdminClient adminClient;
  private final BigtableDataClient dataClient;

  public GCJBigtableInit() throws IOException {
    dataClient = BigtableDataClient.create(PROJECT_ID, INSTANCE_ID);
  }

  public BigtableDataClient getDataClient(){
    return dataClient;
  }

  public BigtableTableAdminClient getAdminClient(){
    if(adminClient == null){
      try {
        adminClient = BigtableTableAdminClient.create(PROJECT_ID, INSTANCE_ID);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return adminClient;
  }

}
