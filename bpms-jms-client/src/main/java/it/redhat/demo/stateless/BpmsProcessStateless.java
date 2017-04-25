package it.redhat.demo.stateless;

import it.redhat.demo.invm.JmsInVM;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Stateless
public class BpmsProcessStateless {

    @Inject
    @JmsInVM
    private KieServicesClient gateway;

    public Long startProcess(String container, String definition, Map<String, Object> params) {

        ProcessServicesClient processService = gateway.getServicesClient(ProcessServicesClient.class);
        return processService.startProcess(container, definition, params);

    }

}
