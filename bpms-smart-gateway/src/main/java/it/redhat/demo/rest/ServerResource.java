package it.redhat.demo.rest;

import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
@Path("server")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ServerResource {

    @Inject
    private KieServicesClient kieServices;

    @GET
    public ServiceResponse<KieServerInfo> getServerInfo() {

        return kieServices.getServerInfo();

    }

}
