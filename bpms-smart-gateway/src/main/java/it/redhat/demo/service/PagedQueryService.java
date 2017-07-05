package it.redhat.demo.service;

import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.redhat.demo.query.QueryProducer.GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES;
import static it.redhat.demo.query.QueryProducer.NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE;
import static it.redhat.demo.query.QueryProducer.POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK_WITH_VARS;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */

@Stateless
public class PagedQueryService {

    public static final String[] POT_OWNED_STATUS = {"Created", "Ready", "Reserved", "InProgress", "Suspended"};
    public static final int ARBITRARY_LONG_VALUE = 10000;

    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private Logger log;

    public Page<TaskInstance> potOwnedTasksByVariablesAndParams(String user, List<String> groups, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap, Integer page, Integer pageSize, Boolean asc) {

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

        if (asc == null) {
            asc = Boolean.TRUE;
        }

        List<TaskInstance> taskWithDuplicates = queryServices.query(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS, QUERY_MAP_TASK, "potOwnedTasksByVariablesAndParamsFilter", parameters, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);
        log.trace("taskWithDuplicates: {}", taskWithDuplicates);

        return getTaskInstancePage(pageSize, page, asc, taskWithDuplicates);

    }

    public Page<TaskInstance> potOwnedTasksByParam(String user, List<String> groups, String paramName, String paramValue, Integer page, Integer pageSize, boolean asc) {

        ArrayList<String> paramValues = new ArrayList<>();
        paramValues.add(paramValue);

        HashMap<String, List<String>> paramsMap = new HashMap<>();
        paramsMap.put(paramName, paramValues);

        return potOwnedTasksByVariablesAndParams(user, groups, paramsMap, null, page, pageSize, asc);

    }

    public Page<TaskInstance> notPotOwnedTasksForWorkedProcessInstance(String user, List<String> groups, Integer page, Integer pageSize, boolean asc) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("groups", groups);

        List<TaskInstance> taskWithDuplicates = queryServices.query(NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE, QUERY_MAP_TASK, "notPotOwnedTasksForWorkedProcessInstanceFilter", parameters, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);
        log.trace("taskWithDuplicates: {}", taskWithDuplicates);

        return getTaskInstancePage(pageSize, page, asc, taskWithDuplicates);

    }

    private Page<TaskInstance> getTaskInstancePage(Integer pageSize, Integer page, boolean asc, List<TaskInstance> taskWithDuplicates) {
        Stream<Long> distinct = taskWithDuplicates.stream().map(taskInstance -> taskInstance.getId()).distinct();
        List<Long> ids = (asc) ?
                distinct.sorted().collect(Collectors.toList()) :
                distinct.sorted(Collections.reverseOrder()).collect(Collectors.toList());

        log.trace("ids: {}", ids);

        int total = ids.size();
        int offset = page * pageSize;
        int limit = (page + 1) * pageSize;

        if (offset >= total) {
            return new Page<>(total, page, pageSize, asc);
        }

        ids = ids.subList(offset, Math.min(limit, total));

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                .in("taskid", ids)
                .oderBy("taskid", asc)
                .get();

        List<TaskInstance> taskInstances = queryServices.query(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES,
                QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);

        return new Page<>(total, page, pageSize, asc, taskInstances);
    }

}
