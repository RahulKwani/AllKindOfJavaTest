package com.rahul.practice.hackerrank;

public class Two2DArrayProb {

  private static int[][] arr = {
    {1, 1, 1, 0, 0, 0},
    {0, 1, 0, 0, 0, 0},
    {1, 1, 1, 0, 0, 0},
    {0, 0, 2, 4, 4, 0},
    {0, 0, 0, 2, 0, 0},
    {0, 0, 1, 2, 4, 0}
  };

  /**
   *
   *
   * <ul>
   *   <li>7, 4, 2, 0
   *   <li>4, 8, 10, 8
   *   <li>3, 6, 7, 6
   *   <li>3, 9, 19, 14
   * </ul>
   */
  public static void main(String[] args) {

    int heighest = -1000;
    for (int i = 0; i < arr.length - 2; i++) {
      for (int j = 0; j < arr[i].length - 2; j++) {
        int first = arr[i][j] + arr[i][j + 1] + arr[i][j + 2];
        int second = arr[i + 1][j + 1];
        int third = arr[i + 2][j] + arr[i + 2][j + 1] + arr[i + 2][j + 2];

        int totalSum = first + second + third;
        if (totalSum > heighest) {
          heighest = totalSum;
        }
      }
    }
    System.out.println(heighest);

    System.out.println("print ---- \n");
    //    for (int i = 0; i < sumOfArr.length; i++) {
    //      for (int j = 0; j < sumOfArr[i].length; j++) {
    //        System.out.print(sumOfArr[i][j] + ", ");
    //      }
    //      System.out.println();
    //    }
  }
}
