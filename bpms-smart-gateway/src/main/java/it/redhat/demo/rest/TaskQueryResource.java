package it.redhat.demo.rest;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.client.QueryServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Path("taskWithVars")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskQueryResource {

    @Inject
    private QueryServicesClient queryServices;

    @GET
    public List<QueryDefinition> getQueryDefinitions() {

        return queryServices.getQueries(0, 10000);

    }

    @POST
    public void registerQuery() {

        QueryDefinition query = getQueryDefinition();
        queryServices.registerQuery(query);

    }

    @PUT
    public void replaceQuery() {

        QueryDefinition query = getQueryDefinition();
        queryServices.replaceQuery(query);

    }

    @GET
    @Path("result")
    public List<TaskInstance> excetuteQuery() {

        List<TaskInstance> query = queryServices.query(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, 0, 10, TaskInstance.class);
        return query;

    }

    private QueryDefinition getQueryDefinition() {
        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS);
        query.setSource("java:jboss/datasources/ExampleDS");
        query.setExpression("select ti.*, mv.name as TVNAME, mv.value as TVVALUE from AuditTaskImpl ti inner join TaskVariableImpl mv on (mv.taskid = ti.taskId)");
        query.setTarget("CUSTOM");
        return query;
    }

}
