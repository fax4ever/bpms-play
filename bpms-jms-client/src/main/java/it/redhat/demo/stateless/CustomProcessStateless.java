package it.redhat.demo.stateless;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.*;
import java.util.UUID;

/**
 * Created by fabio.ercoli@redhat.com on 19/04/17.
 */

@Stateless
public class CustomProcessStateless {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/KIE.SERVER.REQUEST")
    private Queue requestQueue;

    @Resource(mappedName = "java:/queue/KIE.SERVER.RESPONSE")
    private Queue responseQueue;

    public Long startProcess() {

        String corrId = UUID.randomUUID().toString();
        String selector = "JMSCorrelationID = '" + corrId + "'";

        Connection connection = null;

        try {

            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer messageProducer = session.createProducer(requestQueue);
            MessageProducer messageConsumer = session.createProducer(requestQueue);
            connection.start();


        }  catch( JMSException jmse ) {

            throw new RuntimeException("unable to create connection");

        }

        return 0l;

    }


}
