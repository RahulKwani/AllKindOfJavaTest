package com.rahul.practice.hackerrank;

import java.util.Scanner;

/**
 * https://www.hackerrank.com/challenges/java-negative-subarray/problem
 *
 * <p>1 -2 4 -5 1
 *
 * <p>-2, -5, -1, -1, -4, -3, -2, -2, -1
 */
public class SubArray {

  private static int[] arr = {1, -2, 4, -5, 1};

  public static void main(String[] args) {

    Scanner scan = new Scanner(System.in);
    int len = scan.nextInt();

    int[] arr = new int[len];
    for (int i = 0; i < len; i++) {
      arr[i] = scan.nextInt();
    }
    for (int i = 0; i < len; i++) {
      System.out.println(arr[i]);
    }

    // arr = {1, -2, 4, -5, 1};

    int negCount = 0;
    for (int i = 0; i < arr.length; i++) {
      System.out.println("\n== " + i);

      for (int j = 0; j < arr.length - i; j++) {
        int sum = 0;
        for (int k = 0; k <= i; k++) {
          int index = k + j;
          System.out.print("arr[" + index + "]: " + arr[index] + " ");
          sum += arr[index];
        }
        if (sum < 0) {
          negCount++;
        }
        System.out.println(" ==> " + sum);
      }
      System.out.println("NAC: " + negCount);
    }

    // Submitted solution
    //        int negCount = 0;
    //        for (int i = 0; i < arr.length; i++) {
    //
    //          for (int j = 0; j < arr.length - i; j++) {
    //            int sum = 0;
    //
    //            for (int k = 0; k <= i; k++) {
    //              sum += arr[k + j];
    //            }
    //            if (sum < 0) {
    //              negCount++;
    //            }
    //          }
    //        }
    //        System.out.println(negCount);

    /*
      == 0
      arr[0];
      arr[1];
      arr[2];
      arr[3];
      arr[4];

      == 1
      arr[0], arr[1];
      arr[1], arr[2];
      arr[2], arr[3];
      arr[3], arr[4];

      == 2
      0 arr[0], arr[1], arr[2];
      1 arr[1], arr[2], arr[3];
      2 arr[2], arr[3], arr[4];

      == 3
      arr[0], arr[1], arr[2], arr[3];
      arr[1], arr[2], arr[3], arr[4];

      == 4
      arr[0], arr[1], arr[2], arr[3], arr[4];
    */
  }
}
