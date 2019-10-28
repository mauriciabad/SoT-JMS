import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Requestor {

  private Session session;
  private Destination sendDestination;
  private Destination receiveDestination;
  private Connection connection;
  private MessageProducer producer;
  private MessageConsumer consumer;

  private List<Message> sentMessages = new ArrayList<Message>();
  private List<Message> receivedMessages = new ArrayList<Message>();
  private List<String> sentMessagesTitles = new ArrayList<String>();

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
          receivedMessages.add(msg);
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

  //  public static void displayMessage(TextMessage msg) {
  //    try {
  //      System.out.println("JMSMessageID = " + msg.getJMSMessageID());
  //      System.out.println("  JMSDestination = " + msg.getJMSDestination());
  //      System.out.println("  Text = " + msg.getText());
  //    } catch (JMSException e) {
  //      System.out.println("sent: " + msg);
  //    }
  //  }

  public TextMessage sendMessage(String body) {
    TextMessage msg = null;
    try {
      msg = session.createTextMessage(body);
      msg.setJMSReplyTo(receiveDestination);
      producer.send(msg);
    } catch (JMSException e) {
      System.out.println("Error sending the message: " + body);
      e.printStackTrace();
    }
    return msg;
  }

  public List getSentMessages() { return sentMessages; }
  public List getReceivedMessages() { return receivedMessages; }

  public List getMessagesTitles() {
    return sentMessages.stream().map(msg -> {
      try {
        return ((TextMessage) msg).getText();
      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }

  public String getName() { return name; }
  public Color getColor() { return color; }
}
