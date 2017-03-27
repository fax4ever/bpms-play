package it.redhat.demo.rest;

import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
@Path("kie")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource {

    @Inject
    private KieServicesClient kieServices;

    @PostConstruct
    private void init() {

    }

    @GET
    public ServiceResponse<KieServerInfo> getServerInfo() {

        return kieServices.getServerInfo();

    }

    @GET
    @Path("process/definitions")
    public List<ProcessDefinition> getProcessDefinitions() {

        QueryServicesClient query = getQuery();
        return query.findProcesses(0, 100);

    }

    private QueryServicesClient getQuery() {
        return kieServices.getServicesClient(QueryServicesClient.class);
    }

}
