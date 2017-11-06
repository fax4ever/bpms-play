package it.redhat.demo.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import it.redhat.demo.service.SmartProcessService;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;

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

    @POST
    @Path("definitions/selection/{times}")
    @Consumes(MediaType.TEXT_PLAIN)
    public Integer startProcessTimes(@QueryParam("curriculum") String curriculum, @PathParam("times") Integer times) {

        for (int i=0; i<times; i++) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("curriculum", curriculum + "-" + i);

            smartProcessService.startProcess(PROCESS_DEF_ID, params);
        }

        return times;

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
