package it.redhat.demo.producer;

import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@ApplicationScoped
public class ServiceClientProducer {

    @Inject
    private KieServicesClient kieServices;

    @Produces
    public QueryServicesClient getQuery() {
        return kieServices.getServicesClient(QueryServicesClient.class);
    }

    @Produces
    public ProcessServicesClient getProcess() {
        return kieServices.getServicesClient(ProcessServicesClient.class);
    }

}
