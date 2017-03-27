package it.redhat.demo.rest;

import it.redhat.demo.qualifier.ContaierV3;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.client.KieServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
@Path("kie/v3")
@Produces(MediaType.APPLICATION_JSON)
public class Container3Resource {

    @Inject
    private KieServicesClient kieServices;

    @Inject @ContaierV3
    private KieContainerResource kieContainerV1;

    @GET
    public ServiceResponse<KieContainerResource> getContainer() {

        return kieServices.getContainerInfo(kieContainerV1.getContainerId());

    }

    @POST
    public ServiceResponse<KieContainerResource> deployContainer() {

        return kieServices.createContainer(kieContainerV1.getContainerId(), kieContainerV1);

    }

    @DELETE
    public ServiceResponse<Void> disposeContainer() {

        return kieServices.disposeContainer(kieContainerV1.getContainerId());

    }

}
