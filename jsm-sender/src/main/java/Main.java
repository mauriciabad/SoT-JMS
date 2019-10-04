import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Main {
    public static void main(String[] args) {
        Connection connection; // to connect to the ActiveMQ
        Session session; // session for creating messages, producers and

        Destination sendDestination; // reference to a queue/topic destination
        MessageProducer producer; // for sending messages

        try {
            ConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the sender destination (i.e., queue "myFirstDestination")
            sendDestination = session.createQueue("myFirstDestination");
            producer = session.createProducer(sendDestination);

            String body = "Hello, this is my first message!"; //or serialize an object!
                // create a text message
            Message msg = session.createTextMessage(body);
                // send the message
            producer.send(msg);

            try {
                //print only the attributes you want to see
                System.out.println("JMSMessageID = " + msg.getJMSMessageID());
                System.out.println("  JMSDestination = " + msg.getJMSDestination());
                System.out.println("  Text = " + ((TextMessage) msg).getText());
            } catch (JMSException e) {
                System.out.println("sent: " + msg);
            }
        } catch (JMSException e) { e.printStackTrace(); }
    }
}
