package it.redhat.demo.jms;

import org.slf4j.Logger;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Enumeration;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */
public class JmsUtil {

    public static void logMessage(Logger logger, String action, TextMessage textMessage, String queueName) throws JMSException {

        String jmsCorrelationID = textMessage.getJMSCorrelationID();

        logger.info("{}\n{}",action, textMessage.getText());
        logger.info("queue {}", queueName);
        logger.info("correlation ID {}", jmsCorrelationID);

        Enumeration srcProperties = textMessage.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String)srcProperties.nextElement ();
            Object propertyValue = textMessage.getObjectProperty(propertyName);
            logger.info("property name {} - value {}", propertyName, propertyValue);
        }

    }



}
