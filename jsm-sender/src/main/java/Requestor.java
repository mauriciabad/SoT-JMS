import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Requestor {

  private Session session;
  private Destination sendDestination;
  private MessageProducer producer;

  private List<Message> messages = new ArrayList<Message>();
  private List<String> messagesTitles = new ArrayList<String>();

  private String name = Randomizer.getName();
  private Color color = Randomizer.getColor();

  private Runnable onMessageChange;

  public Requestor(Runnable onMessageChange) {
    this.onMessageChange = onMessageChange;
    init();

//    try {
//      consumer.setMessageListener(new MessageListener() {
//        @Override
//        public void onMessage(Message msg) {
//          messages.add(msg);
//          onMessageChange.run();
//        }
//      });
//      connection.start(); // this is needed to start receiving messages
//    } catch (JMSException e) {
//      System.out.println("Error creating message listener");
//      e.printStackTrace();
//    }
  }

  private void init() {
    try {
      session = new ActiveMQConnectionFactory("tcp://localhost:61616").createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);;
      sendDestination = session.createQueue("ChatGroup1");
      producer = session.createProducer(sendDestination);
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
      producer.send(msg);
    } catch (JMSException e) {
      System.out.println("Error sending the message: " + body);
      e.printStackTrace();
    }
    return msg;
  }

  public List getMessages() {
    return messages;
  }

  public List getMessagesTitles() {
    return messages.stream().map(msg -> {
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
