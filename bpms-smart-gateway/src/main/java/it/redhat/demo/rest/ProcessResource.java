package it.redhat.demo.rest;

import it.redhat.demo.service.ProcessService;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Path("process")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProcessResource {

    @Inject
    private ProcessService processDefinitionService;

    @GET
    @Path("definitions")
    public List<ProcessDefinition> getProcessDefinitions() {

        return processDefinitionService.getProcessDefinitions();

    }

    @GET
    @Path("definitions/{processDefinitionId}")
    public List<ProcessDefinition> getProcessDefinitionsByProcessDefinitionId(@PathParam("processDefinitionId") String processDefinitionId) {

        return processDefinitionService.getProcessDefinitionsByProcessDefinitionId(processDefinitionId);

    }

    @POST
    @Path("definitions/{processDefinitionId}")
    public Long startProcess(@PathParam("processDefinitionId") String processDefintionId) {

        return processDefinitionService.startProcess(processDefintionId);

    }

    @GET
    @Path("instances")
    public List<ProcessInstance> getProcessInstances() {

        return processDefinitionService.findProcessInstances();

    }

}