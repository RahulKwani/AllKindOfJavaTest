package com.local.learn.kotlin

fun main(args: Array<String>){
    println("Hello World!!")
}

fun printName(args: String) : String{
    println("Hello $args");
    return "$args is learning Kotlin just fine";
}