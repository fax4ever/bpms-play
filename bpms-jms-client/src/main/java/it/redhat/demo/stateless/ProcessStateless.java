package it.redhat.demo.stateless;

import it.redhat.demo.qualifier.JmsVM;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Stateless
public class ProcessStateless {

    @Inject
    @JmsVM
    //@JmsRemote
    private KieServicesClient gateway;

    public Long startProcess() {

        ProcessServicesClient processService = gateway.getServicesClient(ProcessServicesClient.class);
        return processService.startProcess("main", "it.redhat.demo.diagram");

    }

}
