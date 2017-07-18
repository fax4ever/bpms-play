package it.redhat.demo.service;

import it.redhat.demo.model.Page;
import it.redhat.demo.model.TaskInstanceWithVariables;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.TaskInstance;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */

@Stateless
public class PagedQueryService {

    @Inject
    private QueryService queryService;

    @Inject
    private PagedService pagedService;

    @Inject
    private Logger log;

    public Page<TaskInstance> potOwnedTasksByParam(String user, List<String> groups, String paramName, String paramValue, Integer page, Integer pageSize, boolean asc) {

        ArrayList<String> paramValues = new ArrayList<>();
        paramValues.add(paramValue);

        HashMap<String, List<String>> paramsMap = new HashMap<>();
        paramsMap.put(paramName, paramValues);

        return potOwnedTasksByVariablesAndParams(user, groups, paramsMap, null, page, pageSize, asc);

    }

    public Page<TaskInstanceWithVariables> potOwnedTasksByVariablesAndParamsWithVariablesAndParams(String user, List<String> groups, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap, Integer page, Integer pageSize, boolean asc) {

        List<Long> taskIds = queryService.potOwnedTasksByVariablesAndParams(user, groups, paramsMap, variablesMap);

        if (taskIds.isEmpty()) {
            return new Page<>(0, page, pageSize, asc, new ArrayList<>());
        }

        List<Long> pageIds = pagedService.extractPage(pageSize, page, asc, taskIds);

        if (pageIds.isEmpty()) {
            return new Page<>(taskIds.size(), page, pageSize, asc, new ArrayList<>());
        }

        List<TaskInstance> tasksWithParams = queryService.getAllTaskInputInstancesWithVariables(pageIds, asc);

        List<Long> processIds = tasksWithParams.stream().map(taskInstance -> taskInstance.getProcessInstanceId()).distinct().collect(Collectors.toList());
        List<ProcessInstance> processInstances = queryService.processInstanceWithVariables(processIds);
        Map<Long, Map<String, Object>> processInstanceVariables = processInstances.stream().collect(Collectors.toMap(ProcessInstance::getId, ProcessInstance::getVariables));

        List<TaskInstanceWithVariables> taskInstanceWithVariables = tasksWithParams.stream()
                .map(taskInstance -> new TaskInstanceWithVariables(taskInstance, processInstanceVariables.get(taskInstance.getProcessInstanceId())))
                .collect(Collectors.toList());

        return new Page<>(taskIds.size(), page, pageSize, asc, taskInstanceWithVariables);

    }

    public Page<TaskInstance> potOwnedTasksByVariablesAndParams(String user, List<String> groups, Map<String, List<String>> paramsMap, Map<String, List<String>> variablesMap, Integer page, Integer pageSize, boolean asc) {

        List<Long> taskIds = queryService.potOwnedTasksByVariablesAndParams(user, groups, paramsMap, variablesMap);

        if (taskIds.isEmpty()) {
            return new Page<>(0, page, pageSize, asc, new ArrayList<>());
        }

        List<Long> pageIds = pagedService.extractPage(pageSize, page, asc, taskIds);

        if (pageIds.isEmpty()) {
            return new Page<>(taskIds.size(), page, pageSize, asc, new ArrayList<>());
        }

        List<TaskInstance> tasksWithParams = queryService.getAllTaskInputInstancesWithVariables(pageIds, asc);

        return new Page<>(taskIds.size(), page, pageSize, asc, tasksWithParams);

    }

    public Page<TaskInstance> notPotOwnedTasksForWorkedProcessInstance(String user, List<String> groups, Integer page, Integer pageSize, boolean asc) {

        List<Long> taskIds = queryService.notPotOwnedTasksForWorkedProcessInstance(user, groups);

        if (taskIds.isEmpty()) {
            return new Page<>(0, page, pageSize, asc, new ArrayList<>());
        }

        List<Long> pageIds = pagedService.extractPage(pageSize, page, asc, taskIds);

        if (pageIds.isEmpty()) {
            return new Page<>(taskIds.size(), page, pageSize, asc, new ArrayList<>());
        }

        List<TaskInstance> tasksWithParams = queryService.getAllTaskInputInstancesWithVariables(pageIds, asc);

        return new Page<>(taskIds.size(), page, pageSize, asc, tasksWithParams);

    }

}
