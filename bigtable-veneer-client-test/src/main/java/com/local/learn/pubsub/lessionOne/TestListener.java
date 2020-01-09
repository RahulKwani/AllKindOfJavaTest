package com.local.learn.pubsub.lessionOne;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestListener {

  private static final Logger LOG = Logger.getLogger(TestListener.class.getName());
  private static final String PROJECT_ID = ServiceOptions.getDefaultProjectId();

  private static final List<MessageReceiver> childMessageReceiver = new ArrayList<>();

  private static final String SUBS_1 = "test_pubsub_sample";
  private static final String SUBS_2 = "test_pubsub_sample_2";

  static class OuterMessageReceiver implements MessageReceiver {

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
      String messageData = message.getData().toStringUtf8();
      LOG.info("OUTER SUBS=> Message Id: " + message.getMessageId() + " Data: " + messageData);
      consumer.ack();

      if (messageData.startsWith("START_INNER_LISTENER")) { // Listener for another Subscription
        ProjectSubscriptionName subscription2 = ProjectSubscriptionName.of(PROJECT_ID, SUBS_2);
        try {

          MessageReceiver messageReceiver = new InnerMessageReceiver();
          childMessageReceiver.add(messageReceiver);

          Subscriber childSubs = Subscriber.newBuilder(subscription2, messageReceiver).build();
          childSubs.startAsync().awaitRunning();

          // This should stop the subscriber after 2 mins but it is continue to live on.
          childSubs.awaitTerminated(2, TimeUnit.MINUTES);

        } catch (TimeoutException e) {
          throw new IllegalStateException("Exception occurred while closing the inner subscriber");
        }
      }
    }
  }

  static class InnerMessageReceiver implements MessageReceiver {

    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer ackReplyConsumer) {
      LOG.info(
          "INNER_SUBS=> Message Id: "
              + message.getMessageId()
              + " Data: "
              + message.getData().toStringUtf8());
      ackReplyConsumer.ack();
    }
  }

  public static void main(String[] args) throws Exception {
    ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(PROJECT_ID, SUBS_1);
    try {
      Subscriber outerSubscriber =
          Subscriber.newBuilder(subscriptionName, new OuterMessageReceiver()).build();
      outerSubscriber.startAsync().awaitRunning();

      outerSubscriber.awaitTerminated();
    } catch (IllegalStateException e) {
      LOG.log(Level.SEVERE, "Subscriber unexpectedly stopped: ", e);
    }
  }
}
