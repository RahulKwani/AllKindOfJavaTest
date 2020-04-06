package com.rahul.practice.hackerrank;

import java.util.ArrayList;
import java.util.List;

/** https://www.hackerrank.com/challenges/java-string-tokens/problem?h_r=next-challenge&h_v=zen */
public class StringToken {

  public static void main(String[] args) {
    token("He is a very very good ?? boy, isn't he?");
  }

  private static void token(String str) {

    String[] arr = str.trim().split("\\s|!|,|a-zA-Z|\\_|'|@|\\?|\\.");

    List<String> finalList = new ArrayList<>();
    for (String s : arr) {
      if (!"".equals(s)) {
        finalList.add(s);
      }
    }
    System.out.println(finalList.size());
    finalList.forEach(System.out::println);
  }
}
