package com.local.learn.pubsub.lessionOne;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Temp {

  public static void main(String[] args) throws Exception {

    ExecutorService service = Executors.newSingleThreadExecutor();

    service.submit(new WorkerOne());
    service.submit(new WorkerTwo());
    //    service.invokeAll(ImmutableList.of(new WorkerOne(), new WorkerT));
    service.shutdown();
    service.awaitTermination(30, TimeUnit.SECONDS);
  }

  static class WorkerOne extends Thread {
    @Override
    public void run() {
      try {
        TimeUnit.SECONDS.sleep(5);
        IntStream.range(0, 10).forEach(System.out::println);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  static class WorkerTwo extends Thread {
    @Override
    public void run() {

      ExecutorService service = Executors.newSingleThreadExecutor();
      service.submit(
          () -> {
            try {
              TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            System.out.println("callable call");
          });
      try {
        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(5);
        IntStream.range(20, 30).forEach(System.out::println);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
