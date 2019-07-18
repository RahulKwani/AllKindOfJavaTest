package com.learn.hbase.bigtable.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

public class MyUtil {
  private static Calendar date1 =
      new Calendar.Builder()
          .setFields(Calendar.YEAR, 2017, Calendar.MONTH, 7, Calendar.DATE, 20)
          .build();
  private static Calendar date2 =
      new Calendar.Builder()
          .setFields(Calendar.YEAR, 2019, Calendar.MONTH, 3, Calendar.DATE, 4)
          .build();
  private static Calendar date3 =
      new Calendar.Builder()
          .setFields(Calendar.YEAR, 1999, Calendar.MONTH, 10, Calendar.DATE, 14)
          .build();

  public static String ranName() {
    return "testTable-" + RandomStringUtils.randomAlphanumeric(10);
  }

  public static String rowkey() {
    return "colTime" + RandomStringUtils.randomAlphanumeric(5);
  }

  public static String familyName() {
    return "family" + RandomStringUtils.randomAlphabetic(5);
  }

  public static String qualifier() {
    return "qualifier" + RandomStringUtils.randomAlphanumeric(5);
  }

  public static byte[] value() {
    return RandomStringUtils.randomAlphabetic(10).getBytes();
  }

  public static long timeInLong() {
    int deviation = new Random().nextInt(100000);
    if (deviation % 4 == 0) {
      return date1.getTimeInMillis() - deviation;
    }
    switch (deviation % 4) {
      case 0:
        return date1.getTimeInMillis() - deviation;
      case 1:
        return date2.getTimeInMillis() - deviation;
      case 2:
        return date3.getTimeInMillis() - deviation;
      case 3:
        return date1.getTimeInMillis() - deviation - 100000L;
      default:
        return System.currentTimeMillis() - deviation;
    }
  }

  public static void printHBaseResScan(ResultScanner rs) throws IOException {
    Result result = rs.next();
    while (result != null) {
      System.out.println("Row: " + Bytes.toString(result.getRow()));
      for (Cell cell : result.rawCells()) {
        System.out.println(
            "\tFamily: "
                + Bytes.toString(cell.getFamilyArray())
                + "\tQualifier: "
                + Bytes.toString(cell.getQualifierArray())
                + "\t Timestamp: "
                + cell.getTimestamp()
                + "\tValue:"
                + Bytes.toString(cell.getValueArray()));
      }
      result = rs.next();
    }
  }
}
