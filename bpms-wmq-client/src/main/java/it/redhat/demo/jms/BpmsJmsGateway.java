package it.redhat.demo.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.*;
import java.util.UUID;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */

@ApplicationScoped
public class BpmsJmsGateway {

    private static final Logger LOG = LoggerFactory.getLogger(BpmsJmsGateway.class);

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    public void send(String container, String jsonCommand) {

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        // for request
        String corrId = UUID.randomUUID().toString();

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(requestQueue);
            connection.start();

            TextMessage requestMessage = session.createTextMessage(jsonCommand);

            requestMessage.setJMSCorrelationID(corrId);

            // 2 --> json format
            requestMessage.setIntProperty("serialization_format", 2);

            // 2 --> async interaction
            requestMessage.setIntProperty("kie_interaction_pattern", 2);

            requestMessage.setStringProperty("kie_class_type", "org.kie.server.api.commands.DescriptorCommand");
            requestMessage.setStringProperty("kie_target_capability", "BPM");
            requestMessage.setStringProperty("container_id", container);

            producer.send(requestMessage);
            JmsUtil.logMessage(LOG, ":: SEND MESSAGE ::", requestMessage, requestQueue.getQueueName());

        } catch (JMSException ex) {

            throw new RuntimeException(ex.getMessage(), ex);

        } finally {

            // Close objects
            if(producer != null) {
                try {
                    producer.close();
                } catch (JMSException ex) {
                    LOG.warn("Unable to close producer", ex);
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
