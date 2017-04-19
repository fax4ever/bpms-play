package it.redhat.demo.stateless;

import it.redhat.demo.qualifier.StartProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by fabio.ercoli@redhat.com on 19/04/17.
 */

@Stateless
public class CustomProcessStateless {

    private static final Logger LOG = LoggerFactory.getLogger(CustomProcessStateless.class);
    public static final long TIME_OUT = 360000;

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/KIE.SERVER.REQUEST")
    private Queue requestQueue;

    @Resource(mappedName = "java:/queue/KIE.SERVER.RESPONSE")
    private Queue responseQueue;

    @Inject
    @StartProcess
    private String startProcessPayload;

    public String startProcess() {

        // for request
        String corrId = UUID.randomUUID().toString();
        // for response
        String selector = "JMSCorrelationID = '" + corrId + "'";

        Connection connection = null;
        Session session = null;

        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(requestQueue);
            MessageConsumer messageConsumer = session.createConsumer(responseQueue, selector);
            connection.start();

            TextMessage requestMessage = session.createTextMessage(startProcessPayload);

            requestMessage.setJMSCorrelationID(corrId);
            requestMessage.setIntProperty("serialization_format", 2);
            requestMessage.setIntProperty("kie_interaction_pattern", 1);
            requestMessage.setStringProperty("kie_class_type", "org.kie.server.api.commands.DescriptorCommand");
            requestMessage.setStringProperty("kie_target_capability", "BPM");
            requestMessage.setStringProperty("container_id", "main");

            logMessage("sending message", requestMessage);
            messageProducer.send(requestMessage);

            TextMessage responseMessage = (TextMessage) messageConsumer.receive(TIME_OUT);
            logMessage("received message", responseMessage);

            return responseMessage.getText();

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

    private void logMessage(String action, TextMessage textMessage) throws JMSException {

        LOG.info("{}\n{}",action, textMessage.getText());

        Enumeration srcProperties = textMessage.getPropertyNames();
        while (srcProperties.hasMoreElements()) {
            String propertyName = (String)srcProperties.nextElement ();
            Object propertyValue = textMessage.getObjectProperty(propertyName);
            LOG.info("property name {} - value {}", propertyName, propertyValue);
        }

    }


}
