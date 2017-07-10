package it.redhat.demo.rest;

import it.redhat.demo.qualifier.ContaierV1;
import it.redhat.demo.qualifier.ContaierV2;
import it.redhat.demo.qualifier.ContaierV3;
import it.redhat.demo.qualifier.ContaierV5;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */
@Path("container")
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

    @Inject @ContaierV5
    private KieContainerResource kieContainerV5;

    @Inject
    private QueryServicesClient queryServices;

    private KieContainerResource lookupContainer(String version) {

        return  ("v1".equals(version)) ? kieContainerV1 :
                ("v2".equals(version)) ? kieContainerV2 :
                ("v2".equals(version)) ? kieContainerV3 :
                kieContainerV5;

    }

    @GET
    @Path("{version : v[1-5]}")
    public ServiceResponse<KieContainerResource> getContainer(@PathParam("version") String version) {

        return kieServices.getContainerInfo(lookupContainer(version).getContainerId());

    }


    @POST
    @Path("{version : v[1-5]}")
    public ServiceResponse<KieContainerResource> deployContainer(@PathParam("version") String version) {

        KieContainerResource container = lookupContainer(version);
        return kieServices.createContainer(container.getContainerId(), container);

    }

    @DELETE
    @Path("{version : v[1-5]}")
    public ServiceResponse<Void> disposeContainer(@PathParam("version") String version) {

        KieContainerResource container = lookupContainer(version);
        return kieServices.disposeContainer(container.getContainerId());

    }

    @GET
    public List<String> getContainers() {

        List<ProcessDefinition> processes = queryServices.findProcesses(0, 10000);
        return processes.stream()
                .map(processDefinition -> processDefinition.getContainerId())
                .distinct().collect(Collectors.toList());

    }

    @DELETE
    public void disposeOtherContainers() {

        getContainers().stream()
            .filter(container -> !container.contains("bpms-selection-process"))
            .forEach(container -> kieServices.disposeContainer(container));

    }

}
