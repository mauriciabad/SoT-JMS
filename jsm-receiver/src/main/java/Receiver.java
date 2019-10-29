import org.apache.activemq.ActiveMQConnectionFactory;
import org.json.JSONObject;

import javax.jms.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Receiver {

  private Session session;
  private Destination receiveDestination;
  private Destination receiveImportantDestination;
  private Connection connection;
  private MessageConsumer consumer;
  private MessageConsumer consumerImportant;
  private MessageProducer producer;

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

    try {
      consumerImportant.setMessageListener(new MessageListener() {
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
      receiveDestination = session.createQueue("QueueToAdmin");
      receiveImportantDestination = session.createQueue("QueueToImportantAdmin");
      consumer = session.createConsumer(receiveDestination);
      consumerImportant = session.createConsumer(receiveImportantDestination);
      producer = session.createProducer(null);
    } catch (JMSException e) {
      System.out.println("MQ not running in tcp://localhost:61616");
      e.printStackTrace();
    }
  }

  public void replyMessage(int index, String body) { replyMessage(index, body, false); }

  public void replyMessage(int index, String body, Boolean important) {
    TextMessage receivedMsg = receivedMessages.get(index);
    TextMessage sentMsg = null;

    try {
      JSONObject receivedJson = new JSONObject(receivedMsg.getText());
      JSONObject sentJson = new JSONObject();

      sentJson.put("name", receivedJson.getString("name"));
      sentJson.put("question", receivedJson.getString("question"));
      sentJson.put("response", body);
      sentJson.put("color", receivedJson.getString("color"));
      sentJson.put("important", receivedJson.getBoolean("important"));

      sentMsg = session.createTextMessage(sentJson.toString());
      sentMsg.setJMSCorrelationID(receivedMsg.getJMSMessageID());
      Destination returnAddress = receivedMsg.getJMSReplyTo();
      sentMsg.setJMSDestination(returnAddress);
      producer.send(returnAddress, sentMsg);
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
        JSONObject receivedJson = new JSONObject(msg.getText());

        String question = receivedJson.getString("question");
        String name     = receivedJson.getString("name");
        String color    = receivedJson.getString("color");
        Boolean important = receivedJson.getBoolean("important");
        String response = "No response yet";

        if (sentMessages.containsKey(msg.getJMSMessageID())){
          String sendMsgText = sentMessages.get(msg.getJMSMessageID()).getText();

          JSONObject sendJson = new JSONObject(sendMsgText);
          question = sendJson.getString("question");
          name     = sendJson.getString("name");
          color    = sendJson.getString("color");
          important= sendJson.getBoolean("important");
          response = sendJson.getString("response");
        }

        String importantStr = important ? "⚠ " : "";

        return importantStr + name + ": " + question + " | You: " + response;

      } catch (JMSException e) {
        return "Unreadable message";
      }
    }).collect(Collectors.toList());
  }
}