import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Main {
    public static void main(String[] args) {
        Connection connection; // to connect to the ActiveMQ
        Session session; // session for creating messages, producers and

        Destination receiveDestination; // reference to a queue/topic destination
        MessageConsumer consumer; // for sending messages

        try {
            ConnectionFactory connectionFactory;
            connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // connect to the sender destination (i.e., queue "myFirstDestination")
            receiveDestination = session.createQueue("myFirstDestination");
            consumer = session.createConsumer(receiveDestination);

            consumer.setMessageListener(new MessageListener() {
                @Override public void onMessage(Message msg) {
                    try {
                        //print only the attributes you want to see
                        System.out.println("JMSMessageID = " + msg.getJMSMessageID());
                        System.out.println("  JMSDestination = " + msg.getJMSDestination());
                        System.out.println("  Text = " + ((TextMessage) msg).getText());
                    } catch (JMSException e) {
                        System.out.println("received: " + msg);
                    }
                }
            });
            connection.start(); // this is needed to start receiving messages

        } catch (JMSException e) { e.printStackTrace(); }
    }
}
