package it.redhat.demo.stateless;

import it.redhat.demo.qualifier.StartProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.*;
import java.util.Enumeration;

/**
 * Created by fabio.ercoli@redhat.com on 19/04/17.
 */

@ApplicationScoped
public class OneWayProcessStateless {

    private static final Logger LOG = LoggerFactory.getLogger(OneWayProcessStateless.class);

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    @Inject
    @StartProcess
    private String startProcessPayload;

    public void startProcess() {

        Connection connection = null;
        Session session = null;

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            MessageProducer messageProducer = session.createProducer(requestQueue);
            connection.start();

            TextMessage requestMessage = session.createTextMessage(startProcessPayload);
            requestMessage.setIntProperty("serialization_format", 2);
            requestMessage.setIntProperty("kie_interaction_pattern", 2);
            requestMessage.setStringProperty("kie_class_type", "org.kie.server.api.commands.DescriptorCommand");
            requestMessage.setStringProperty("kie_target_capability", "BPM");
            requestMessage.setStringProperty("container_id", "main");

            messageProducer.send(requestMessage);
            logMessage("sending message", requestMessage, requestQueue);

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
