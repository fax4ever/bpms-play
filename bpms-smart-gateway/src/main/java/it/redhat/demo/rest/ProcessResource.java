package it.redhat.demo.rest;

import it.redhat.demo.service.SmartProcessService;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Path("process")
@Produces(MediaType.APPLICATION_JSON)
public class ProcessResource {

    public final static String PROCESS_DEF_ID = "it.redhat.demo.selection-process";

    @Inject
    private SmartProcessService smartProcessService;

    @GET
    @Path("definitions")
    public List<ProcessDefinition> getProcessDefinitions() {

        return smartProcessService.getProcessDefinitions();

    }

    @GET
    @Path("definitions/selection")
    public List<ProcessDefinition> getProcessDefinitionsByProcessDefinitionId() {

        return smartProcessService.getProcessDefinitionsByProcessDefinitionId(PROCESS_DEF_ID);

    }

    @POST
    @Path("definitions/selection")
    @Consumes(MediaType.TEXT_PLAIN)
    public Long startProcess(String curriculum) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("curriculum", curriculum);

        return smartProcessService.startProcess(PROCESS_DEF_ID, params);

    }

    @DELETE
    @Path("definitions/selection")
    public void abortAllProcessInstances() {

        smartProcessService.abortAllProcessInstances(PROCESS_DEF_ID);

    }

    @GET
    @Path("instances")
    public List<ProcessInstance> getProcessInstances() {

        return smartProcessService.findProcessInstances(PROCESS_DEF_ID);

    }

    @DELETE
    @Path("instances/{id}")
    public void abortProcessInstance(@PathParam("id") Long id) {

        smartProcessService.abortProcessInstance(id);

    }

    @GET
    @Path("instances/variables/{id}")
    public Map<String, Object> getProcessInstanceVariables(@PathParam("id") Long processInstanceId) {

        return smartProcessService.getProcessInstanceVariables(processInstanceId);

    }

}
