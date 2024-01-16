/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 *
 * @author alongkornvanzoh
 */
public class TextListener implements MessageListener {

    private MessageProducer replyProducer;
    private Session session;

    public TextListener(Session session) {

        this.session = session;
        try {
            replyProducer = session.createProducer(null);
        } catch (JMSException ex) {
            Logger.getLogger(TextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = null;

        try {
            if (message instanceof TextMessage) {
                msg = (TextMessage) message;
                System.out.println("Reading message: " + msg.getText());
            } else {
                System.err.println("Message is not a TextMessage");
            }
            int start = Integer.parseInt(msg.getText().split(",")[0]);
            int end = Integer.parseInt(msg.getText().split(",")[1]);
            String quantityOfPrime = String.valueOf(getPrimeQuantity(start, end));
            
            TextMessage response = session.createTextMessage("The number of primes between " + start + " and " + end + " is " + quantityOfPrime);
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            System.out.println("sending message " + response.getText());
            replyProducer.send(message.getJMSReplyTo(), response);
        } catch (JMSException e) {
            System.err.println("JMSException in onMessage(): " + e.toString());
        } catch (Throwable t) {
            System.err.println("Exception in onMessage():" + t.getMessage());
        }

    }

    private int getPrimeQuantity(int start, int end) {
        int count = 0;
        for (int i = start; i <= end; i++) {
            if (this.isPrime(i)) {
                count += 1;
            }
        }
        return count;
    }

    private boolean isPrime(int n) {
        int i;
        for (i = 2; i * i <= n; i++) {
            if ((n % i) == 0) {
                return false;
            }
        }
        return true;
    }
}
