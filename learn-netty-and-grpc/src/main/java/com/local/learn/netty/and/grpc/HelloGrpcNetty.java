package com.local.learn.netty.and.grpc;

import io.grpc.netty.shaded.io.netty.handler.ssl.OpenSsl;
import io.grpc.netty.shaded.io.netty.internal.tcnative.SSL;

public class HelloGrpcNetty {

  public static void main(String[] args){
    System.out.println(OpenSsl.isAvailable());
    ClassLoader loader = SSL.class.getClassLoader();

    System.out.println(loader.getResource("java.library.path"));
    System.out.println(loader.getParent());
  }
}
