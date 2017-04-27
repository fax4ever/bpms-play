package it.redhat.demo.stateless;

import it.redhat.demo.remote.JmsRemote;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Stateless
public class BpmsProcessStateless {
    
    private static final Logger LOGG = LoggerFactory.getLogger(BpmsProcessStateless.class);

    @Inject
    @JmsRemote
    private KieServicesClient gateway;

    public Long startProcess(String container, String definition, Map<String, Object> params) {

        String correlation = UUID.randomUUID().toString();
        SimpleCorrelationKey correlationKey = new SimpleCorrelationKey(correlation);

        LOGG.info("starting process using correlation key {}", correlation);

        ProcessServicesClient processService = gateway.getServicesClient(ProcessServicesClient.class);
        return processService.startProcess(container, definition, correlationKey, params);

    }

    public void sendSignal(String container, Long pi, String name, String value) {

        ProcessServicesClient processService = gateway.getServicesClient(ProcessServicesClient.class);
        processService.signalProcessInstance(container, pi, name, value);

    }

}
