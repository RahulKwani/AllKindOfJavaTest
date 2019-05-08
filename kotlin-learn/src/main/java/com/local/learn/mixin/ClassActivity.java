package com.local.learn.mixin;

import com.local.learn.kotlin.HelloWorldKt;

public class ClassActivity {

  public static void main(String[] args) {
    String out = HelloWorldKt.printName("Rahul");
    System.out.println(out);
  }
}
