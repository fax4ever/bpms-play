package it.redhat.demo.stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;
import java.util.Enumeration;

/**
 * Created by fabio.ercoli@redhat.com on 24/04/17.
 */

@Stateless
public class ClearQueue {

    private static final Logger LOG = LoggerFactory.getLogger(CustomProcessStateless.class);
    public static final long TIME_OUT = 10000;

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    @Resource(mappedName = "java:/mqResponse")
    private Queue responseQueue;

    public int purgeResponse() {
        return purge(requestQueue);
    }

    public int purgeRequest() {
        return purge(responseQueue);
    }

    private int purge(Queue queue) {

        Connection connection = null;
        Session session = null;
        int result = 0;

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            MessageConsumer messageConsumer = session.createConsumer(queue);

            while (true) {

                Message message = messageConsumer.receive(TIME_OUT);
                if (message == null) {
                    return result;
                }

                result++;
                if (message instanceof TextMessage) {
                    logMessage("remove message", (TextMessage) message, queue);
                }

            }


        } catch (JMSException ex) {

            throw new RuntimeException(ex.getMessage(), ex);

        } finally {

            if (connection != null) {
                try {
                    connection.close();
                    if (session != null) {
                        session.close();
                    }
                } catch (JMSException ex) {
                    LOG.warn("Unable to close connection or session!", ex);
                }
            }

        }

    }

    private void logMessage(String action, TextMessage textMessage, Queue queue) throws JMSException {

        String jmsCorrelationID = textMessage.getJMSCorrelationID();

        LOG.info("{}\n{}",action, textMessage.getText());
        LOG.info("from queue {}", queue);
        LOG.info("correlation ID {}", jmsCorrelationID);

        Enumeration srcProperties = textMessage.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String)srcProperties.nextElement ();
            Object propertyValue = textMessage.getObjectProperty(propertyName);
            LOG.info("property name {} - value {}", propertyName, propertyValue);
        }

    }

}
