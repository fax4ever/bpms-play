package it.redhat.demo.mdb;

import org.jboss.ejb3.annotation.ResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * Created by fabio.ercoli@redhat.com on 25/04/17.
 */

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
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        LOG.info("BPM Command Completed ", text);
    }


}
