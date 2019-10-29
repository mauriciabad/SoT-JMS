import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Receiver {

  private Session session;
  private Destination sendDestination;
  private Connection connection;
  private MessageConsumer consumer;

  private Map<String,TextMessage> sentMessages= new HashMap<String, TextMessage>();
  private List<TextMessage> receivedMessages = new ArrayList<TextMessage>();

  private Runnable onMessageChange;

  public Receiver(Runnable onMessageChange) {
    this.onMessageChange = onMessageChange;
    init();

    try {
      consumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message msg) {
          receivedMessages.add((TextMessage) msg);
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
    TextMessage receivedMsg = receivedMessages.get(index);

    TextMessage sentMsg = null;
    try {
      sentMsg = session.createTextMessage(body);
      sentMsg.setJMSCorrelationID(receivedMsg.getJMSMessageID());
      MessageProducer producer = session.createProducer(receivedMsg.getJMSReplyTo());
      producer.send(receivedMsg);
      sentMessages.put(receivedMsg.getJMSMessageID(), sentMsg);
    } catch (JMSException e) {
      System.out.println("Error sending the message: " + body);
      e.printStackTrace();
    }

    onMessageChange.run();
  }

  public List<TextMessage> getReceivedMessages() { return receivedMessages; }
  public Map<String, TextMessage> getSentMessages() { return sentMessages; }

  public List getMessagesTitles() {
    return receivedMessages.stream().map(msg -> {
      try {
        String question = msg.getText();
        String author = "Name";
        String response = "No response yet";

        if (sentMessages.containsKey(msg.getJMSMessageID())){
          response = sentMessages.get(msg.getJMSMessageID()).getText();
        }

        return author + ": " + question + " You: " + response;

      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }
}