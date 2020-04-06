package com.rahul.practice.hackerrank;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** https://www.hackerrank.com/challenges/java-arraylist/problem */
public class ArrayListProblem {

  public static void main(String[] args) {
    Scanner scan = new Scanner(System.in);

    List<List<Integer>> list = new ArrayList<>();
    while (scan.hasNext()) {
      String[] numArr = scan.nextLine().split("\\s");
      if (numArr.length == 0) {
        continue;
      }
      List<Integer> numbers = new ArrayList<>();
      for (String numStr : numArr) {
        if (!"".equals(numStr)) {
          numbers.add(Integer.parseInt(numStr));
        }
      }

      list.add(numbers);
    }

    int totalNumber = list.get(0).get(0);

    for (int i = totalNumber + 2; i < list.size(); i++) {
      List<Integer> str = list.get(i);

      try {
        Integer value = list.get(str.get(0)).get(str.get(1));
        System.out.println(value);
      } catch (Exception ex) {
        System.out.println("ERROR!");
      }
    }
  }

  public static void moreOptimized(String[] args) {
    Scanner scan = new Scanner(System.in);
  }
}
