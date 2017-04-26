package it.redhat.demo.mdb;

import it.redhat.demo.jms.JmsUtil;
import org.jboss.ejb3.annotation.ResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */

@MessageDriven(name = "BpmsResponseReaderMdb", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "useJNDI", propertyValue = "false"),
        @ActivationConfigProperty(propertyName = "hostName", propertyValue = "mpebtx01.generali.it"),
        @ActivationConfigProperty(propertyName = "port", propertyValue = "11421"),
        @ActivationConfigProperty(propertyName = "channel", propertyValue = "QS1XXLZ1.SC.WAS"),
        @ActivationConfigProperty(propertyName = "queueManager", propertyValue = "QS1XXLZ1"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "BPMS.LAB.RESPONSE"),
        @ActivationConfigProperty(propertyName = "transportType", propertyValue = "CLIENT") })
@ResourceAdapter(value = "wmq.jmsra.rar")
public class BpmsResponseReaderMdb implements MessageListener {

    private static Logger LOG = LoggerFactory.getLogger(BpmsResponseReaderMdb.class);

    @Override
    public void onMessage(Message message) {

        if (!(message instanceof TextMessage)) {
            LOG.info("wrong message type {}", message.getClass());
            return;
        }

        TextMessage textMessage = (TextMessage) message;
        String text;

        try {
            text = textMessage.getText();
            String queueName = System.getProperty("wmq.destination.response");
            JmsUtil.logMessage(LOG, ":: RECEIVE MESSAGE ::", (TextMessage) message, queueName);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        LOG.info("BPM Command Completed ", text);
    }


}
