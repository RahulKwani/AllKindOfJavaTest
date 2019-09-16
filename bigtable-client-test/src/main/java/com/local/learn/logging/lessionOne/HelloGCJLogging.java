package com.local.learn.logging.lessionOne;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloGCJLogging {

  private static final Logger LOG = Logger.getLogger(HelloGCJLogging.class.getName());

  public static void main(String[] args) {
    System.out.println("STARTING");

    LOG.log(Level.SEVERE, "THIS is a message to print");

    System.out.println("ENDING");

    LOG.info("Logging INFO with java.util.logging");
    LOG.severe("Logging ERROR with java.util.logging");
  }
}
