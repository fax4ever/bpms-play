package it.redhat.demo.rest;

import it.redhat.demo.exception.QueryDefinitionNotFoundException;
import it.redhat.demo.query.QueryProducer;
import it.redhat.demo.query.QuerySelector;
import it.redhat.demo.model.Page;
import it.redhat.demo.service.PagedQueryService;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    @Inject
    private PagedQueryService pagedQueryService;

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
        String[] validGroups = {"HR"};

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", "giacomo");
        parameters.put("status", Arrays.asList(validStatus));
        parameters.put("groups", Arrays.asList(validGroups));

        return queryServices.query(ACTIVE_TASKS_FOR_GROUP, QUERY_MAP_TASK_WITH_VARS, "userTaskFilter", parameters, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED)
    @GET
    public List activeTasksForGroupInputParamContentFiltered() {

        String[] validStatus = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
        String[] validGroups = {"HR"};

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", "giacomo");
        parameters.put("status", Arrays.asList(validStatus));
        parameters.put("groups", Arrays.asList(validGroups));
        parameters.put("paramName", "curriculum");
        parameters.put("paramValue", "f739");

        return queryServices.query(ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED, QUERY_MAP_TASK_WITH_VARS, "userTaskContentFilter", parameters, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED + "/in")
    @GET
    public List activeTasksForGroupInputParamContentFilteredIn() {

        String[] validStatus = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
        String[] validGroups = {"HR"};
        String[] possibleParamValues = {"f739", "f731", "f733"};

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", "giacomo");
        parameters.put("status", Arrays.asList(validStatus));
        parameters.put("groups", Arrays.asList(validGroups));
        parameters.put("paramName", "curriculum");
        parameters.put("paramValue", possibleParamValues);

        return queryServices.query(ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED, QUERY_MAP_TASK_WITH_VARS, "userTaskContentFilter", parameters, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/" + ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED + "/map")
    @GET
    public List activeTasksForGroupInputParamContentFilteredMap() {

        String[] validStatus = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
        String[] validGroups = {"HR"};

        List<String> values1 = new ArrayList<>();
        values1.add("f739");

        List<String> values2 = new ArrayList<>();
        values2.add("ciao");
        values2.add("ciaone");

        HashMap<String, List<String>> paramMap = new HashMap<>();

        paramMap.put("curriculum", values1);
        paramMap.put("babusca", values2);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", "giacomo");
        parameters.put("status", Arrays.asList(validStatus));
        parameters.put("groups", Arrays.asList(validGroups));
        parameters.put("paramMap", paramMap);

        return queryServices.query(ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED, QUERY_MAP_TASK_WITH_VARS, "userTaskMapContentFilter", parameters, 0, MAX_ROWS, TaskInstance.class);

    }

    @Path("name/getAllTaskInputInstancesWithVariables/{page}/{pageSize}")
    @GET
    public List getAllTaskInputInstancesWithVariables(@PathParam("page") Integer page, @PathParam("pageSize") Integer pageSize) {

        return queryServices.query(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES,
                QUERY_MAP_TASK_WITH_VARS, page, pageSize, TaskInstance.class);

    }

    @Path("name/potOwnedTasksByVariablesAndParams/{offset}/{size}")
    @GET
    public List potOwnedTasksByVariablesAndParams(@PathParam("offset") Integer offset, @PathParam("size") Integer size) {

        String[] validStatus = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
        String[] validGroups = {"HR"};

        List<String> values1 = new ArrayList<>();
        values1.add("f711");

        List<String> values2 = new ArrayList<>();
        values2.add("true");

        HashMap<String, List<String>> paramsMap = new HashMap<>();

        paramsMap.put("curriculum", values1);
        paramsMap.put("Skippable", values2);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", "giacomo");
        parameters.put("status", Arrays.asList(validStatus));
        parameters.put("groups", Arrays.asList(validGroups));
        parameters.put("paramsMap", paramsMap);

        return queryServices.query(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS, QUERY_MAP_TASK, "potOwnedTasksByVariablesAndParamsFilter", parameters, offset, size, TaskInstance.class);

    }

    @Path("paged/{offset}/{size}")
    @GET
    public Page paged(@PathParam("offset") Integer offset, @PathParam("size") Integer size, @QueryParam("asc") Boolean asc) {

        String[] validGroups = {"HR"};

        return pagedQueryService.potOwnedTasksByVariablesAndParams("giacomo", Arrays.asList(validGroups), null, null, offset, size, asc);

    }

    @Path("full/{offset}/{size}")
    @GET
    public Page full(@PathParam("offset") Integer offset, @PathParam("size") Integer size, @QueryParam("asc") Boolean asc) {

        String[] validGroups = {"HR"};

        HashMap<String, List<String>> filterMap = new HashMap<>();
        ArrayList<String> values = new ArrayList<>();
        values.add("f731");
        filterMap.put("curriculum", values);

        return pagedQueryService.potOwnedTasksByVariablesAndParamsWithVariablesAndParams("giacomo", Arrays.asList(validGroups), filterMap, filterMap, offset, size, asc);

    }

    @Path("single/{offset}/{size}")
    @GET
    public Page potOwnedTasksByParam(@PathParam("offset") Integer offset, @PathParam("size") Integer size, @QueryParam("asc") Boolean asc) {

        String[] validGroups = {"HR"};

        return pagedQueryService.potOwnedTasksByParam("giacomo", Arrays.asList(validGroups), "curriculum", "f731", offset, size, asc);

    }

    @Path("notowned/{offset}/{size}")
    @GET
    public Page notPotOwnedTasksForWorkedProcessInstance(@PathParam("offset") Integer offset, @PathParam("size") Integer size, @QueryParam("asc") Boolean asc) {

        String[] validGroups = {"HR"};

        return pagedQueryService.notPotOwnedTasksForWorkedProcessInstance("giacomo", Arrays.asList(validGroups), offset, size, asc);

    }

}
