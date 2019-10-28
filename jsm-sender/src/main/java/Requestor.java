import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Requestor {

  private static Session session;
  private static Destination sendDestination;
  private static MessageProducer producer;

  public static void main(String[] args) {
    init();
    TextMessage msg = sendMessage("Hello, this is my first message!");
    displayMessage(msg);
  }

  private static void init() {
    try {
      session = new ActiveMQConnectionFactory("tcp://localhost:61616").createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);;
      sendDestination = session.createQueue("ChatGroup1");
      producer = session.createProducer(sendDestination);
    } catch (JMSException e) {
      System.out.println("MQ not running in tcp://localhost:61616");
      e.printStackTrace();
    }
  }

  private static void displayMessage(TextMessage msg) {
    try {
      //print only the attributes you want to see
      System.out.println("JMSMessageID = " + msg.getJMSMessageID());
      System.out.println("  JMSDestination = " + msg.getJMSDestination());
      System.out.println("  Text = " + msg.getText());
    } catch (JMSException e) {
      System.out.println("sent: " + msg);
    }
  }

  public static TextMessage sendMessage(String body) {
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
}
