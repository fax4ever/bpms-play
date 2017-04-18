package it.redhat.demo.stateless;

import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Stateless
public class KieServiceClientStateless {

    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/KIE.SERVER.REQUEST")
    private Queue requestQueue;

    @Resource(mappedName = "java:/queue/KIE.SERVER.RESPONSE")
    private Queue responseQueue;

    public KieServicesClient getClient() {

        KieServicesConfiguration jmsConf = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue);
        return KieServicesFactory.newKieServicesClient(jmsConf);

    }


}
