package com.rahul.practice.hackerrank;

import java.util.Scanner;

/**
 * https://www.hackerrank.com/challenges/java-stack/problem?h_r=next-challenge&h_v=zen&h_r=next-challenge&h_v=zen
 */
// TODO: To be done
public class JavaStackProblem {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);

    while (sc.hasNext()) {
      String input = sc.next();

      int normal = 0, curly = 0, big = 0;

      System.out.println("input" + input);
      for (char ch : input.toCharArray()) {
        switch (ch) {
          case '(':
            normal++;
            break;
          case ')':
            normal--;
            break;
          case '{':
            curly++;
            break;
          case '}':
            curly--;
            break;
          case '[':
            big++;
            break;
          case ']':
            big--;
            break;
          default:
            break;
        }
      }

      System.out.println(normal == 0 && curly == 0 || big == 0);
    }
  }
}
