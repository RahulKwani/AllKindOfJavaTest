package com.rahul.practice.hackerrank;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/** https://www.hackerrank.com/challenges/phone-book/problem?h_r=next-challenge&h_v=zens */
public class JavaMapProblem {

  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    int n = in.nextInt();
    in.nextLine();
    Map<String, Integer> map = new HashMap<>();
    for (int i = 0; i < n; i++) {
      String name = in.nextLine();
      int phone = in.nextInt();
      in.nextLine();
      map.put(name, phone);
    }
    System.out.println(map.toString());
    while (in.hasNext()) {
      String s = in.nextLine();
      if (map.containsKey(s)) {
        System.out.println(s + "=" + map.get(s));
      } else {
        System.out.println("Not found");
      }
    }
    in.close();
  }
}
