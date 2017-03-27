package it.redhat.demo.producer;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@ApplicationScoped
public class KieProducer {

    public static final int TIMEOUT = 180000;

    @Produces
    public KieServicesClient getServiceClient() {

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration("http://localhost:8080/kie-server/services/rest/server", "fabio", "fabio$739");
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(TIMEOUT);

        KieServicesClient kieServicesClient = KieServicesFactory.newKieServicesClient(config);
        return kieServicesClient;

    }


}
