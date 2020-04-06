package com.rahul.practice.hackerrank;

import java.util.regex.Pattern;

/**
 * https://www.hackerrank.com/challenges/pattern-syntax-checker/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
 */
public class PatternVerify {

  public static void main(String[] args) {
    verify("([A-Z])(.+)");
    verify("[AZ[a-z](a-z)");
    verify("batcatpat(nat");
  }

  private static void verify(String pattern) {
    try {
      Pattern.compile(pattern);
      System.out.println("Valid");
    } catch (Exception ex) {
      // ignored
      System.out.println("Invalid");
    }
  }
}
