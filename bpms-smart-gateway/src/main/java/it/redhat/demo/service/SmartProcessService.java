package it.redhat.demo.service;

import it.redhat.demo.compatator.LastVersionComparator;
import it.redhat.demo.exception.ProcessDefinitionNotFoundException;
import it.redhat.demo.exception.ProcessInstanceNotFoundException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Stateless
public class SmartProcessService {

    private static final int MAX_RESULT = 100000;

    @Inject
    private Logger log;

    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private ProcessServicesClient processServices;

    public List<ProcessDefinition> getProcessDefinitionsByProcessDefinitionId(String processDefinitionId) {

        return queryServices.findProcessesById(processDefinitionId);

    }

    public List<ProcessDefinition> getProcessDefinitions() {

        return queryServices.findProcesses(0, MAX_RESULT);

    }

    public Long startProcess(String processDefinitionId, HashMap<String, Object> params) {

        List<ProcessDefinition> definitions = getProcessDefinitionsByProcessDefinitionId(processDefinitionId);
        Optional<ProcessDefinition> lastProcessDefinition = definitions.stream().sorted(new LastVersionComparator()).findFirst();

        if (!lastProcessDefinition.isPresent()) {
            throw new ProcessDefinitionNotFoundException(processDefinitionId);
        }

        String containerId = lastProcessDefinition.get().getContainerId();
        log.info("starting process definition {} on container {}", processDefinitionId, containerId);

        return processServices.startProcess(containerId, processDefinitionId, params);

    }

    public List<ProcessInstance> findProcessInstances(String processDefId) {

        // only active process instance
        ArrayList<Integer> status = new ArrayList<>();
        status.add(1);

        return queryServices.findProcessInstancesByProcessId(processDefId, status, 0, MAX_RESULT);

    }

    public void abortProcessInstance(Long processInstanceId) {

        ProcessInstance processInstance = queryServices.findProcessInstanceById(processInstanceId);
        if (processInstance == null) {
            throw new ProcessInstanceNotFoundException(processInstanceId);
        }

        processServices.abortProcessInstance(processInstance.getContainerId(), processInstanceId);

    }

    public void abortAllProcessInstances(String processDefinitionId) {

        List<ProcessInstance> processInstances = findProcessInstances(processDefinitionId);

        processInstances.stream().collect(
            Collectors.groupingBy(ProcessInstance::getContainerId,
                Collectors.mapping(ProcessInstance::getId, Collectors.toList())
            )
        ).forEach((containerId, processInstanceIds) -> {
            processServices.abortProcessInstances(containerId, processInstanceIds);
        });

    }

    public Map<String, Object> getProcessInstanceVariables(Long processInstanceId) {

        ProcessInstance processInstance = queryServices.findProcessInstanceById(processInstanceId);
        return processServices.getProcessInstanceVariables(processInstance.getContainerId(), processInstanceId);

    }

}
