package it.redhat.demo.service;

import it.redhat.demo.compatator.LastVersionComparator;
import it.redhat.demo.exception.SmartGatewayException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.redhat.demo.rest.ProcessResource.PROCESS_DEF_ID;

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
            throw new SmartGatewayException("process definition id not present: " + processDefinitionId);
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
            throw new SmartGatewayException("process instance id not present: " + processInstanceId);
        }

        processServices.abortProcessInstance(processInstance.getContainerId(), processInstanceId);

    }

    public void abortAllProcessInstances(String processDefinitionId) {

        List<ProcessInstance> processInstances = findProcessInstances(PROCESS_DEF_ID);

        processInstances.stream().collect(
            Collectors.groupingBy(ProcessInstance::getContainerId,
                Collectors.mapping(ProcessInstance::getId, Collectors.toList())
            )
        ).forEach((containerId, processInstanceIds) -> {
            processServices.abortProcessInstances(containerId, processInstanceIds);
        });

    }

}
