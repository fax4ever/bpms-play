package it.redhat.demo.service;

import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static it.redhat.demo.query.QueryProducer.GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES;
import static it.redhat.demo.query.QueryProducer.POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK;
import static org.kie.server.client.QueryServicesClient.QUERY_MAP_TASK_WITH_VARS;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */

@Stateless
public class PagedQueryService {

    public static final int ARBITRARY_LONG_VALUE = 10000;
    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private Logger log;

    public List<TaskInstance> potOwnedTasksByVariablesAndParams(String user, List<String> groups, List<String> status, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap, Integer page, Integer pageSize) {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("user", user);
        parameters.put("groups", groups);

        if (status != null) {
            parameters.put("status", status);
        }

        if (paramsMap != null) {
            parameters.put("paramsMap", paramsMap);
        }

        if (variablesMap != null) {
            parameters.put("variablesMap", variablesMap);
        }

        List<TaskInstance> taskWithDuplicates = queryServices.query(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS, QUERY_MAP_TASK, "potOwnedTasksByVariablesAndParamsFilter", parameters, 0, ARBITRARY_LONG_VALUE, TaskInstance.class);
        log.info("taskWithDuplicates: {}", taskWithDuplicates);

        List<Long> ids = taskWithDuplicates.stream().map(taskInstance -> taskInstance.getId()).distinct().collect(Collectors.toList());
        log.info("ids: {}", ids);

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder()
                .in("taskid", ids)
                .get();

        return queryServices.query(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES,
                QUERY_MAP_TASK_WITH_VARS, queryFilterSpec, 0, 10000, TaskInstance.class);

    }

}
