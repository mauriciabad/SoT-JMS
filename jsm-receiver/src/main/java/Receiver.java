import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Receiver {

  private Session session;
  private Destination sendDestination;
  private Connection connection;
  private MessageConsumer consumer;

  private List<TextMessage> messages = new ArrayList<TextMessage>();
  private List<String> messagesTitles = new ArrayList<String>();

  private Runnable onMessageChange;

  public Receiver(Runnable onMessageChange) {
    this.onMessageChange = onMessageChange;
    init();

    try {
      consumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message msg) {
          messages.add((TextMessage) msg);
          onMessageChange.run();
        }
      });
      connection.start();
    } catch (JMSException e) {
      System.out.println("Error creating message listener");
      e.printStackTrace();
    }
  }

  private void init() {
    try {
      connection = new ActiveMQConnectionFactory("tcp://localhost:61616").createConnection();
      session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);;
      sendDestination = session.createQueue("QueueToAdmin");
      consumer = session.createConsumer(sendDestination);
    } catch (JMSException e) {
      System.out.println("MQ not running in tcp://localhost:61616");
      e.printStackTrace();
    }
  }

  public void replyMessage(int index, String body) {
    TextMessage requestMsg = messages.remove(index);

    TextMessage replyMsg = null;
    try {
      replyMsg = session.createTextMessage(body);
      replyMsg.setJMSCorrelationID(requestMsg.getJMSMessageID());
      MessageProducer producer = session.createProducer(requestMsg.getJMSReplyTo());
      producer.send(requestMsg);
    } catch (JMSException e) {
      System.out.println("Error sending the message: " + body);
      e.printStackTrace();
    }

    onMessageChange.run();
  }

  public List getMessages() {
    return messages;
  }

  public List getMessagesTitles() {
    return messages.stream().map(msg -> {
      try {
        String question = msg.getText();
        String author = "Anonymous";
        String response = "No response yet";

        return author + ": " + question + " | You: " + response;

      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }
}