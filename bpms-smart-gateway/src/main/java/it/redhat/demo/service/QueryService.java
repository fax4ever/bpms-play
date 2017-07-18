package it.redhat.demo.service;

import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static it.redhat.demo.query.QueryProducer.*;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_PI_WITH_VARS;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK_WITH_VARS;

/**
 * Created by fabio.ercoli@redhat.com on 18/07/2017.
 */

@ApplicationScoped
public class QueryService {

    public static final String[] POT_OWNED_STATUS = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
    public static final int ARBITRARY_LONG_VALUE = 10000;

    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private Logger log;

    public List<Long> potOwnedTasksByVariablesAndParams(String user, List<String> groups, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("groups", groups);

        parameters.put("status", Arrays.asList(POT_OWNED_STATUS));

        if (paramsMap != null) {
            parameters.put("paramsMap", paramsMap);
        }

        if (variablesMap != null) {
            parameters.put("variablesMap", variablesMap);
        }

        List<TaskInstance> taskWithDuplicates = queryServices.query(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS, QUERY_MAP_TASK, "potOwnedTasksByVariablesAndParamsFilter", parameters, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);
        List<Long> ids = taskWithDuplicates.stream().map(taskInstance -> taskInstance.getId()).distinct().collect(Collectors.toList());

        log.debug("AQ: potOwnedTasksByVariablesAndParams - task ids: {}", ids);

        return ids;

    }

    public List<Long> notPotOwnedTasksForWorkedProcessInstance(String user, List<String> groups) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
            .notEqualsTo("actualOwner", user)
            .notEqualsTo("potOwner", user)
            .notEqualsTo("status", "Completed")
            .equalsTo("ostatus", "Completed")
            .equalsTo("oactualOwner", user)
            .get();

        List<List> query = queryServices.query(NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE, "RawList7", queryFilterSpec, 0, ARBITRARY_LONG_VALUE, List.class);

        // we need to exclude all tasks potentially owned by group membership
        Set<Long> taskOwnedByGroupMembership = query.stream().filter(taskItems -> {

            String actualOwner = (String) taskItems.get(2);
            String potOwner = (String) taskItems.get(3);

            return (actualOwner == "" && groups.contains(potOwner));
        })
        .map(taskItems -> ((Double) taskItems.get(0)).longValue())
        .collect(Collectors.toSet());

        List<Long> ids = query.stream()
            .map(taskItems -> ((Double) taskItems.get(0)).longValue())
            .distinct()
            .filter(id -> !taskOwnedByGroupMembership.contains(id))
            .collect(Collectors.toList());

        log.debug("AQ: notPotOwnedTasksForWorkedProcessInstance - task ids: {}", ids);

        return ids;
    }

    public List<TaskInstance> getAllTaskInputInstancesWithVariables(List<Long> taskIds, boolean asc) {

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
            .in("taskid", taskIds)
            .oderBy("taskid", asc)
            .get();

        List<TaskInstance> taskInstances = queryServices.query(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES,
                QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);

        log.debug("AQ: getAllTaskInputInstancesWithVariables - tasks: {}", taskInstances);

        return taskInstances;

    }

    public List<ProcessInstance> processInstanceWithVariables(List<Long> processIds) {

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
            .in("id", processIds)
            .get();

        List<ProcessInstance> taskInstances = queryServices.query(QUERY_MAP_PI_WITH_VARS,
                QUERY_MAP_PI_WITH_VARS, queryFilterSpec, 0, ARBITRARY_LONG_VALUE, ProcessInstance.class);

        log.debug("AQ: getAllTaskInputInstancesWithVariables - tasks: {}", taskInstances);

        return taskInstances;

    }

}
