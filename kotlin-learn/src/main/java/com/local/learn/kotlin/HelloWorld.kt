package com.local.learn.kotlin

import java.util.concurrent.TimeUnit

fun main(args: Array<String>){
    println("Hello World!!")
    println(System.nanoTime())
    val time:Long = TimeUnit.SECONDS.toNanos(1);
    println(time);
    println(TimeUnit.MILLISECONDS.toSeconds(1_000_000_000))
}

fun printName(args: String) : String{
    println("Hello $args");
    return "$args is learning Kotlin just fine";
}