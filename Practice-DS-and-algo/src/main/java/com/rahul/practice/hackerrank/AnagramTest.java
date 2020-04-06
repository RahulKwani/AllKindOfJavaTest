package com.rahul.practice.hackerrank;

/** https://www.hackerrank.com/challenges/java-anagrams/problem */
public class AnagramTest {

  public static void main(String[] args) {
    String a = "anagram";
    String b = "margana";

    System.out.println(isAnagram(a, b));
    System.out.println(isAnagram("Hello", "hello"));
    System.out.println(isAnagram("Aa", "aA"));
    System.out.println(isAnagram("elhlo", "hello"));
  }

  private static boolean isAnagram(String a, String b) {
    String A = "Anagrams";
    String NA = "Not Anagrams";

    if (a.length() != b.length()) {
      System.out.println(NA);
      return false;
    }

    int[] aAC = new int[26];
    for (int i = 0; i < a.length(); i++) {
      int ch = a.substring(i, i + 1).toLowerCase().charAt(0);
      aAC[ch - 97]++;
    }

    int[] bAC = new int[26];
    for (int i = 0; i < b.length(); i++) {
      int ch = b.substring(i, i + 1).toLowerCase().charAt(0);
      bAC[ch - 97]++;
    }

    for (int i = 0; i < aAC.length; i++) {
      if (aAC[i] != bAC[i]) {
        System.out.println(NA);
        return false;
      }
    }

    System.out.println(A);
    return true;
  }

  private void sortArr(int[] myArr) {}
}
