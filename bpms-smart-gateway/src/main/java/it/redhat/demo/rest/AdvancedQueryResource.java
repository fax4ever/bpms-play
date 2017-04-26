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
import java.util.Arrays;
import java.util.List;

import static it.redhat.demo.query.QueryProducer.*;
import static org.kie.server.client.QueryServicesClient.*;

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

        }

        throw new QueryDefinitionNotFoundException(query);

    }

    @Path("name/" + ACTIVE_TASKS_ON_COMPLETED_TASKS)
    @GET
    public List activeTasksOnCompletedTasks() {

        return queryServices.query(ACTIVE_TASKS_ON_COMPLETED_TASKS,
                QUERY_MAP_TASK, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES)
    @GET
    public List activeTasksOnCompletedTasksWithVariables() {

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                .notEqualsTo("actualowner", "giacomo")
                .get();

        return queryServices.query(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES,
                QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES)
    @GET
    public List activeTasksOnCompletedTasksWithCustomVariables() {

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                .equalsTo("originalowner", "giacomo")
                .addColumnMapping("originalowner", "string")
                .get();

        return queryServices.query(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES,
                QUERY_MAP_TASK_WITH_CUSTOM_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + WAIT_TASK_FOR_USER_PROCESS_INSTANCE)
    @GET
    public List waitTaskForUserProcessInstance() {

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                .equalsTo("originalowner", "giacomo")
                .addColumnMapping("originalowner", "string")
                .addColumnMapping("groupid", "string")
                .get();

        return queryServices.query(WAIT_TASK_FOR_USER_PROCESS_INSTANCE, QUERY_MAP_TASK_WITH_CUSTOM_VARS, queryFilterSpec,
                0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_FOR_GROUP)
    @GET
    public List activeTasksForGroup() {

        String[] validStatus = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
        String[] validGroups = {"Manager"};

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
            .addColumnMapping("potowner", "string")

            .in("potowner", Arrays.asList(validGroups))
            .in("status", Arrays.asList(validStatus))
            .oderBy("id", false)

            .get();

        return queryServices.query(ACTIVE_TASKS_FOR_GROUP, QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, MAX_ROWS, TaskInstance.class);

    }



}
