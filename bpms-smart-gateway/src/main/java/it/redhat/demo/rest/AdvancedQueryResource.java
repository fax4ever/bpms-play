package it.redhat.demo.rest;

import it.redhat.demo.exception.QueryDefinitionNotFoundException;
import it.redhat.demo.query.QueryProducer;
import it.redhat.demo.query.QuerySelector;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Path("queries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdvancedQueryResource {

    public static final int MAX_ROWS = 100000;

    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private QuerySelector querySelector;

    @GET
    public List<QueryDefinition> getQueryDefinitions() {

        return queryServices.getQueries(0, 10000);

    }

    @GET
    @Path("{query}")
    public List executeQuery(@PathParam("query") String query) {

        QueryDefinition definition = querySelector.selectQuery(query);

        if (QueryProducer.PROCESS.equals(definition.getTarget())) {

            return queryServices.query(query, query, 0, MAX_ROWS, ProcessInstance.class);

        } else if (QueryProducer.TASK.equals(definition.getTarget())) {

            return queryServices.query(query, query, 0, MAX_ROWS, TaskInstance.class);

        } else if (QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS.equals(query)) {

            // active task on completed task
            return queryServices.query(QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS, QueryServicesClient.QUERY_MAP_TASK, 0, MAX_ROWS, TaskInstance.class);

        } else if (QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES.equals(query)) {

            QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                    .notEqualsTo("actualowner", "giacomo")
                    .get();

            // active task on completed task with variables
            return queryServices.query(QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES,
                    QueryServicesClient.QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

        } else if (QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES.equals(query)) {

            QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                    .equalsTo("originalowner", "giacomo")
                    .addColumnMapping("originalowner", "string")
                    .get();

            return queryServices.query(QueryProducer.ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES,
                    QueryServicesClient.QUERY_MAP_TASK_WITH_CUSTOM_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

        } else if (QueryProducer.WAIT_TASK_FOR_USER_PROCESS_INSTANCE.equals(query)) {

            QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                    .equalsTo("originalowner", "giacomo")
                    .addColumnMapping("originalowner", "string")
                    .addColumnMapping("groupid", "string")
                    .get();

            return queryServices.query(QueryProducer.WAIT_TASK_FOR_USER_PROCESS_INSTANCE,
                    QueryServicesClient.QUERY_MAP_TASK_WITH_CUSTOM_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

        }

        throw new QueryDefinitionNotFoundException(query);

    }

}
