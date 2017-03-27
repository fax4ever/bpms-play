package it.redhat.demo.rest;

import it.redhat.demo.service.ProcessDefinitionService;
import org.kie.server.api.model.KieServerInfo;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;

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

    public static final int MAX_SIZE = 1000;
    @Inject
    private KieServicesClient kieServices;

    @Inject
    private ProcessDefinitionService processDefinitionService;

    @Inject
    private QueryServicesClient queryServices;

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

    @GET
    @Path("queries")
    public List<QueryDefinition> getQueryDefinitions() {

        return queryServices.getQueries(0, MAX_SIZE);

    }

    @POST
    @Path("queries")
    public void registerQuery() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_PI);
        query.setSource("java:jboss/datasources/ExampleDS");
        query.setExpression("select * from processinstancelog");
        query.setTarget("PROCESS");

        queryServices.registerQuery(query);

    }

    @GET
    @Path("queries/result")
    public List<ProcessInstance> excetuteQuery() {

        return queryServices.query(QueryServicesClient.QUERY_MAP_PI, QueryServicesClient.QUERY_MAP_PI, 0, 10, ProcessInstance.class);

    }

}
