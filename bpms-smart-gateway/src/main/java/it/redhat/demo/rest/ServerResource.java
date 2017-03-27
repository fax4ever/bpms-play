package it.redhat.demo.rest;

import it.redhat.demo.service.ProcessDefinitionService;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.KieServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
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

    @Inject
    private ProcessDefinitionService processDefinitionService;

    @GET
    public ServiceResponse<KieServerInfo> getServerInfo() {

        return kieServices.getServerInfo();

    }

    @GET
    @Path("process/definitions")
    public List<ProcessDefinition> getProcessDefinitions() {

        return processDefinitionService.getProcessDefinitions();

    }

    @GET
    @Path("process/definitions/{processDefinitionId}")
    public List<ProcessDefinition> getProcessDefinitionsByProcessDefinitionId(@PathParam("processDefinitionId") String processDefinitionId) {

        return processDefinitionService.getProcessDefinitionsByProcessDefinitionId(processDefinitionId);

    }

    @POST
    @Path("process/definitions/{processDefinitionId}")
    public Long startProcess(@PathParam("processDefinitionId") String processDefintionId) {

        return processDefinitionService.startProcess(processDefintionId);

    }

}
