package it.redhat.demo.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;

/**
 * Created by fabio.ercoli@redhat.com on 24/04/17.
 */

@Stateless
public class ClearQueue {

    private static final Logger LOG = LoggerFactory.getLogger(ClearQueue.class);
    public static final long TIME_OUT = 3000;

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    @Resource(mappedName = "java:/mqResponse")
    private Queue responseQueue;

    public int purgeRequest() {
        return purge(requestQueue);
    }

    public int purgeResponse() {
        return purge(responseQueue);
    }

    private int purge(Queue queue) {

        Connection connection = null;
        Session session = null;
        MessageConsumer consumer = null;

        int result = 0;

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(queue);

            while (true) {

                Message message = consumer.receive(TIME_OUT);
                if (message == null) {
                    return result;
                }

                result++;
                if (message instanceof TextMessage) {
                    JmsUtil.logMessage(LOG, ":: CLEAR MESSAGE ::", (TextMessage) message, queue.getQueueName());
                }

            }


        } catch (JMSException ex) {

            throw new RuntimeException(ex.getMessage(), ex);

        } finally {

            // Close objects
            if(consumer != null) {
                try {
                    consumer.close();
                } catch (JMSException ex) {
                    LOG.warn("Unable to close consumer", ex);
                }
            }

            if(session != null) {
                try {
                    session.close();
                } catch (JMSException ex) {
                    LOG.warn("Unable to close session", ex);
                }
            }

            if(connection != null) {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    LOG.warn("Unable to close connection", ex);
                }
            }

        }

    }



}
