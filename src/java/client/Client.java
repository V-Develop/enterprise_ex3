/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package client;

import java.util.Scanner;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author alongkornvanzoh
 */
public class Client {

    @Resource(mappedName = "jms/ConnectionFactory")
    private static ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/TempQueue")
    private static Queue queue;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection connection = null;
        TextListener listener = null;
        boolean isRunning = true;

        while (isRunning) {
            try {
                connection = connectionFactory.createConnection();
                Session session = connection.createSession(
                        false,
                        Session.AUTO_ACKNOWLEDGE);
                listener = new TextListener();
                Queue tempDest = session.createTemporaryQueue();
                MessageConsumer responseConsumer = session.createConsumer(tempDest);
                responseConsumer.setMessageListener(listener);
                MessageProducer producer = session.createProducer(queue);
                TextMessage message = session.createTextMessage();

                Scanner sc = new Scanner(System.in);
                System.out.println("Enter two numbers. Use ',' to seperate each number. To end the program press enter");
                String input = sc.nextLine();
                message.setText(input);
                message.setJMSReplyTo(tempDest);
              
                String correlationId = "12345";
                message.setJMSCorrelationID(correlationId);
                connection.start();
                System.out.println("Sending message: " + message.getText());
                producer.send(message);

                String ch = "";
                Scanner inp = new Scanner(System.in);
                System.out.println("press any key to continue");
                ch = sc.nextLine();
                if (input.equals("")) {
                    isRunning = false;
                }
            } catch (JMSException e) {
                System.err.println("Exception occurred: " + e.toString());
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (JMSException e) {
                    }
                }
            }
        }
    }

}
