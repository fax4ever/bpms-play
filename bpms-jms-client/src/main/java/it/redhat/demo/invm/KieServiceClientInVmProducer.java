package it.redhat.demo.invm;

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
public class KieServiceClientInVmProducer {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/KIE.SERVER.REQUEST")
    private Queue requestQueue;

    @Resource(mappedName = "java:/queue/KIE.SERVER.RESPONSE")
    private Queue responseQueue;

    @Produces
    @JmsInVM
    public KieServicesClient getClient() {

        KieServicesConfiguration jmsConf = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue);
        jmsConf.setMarshallingFormat(MarshallingFormat.JSON);

        return KieServicesFactory.newKieServicesClient(jmsConf);

    }


}
