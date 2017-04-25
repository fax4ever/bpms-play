package it.redhat.demo.jms;

import it.redhat.demo.jms.producer.StartProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;
import java.util.Enumeration;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */

@ApplicationScoped
public class BpmsRequestSenderJms {

    private static final Logger LOG = LoggerFactory.getLogger(BpmsRequestSenderJms.class);

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    @Inject
    @StartProcess
    private String startProcessTemplate;

    public void startProcess(String container, String definition, String payload) {

        String jsonCommand = startProcessTemplate
                .replace("{{container}}", container)
                .replace("{{definition}}", definition)
                .replace("{{payload}}", payload);

        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(requestQueue);
            connection.start();

            TextMessage requestMessage = session.createTextMessage(jsonCommand);

            // 2 --> json format
            requestMessage.setIntProperty("serialization_format", 2);

            // 2 --> async interaction
            requestMessage.setIntProperty("kie_interaction_pattern", 2);

            requestMessage.setStringProperty("kie_class_type", "org.kie.server.api.commands.DescriptorCommand");
            requestMessage.setStringProperty("kie_target_capability", "BPM");
            requestMessage.setStringProperty("container_id", container);

            producer.send(requestMessage);
            logMessage("sent message", requestMessage, requestQueue);

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

    private void logMessage(String action, TextMessage textMessage, Queue queue) throws JMSException {

        String jmsCorrelationID = textMessage.getJMSCorrelationID();

        LOG.info("{}\n{}",action, textMessage.getText());
        LOG.info("from queue {}", queue.getQueueName());
        LOG.info("message id {}", textMessage.getJMSMessageID());
        LOG.info("correlation ID {}", jmsCorrelationID);

        Enumeration srcProperties = textMessage.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String)srcProperties.nextElement ();
            Object propertyValue = textMessage.getObjectProperty(propertyName);
            LOG.info("property name {} - value {}", propertyName, propertyValue);
        }

    }

}
