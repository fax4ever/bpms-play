package it.redhat.demo.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import it.redhat.demo.exception.InitGatewayException;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.KieServicesFactory;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@ApplicationScoped
public class KieProducer {

    public static final int TIMEOUT = 180000;
    public static final String SERVER_URL = "http://localhost:8080/kie-server/services/rest/server";
    public static final int SLEEP = 0;
    public static final int RETRIES = 20;

    @Inject
    private Logger log;

    @Produces
    public KieServicesClient getServiceClient() {

        return getServiceClient("fabio");

    }

    public KieServicesClient getServiceClient(String username) {

        String password = username + "$739";

        KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(SERVER_URL, username, password);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(TIMEOUT);

        return tryGetKieServiceClient(config);

    }

    private KieServicesClient tryGetKieServiceClient(KieServicesConfiguration config) {
        KieServicesClient result = null;

        int retry = RETRIES;
        int loopCount = 0;
        int sleep = SLEEP;

        while (result == null && loopCount < retry) {
            try {
                result = KieServicesFactory.newKieServicesClient(config);
            } catch (KieServicesException ex) {

                loopCount++;
                log.warn("Error on establishing connection with remote process server. Retry {}. Loop Count {}. Sleep {}", retry, loopCount, sleep);

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
        }

        if (result == null) {
            throw new InitGatewayException(retry, loopCount, sleep);
        }

        return result;

    }

}
