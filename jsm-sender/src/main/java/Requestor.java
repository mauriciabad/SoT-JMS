import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Requestor {

  private Session session;
  private Destination sendDestination;
  private Destination receiveDestination;
  private Connection connection;
  private MessageProducer producer;
  private MessageConsumer consumer;

  private List<TextMessage> sentMessages = new ArrayList<TextMessage>();
  private Map<String,TextMessage> receivedMessages = new HashMap<String, TextMessage>();

  private String name = Randomizer.getName();
  private Color color = Randomizer.getColor();

  private Runnable onMessageChange;

  public Requestor(Runnable onMessageChange) {
    this.onMessageChange = onMessageChange;
    init();

    try {
      consumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message msg) {
          try {
            receivedMessages.put(msg.getJMSCorrelationID(), (TextMessage) msg);
          } catch (JMSException e) {
            System.out.println("Error getting correlation id");
            e.printStackTrace();
          }
          onMessageChange.run();
        }
      });
      connection.start(); // this is needed to start receiving sentMessages
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
      receiveDestination = session.createQueue("QueueTo"+name);
      producer = session.createProducer(sendDestination);
      consumer = session.createConsumer(receiveDestination);
    } catch (JMSException e) {
      System.out.println("MQ not running in tcp://localhost:61616");
      e.printStackTrace();
    }
  }

  public TextMessage sendMessage(String body) {
    TextMessage msg = null;
    try {
      msg = session.createTextMessage(body);
      msg.setJMSReplyTo(receiveDestination);
      msg.setJMSDestination(sendDestination);
      producer.send(msg);
      sentMessages.add(msg);
    } catch (JMSException e) {
      System.out.println("Error sending the message: " + body);
      e.printStackTrace();
    }
    onMessageChange.run();
    return msg;
  }

  public List<TextMessage> getSentMessages() { return sentMessages; }
  public Map<String, TextMessage> getReceivedMessages() { return receivedMessages; }

  public List getMessagesTitles() {
    return sentMessages.stream().map(msg -> {
      try {
        String question = msg.getText();
        String author = name;
        String response = "No response yet";

        if (receivedMessages.containsKey(msg.getJMSMessageID())){
          response = receivedMessages.get(msg.getJMSMessageID()).getText();
        }

        return author + ": " + question + " Support: " + response;

      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }

  public String getName() { return name; }
  public Color getColor() { return color; }
}
