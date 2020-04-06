package com.rahul.practice.hackerrank;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyRegex {

  static Pattern pattern =
      Pattern.compile(
          "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
              + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
              + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
              + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");

  public static void main(String[] args) {
    // 000.12.12.034
    // 121.234.12.12
    // 23.45.12.56
    // 00.12.123.123123.123
    // 122.23
    // Hello.IP

    System.out.println(check("000.012.012.034"));
    System.out.println(check("121.234.12.12"));
    System.out.println(check("23.045.12.56"));
    System.out.println(check("666.666.23.23"));
    System.out.println(check("00.12.123.123123.123"));
    System.out.println(check("122.23"));
    System.out.println(check("Hello.IP"));
  }

  private static Pattern patterna =
      Pattern.compile(
          "^(([01][0-9][0-9]|2[0-4][0-9]|25[0-5])+)"
              + ".(([01][0-9][0-9]|2[0-4][0-9]|25[0-5])+)"
              + ".(([01][0-9][0-9]|2[0-4][0-9]|25[0-5])+)"
              + ".(([01][0-9][0-9]|2[0-4][0-9]|25[0-5])+)$+");

  private static Pattern basePattern = Pattern.compile("(\\d+).(\\d+).(\\d+).(\\d+)");

  private static Pattern secondP =
      Pattern.compile(
          "\\b(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
              + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
              + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\."
              + "(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");

  private static boolean check(String ipStr) {
    Matcher ipMatcher = secondP.matcher(ipStr);
    return ipMatcher.matches();
    //    if (!ipMatcher.matches()) {
    //      return false;
    //    }
    //    for (int i = 1; i <= ipMatcher.groupCount(); i++) {
    //      int number = Integer.parseInt(ipMatcher.group(i));
    //      if (0 > number || number > 255) {
    //        return false;
    //      }
    //    }
    //    return true;
  }
}
