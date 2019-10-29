import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONObject;

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
  private Destination sendImportantDestination;
  private Destination receiveImportantDestination;
  private Connection connection;
  private MessageProducer producer;
  private MessageConsumer consumer;
  private MessageConsumer consumerImportant;

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

    try {
      consumerImportant.setMessageListener(new MessageListener() {
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
      sendImportantDestination = session.createQueue("QueueToImportantAdmin");
      receiveDestination = session.createQueue("QueueTo"+name);
      receiveImportantDestination = session.createQueue("QueueToImportant"+name);
      producer = session.createProducer(null);
      consumer = session.createConsumer(receiveDestination);
      consumerImportant = session.createConsumer(receiveImportantDestination);
    } catch (JMSException e) {
      System.out.println("MQ not running in tcp://localhost:61616");
      e.printStackTrace();
    }
  }

  public TextMessage sendMessage(String body) { return sendMessage(body, false); }

  public TextMessage sendMessage(String body, Boolean important) {
    JSONObject json = new JSONObject();

    String c = String.format("#%06X", (0xFFFFFF & color.getRGB()));
    json.put("name", name);
    json.put("color", c);
    json.put("question", body);
    json.put("important", important);

    TextMessage msg = null;
    try {
      msg = session.createTextMessage(json.toString());

      if (important) {
        msg.setJMSReplyTo(receiveImportantDestination);
        msg.setJMSDestination(sendImportantDestination);
        producer.send(sendImportantDestination, msg);
      } else {
        msg.setJMSReplyTo(receiveDestination);
        msg.setJMSDestination(sendDestination);
        producer.send(sendDestination, msg);
      }

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
        JSONObject sentJson = new JSONObject(msg.getText());

        String question = sentJson.getString("question");
        String name     = sentJson.getString("name");
        String color    = sentJson.getString("color");
        Boolean important = sentJson.getBoolean("important");
        String response = "No response yet";

        if (receivedMessages.containsKey(msg.getJMSMessageID())){
          String receivedMsgText = receivedMessages.get(msg.getJMSMessageID()).getText();

          JSONObject receivedJson = new JSONObject(receivedMsgText);
          question = receivedJson.getString("question");
          name     = receivedJson.getString("name");
          color    = receivedJson.getString("color");
          important= receivedJson.getBoolean("important");
          response = receivedJson.getString("response");
        }

        String importantStr = important ? "⚠ " : "";

        return importantStr + "You: " + question + " | Support: " + response;

      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }

  public String getName() { return name; }
  public Color getColor() { return color; }
}
