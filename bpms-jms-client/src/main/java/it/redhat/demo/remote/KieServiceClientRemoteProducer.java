package it.redhat.demo.remote;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@ApplicationScoped
public class KieServiceClientRemoteProducer {

    private static final String REMOTING_URL = new String("remote://localhost:4547");
    private static final String USER = "guest";
    private static final String PASSWORD = "guest$739";
    private static final String INITIAL_CONTEXT_FACTORY = new String("org.jboss.naming.remote.client.InitialContextFactory");
    private static final String CONNECTION_FACTORY = new String("jms/RemoteConnectionFactory");
    private static final String REQUEST_QUEUE_JNDI = new String("jms/queue/KIE.SERVER.REQUEST");
    private static final String RESPONSE_QUEUE_JNDI = new String("jms/queue/KIE.SERVER.RESPONSE");

    @Produces
    @JmsRemote
    public KieServicesClient getClient() {

        final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, REMOTING_URL);
        env.put(Context.SECURITY_PRINCIPAL, USER);
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD);

        Queue requestQueue;
        Queue responseQueue;
        ConnectionFactory connectionFactory;

        try {
            InitialContext context = new InitialContext(env);
            requestQueue = (Queue) context.lookup(REQUEST_QUEUE_JNDI);
            responseQueue = (Queue) context.lookup(RESPONSE_QUEUE_JNDI);
            connectionFactory = (ConnectionFactory) context.lookup(CONNECTION_FACTORY);
        } catch (NamingException e) {
            throw new RuntimeException(e.getCause());
        }

        KieServicesConfiguration jmsConf = KieServicesFactory.newJMSConfiguration(connectionFactory, requestQueue, responseQueue, USER, PASSWORD);
        jmsConf.setMarshallingFormat(MarshallingFormat.JSON);
        jmsConf.setJmsTransactional(false);

        return KieServicesFactory.newKieServicesClient(jmsConf);

    }

}
