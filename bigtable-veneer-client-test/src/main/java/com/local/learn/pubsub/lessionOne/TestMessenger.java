package com.local.learn.pubsub.lessionOne;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class TestMessenger {

  private static final Logger LOG = Logger.getLogger(TestMessenger.class.getName());
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

  public static void main(String... args) throws Exception {
    final String topicName = args[0];
    final int messageCount = Integer.parseInt(args[1]);
    final String messagePrefix = args[2];
    Publisher publisher = null;
    List<ApiFuture<String>> futures = new ArrayList<>();

    try {
      publisher = Publisher.newBuilder(ProjectTopicName.of(PROJECT_ID, topicName)).build();

      for (int i = 0; i < messageCount; i++) {

        String message = messagePrefix + i;
        LOG.info("Message Data: " + message);

        ApiFuture<String> resultFuture =
            publisher.publish(
                PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).build());
        futures.add(resultFuture);
      }
    } finally {
      // Wait on any pending requests
      List<String> messageIds = ApiFutures.allAsList(futures).get();

      LOG.info("successfully sent messages in " + topicName);
      for (String messageId : messageIds) {
        LOG.info(messageId);
      }

      publisher.shutdown();
      publisher.awaitTermination(1, TimeUnit.MINUTES);
    }
  }
}
