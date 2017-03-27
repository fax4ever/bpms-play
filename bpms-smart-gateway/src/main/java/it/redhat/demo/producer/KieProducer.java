package it.redhat.demo.producer;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@ApplicationScoped
public class KieProducer {

    public static final int TIMEOUT = 180000;

    @Produces
    public KieServicesClient getServiceClient() {

        Set<Class<?>> extraClasses = new HashSet<Class<?>>();
        extraClasses.add(Date.class);

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration("http://localhost:8080/kie-server/services/rest/server", "fabio", "fabio$739");
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(TIMEOUT);
        config.addJaxbClasses(extraClasses);

        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(config);
        return kieServicesClient;

    }

}
