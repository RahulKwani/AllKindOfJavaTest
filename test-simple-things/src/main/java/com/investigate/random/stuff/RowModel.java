package com.investigate.random.stuff;

import com.google.cloud.bigtable.data.v2.models.RowCell;
import com.google.protobuf.ByteString;
import java.util.List;

public class RowModel {
  private String rowKey;
  private List<RowCell> cells;

  public String getRowKey() {
    return rowKey;
  }

  public List<RowCell> getCells() {
    return cells;
  }

  public RowModel(ByteString rowKey, List<RowCell> cells) {
    if (rowKey != null) {
      this.rowKey = rowKey.toStringUtf8();
    }
    this.cells = cells;
  }
}
