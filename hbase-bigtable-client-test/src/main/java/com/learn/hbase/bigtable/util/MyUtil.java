package com.learn.hbase.bigtable.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.TableName;
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

  public static void foo(String randomString) {
    if (isBadTableName(randomString)) {
      try {
        createTable("a" + randomString);
        System.out.println("NOT a BadName" + randomString);
        return;
      } catch (Exception e) {
         //System.out.println("Bad name: " + randomString);
      }
    } else {
      System.out.println("Not a isBadTableName: " + randomString);
    }
  }

  public static void fixedFoo(String randomString) {
    if (isBadTableName2(randomString)) {
      try {
        createTable("a" + randomString);
        System.out.println("Fixed --->NOT a BadName2" + randomString);
        return;
      } catch (Exception e) {
         //System.out.println("Fixed --->Bad name2: " + randomString);
      }
    } else {
      System.out.println("Fixed ---> Not a isBadTableName2: " + randomString);
    }
  }

  public static void main(String[] args) {

    for (int i = 0; i < 200; i++) {
      String randomString = RandomStringUtils.random(10, false, false);
      foo(randomString);
      fixedFoo(randomString);
    }

    //    String test = "a釋妢醜婮㳯뺲䂆㾨柮脻";
    //    testArg(test);
    /*
    aᐰ眼豤皳塇趦䜸궁酡㯉
    isBadTableName

    a먔倔芘滽熬단ᨶ炴晝펆
    isBadTableName
    --
    a剸쐪䠗츹趜䙰㨤ᗠ海룒

    a䐖襃퉵ꖻ롾ﻱ瘈緸캧㯐

    a䐖襃퉵ꖻ롾ﻱ瘈緸캧㯐

    a䐖襃퉵ꖻ롾ﻱ瘈緸캧㯐

    a䎲ᗄ膰ර栤柨쌼傸捐狤

    a楴䘀ሳ鲞텍暲桛䱎熅蘫
    a啘慾ްᨮ팠硰ᒑ䢱䥪帶
    a箈跑ꡗ솼巤杦囹琿臽膢

    a梦紧㟒벳ყ珪끵喷厨럿
    aӦᕦꚩ厂珣ǽ짗䎲㷻膈

     */

    //    aك氚步ꇠ굃墡杖汷⋞暚
  }

  private static boolean isBadTableName2(String tableName) {
    for (int i = 0; i < tableName.length(); ++i) {
      char c = tableName.charAt(i);
      if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_' && c != '-' && c != '.') {
        return true;
      }
    }
    return false;
  }

  private static void createTable(String badName) {
    TableName tableName = TableName.valueOf(badName);
  }

  protected static boolean isBadTableName(String tableName) {
    byte[] tableChars = tableName.getBytes();
    for (byte c : tableChars) {
      if (!Character.isAlphabetic(c) && !Character.isDigit(c) && c != '_' && c != '-' && c != '.') {
        return true;
      }
    }
    return false;
  }

  public static boolean isNotALegalTableQualifierName(byte[] qualifierName) {
    int start = 0;
    int end = qualifierName.length;
    if (end - start < 1) {
      return true;
    } else if (qualifierName[start] != 46 && qualifierName[start] != 45) {
      String qualifierString =
          new String(qualifierName, start, end - start, StandardCharsets.UTF_8);
      if (qualifierString.equals("zookeeper")) {
        return true;
      } else {
        for (int i = 0; i < qualifierString.length(); ++i) {
          char c = qualifierString.charAt(i);
          if (!Character.isAlphabetic(c)
              && !Character.isDigit(c)
              && c != '_'
              && c != '-'
              && c != '.') {
            return true;
          }
        }
      }
    } else {
      return true;
    }
    return false;
  }
}
