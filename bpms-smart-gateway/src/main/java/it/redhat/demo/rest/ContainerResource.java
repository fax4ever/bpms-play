package it.redhat.demo.rest;

import it.redhat.demo.qualifier.ContaierV1;
import it.redhat.demo.qualifier.ContaierV2;
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
@Path("container/{version : v[1-3]}")
@Produces(MediaType.APPLICATION_JSON)
public class ContainerResource {

    @Inject
    private KieServicesClient kieServices;

    @Inject @ContaierV1
    private KieContainerResource kieContainerV1;

    @Inject @ContaierV2
    private KieContainerResource kieContainerV2;

    @Inject @ContaierV3
    private KieContainerResource kieContainerV3;

    private KieContainerResource lookupContainer(String version) {

        return  ("v1".equals(version)) ? kieContainerV1 :
                ("v2".equals(version)) ? kieContainerV2 :
                kieContainerV3;

    }

    @GET
    public ServiceResponse<KieContainerResource> getContainer(@PathParam("version") String version) {

        return kieServices.getContainerInfo(lookupContainer(version).getContainerId());

    }


    @POST
    public ServiceResponse<KieContainerResource> deployContainer(@PathParam("version") String version) {

        KieContainerResource container = lookupContainer(version);
        return kieServices.createContainer(container.getContainerId(), container);

    }

    @DELETE
    public ServiceResponse<Void> disposeContainer(@PathParam("version") String version) {

        KieContainerResource container = lookupContainer(version);
        return kieServices.disposeContainer(container.getContainerId());

    }

}
