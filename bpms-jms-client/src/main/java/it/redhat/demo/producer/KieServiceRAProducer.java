package it.redhat.demo.producer;

import it.redhat.demo.qualifier.JmsRA;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@ApplicationScoped
public class KieServiceRAProducer {

    @Resource(mappedName = "java:/MQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/mqRequest")
    private Queue requestQueue;

    @Resource(mappedName = "java:/mqResponse")
    private Queue responseQueue;

    @Produces
    @JmsRA
    public KieServicesClient getClient() {

        KieServicesConfiguration jmsConf = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue);
        jmsConf.setMarshallingFormat(MarshallingFormat.JSON);

        return KieServicesFactory.newKieServicesClient(jmsConf);

    }


}
