package com.rahul.practice.hackerrank;

import java.util.Set;
import java.util.TreeSet;

public class HackerRankTest {

  public static void mainUsingSet(String[] args) {
    String s = "welcometojava";
    int k = 3;
    String smallest = "";
    String largest = "";

    Set<String> sortedSet = new TreeSet<>();
    for (int i = 0; i < s.length() - k + 1; i++) {
      sortedSet.add(s.substring(i, i + k));
    }
    int i = 0;
    for (String str : sortedSet) {
      i++;
      if ("".equals(smallest)) {
        smallest = str;
      }
      if (i == sortedSet.size()) {
        largest = str;
      }
    }
    System.out.println(smallest + "\n" + largest);

    // Jav
    // ToJ
    // ava
    // com
    // eTo
    // elc
    // lco
    // meT
    // oJa
    // ome
    // wel
  }

  public static void main(String[] args) {
    String s = "welcometojava";
    int k = 3;
    String smallest = "";
    String largest = "";

    smallest = s.substring(0, k);
    for (int i = 0; i < s.length() - k + 1; i++) {
      String current = s.substring(i, i + k);
      if (current.compareTo(smallest) <= 0) {
        smallest = current;
      }
      if (current.compareTo(largest) > 0) {
        largest = current;
      }
    }
    System.out.println(smallest + "\n" + largest);
  }
}
