package com.rahul.practice.hackerrank;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** https://www.hackerrank.com/challenges/java-list/problem */
public class JavaListProblem {

  public static void main(String[] args) {
    try (Scanner in = new Scanner(System.in)) {
      int size = in.nextInt();

      List<Integer> numbers = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        numbers.add(in.nextInt());
      }

      int ops = in.nextInt();
      for (int i = 0; i < ops; i++) {
        String type = in.next();
        if ("Insert".equals(type)) {
          numbers.add(in.nextInt(), in.nextInt());
        } else if ("Delete".equals(type)) {
          numbers.remove(in.nextInt());
        }
      }

      System.out.println(numbers.toString());
    }
  }
}
