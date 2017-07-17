package it.redhat.demo.bpms;

import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Created by fabio.ercoli@redhat.com on 17/07/2017.
 */

@ApplicationScoped
public class KieProducer {

    public static final int TIMEOUT = 180000;
    public static final String SERVER_URL = "http://localhost:8080/kie-server/services/rest/server";

    @Produces
    public KieServicesClient getServiceClient() {

        String username = "fabio";
        String password = username + "$739";

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(SERVER_URL, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(TIMEOUT);

        return KieServicesFactory.newKieServicesClient(config);

    }

}
