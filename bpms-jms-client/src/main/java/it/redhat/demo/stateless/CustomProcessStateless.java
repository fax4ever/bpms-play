package it.redhat.demo.stateless;

import it.redhat.demo.qualifier.StartProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.*;
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
        MessageProducer messageProducer = null;
        MessageConsumer messageConsumer = null;

        try {

            try {

                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                messageProducer = session.createProducer(requestQueue);
                messageConsumer = session.createConsumer(responseQueue, selector);
                connection.start();

            } catch (JMSException ex) {

                throw new RuntimeException("unable to create connection", ex);

            }

            LOG.info("sending message - payolad : {}", startProcessPayload);

            try {

                TextMessage textMessage = session.createTextMessage(startProcessPayload);

                textMessage.setJMSCorrelationID(corrId);
                textMessage.setIntProperty("serialization_format", 2);
                textMessage.setIntProperty("kie_interaction_pattern", 1);
                textMessage.setStringProperty("kie_class_type", "org.kie.server.api.commands.DescriptorCommand");
                textMessage.setStringProperty("kie_target_capability", "BPM");
                textMessage.setStringProperty("container_id", "main");

                messageProducer.send(textMessage);

            } catch (JMSException ex) {

                throw new RuntimeException("error on sending message", ex);
            }


            try {
                TextMessage response = (TextMessage) messageConsumer.receive(TIME_OUT);
                return response.getText();
            } catch (JMSException ex) {

                throw new RuntimeException("Unable to receive or retrieve the JMS response.", ex);

            }

        } finally {

            if( connection != null ) {
                try {
                    connection.close();
                    if( session != null ) {
                        session.close();
                    }
                } catch( JMSException ex ) {
                    LOG.warn("Unable to close connection or session!", ex);
                }
            }

        }


    }


}
