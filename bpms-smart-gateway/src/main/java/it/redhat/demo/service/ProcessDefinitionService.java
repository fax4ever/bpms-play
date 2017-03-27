package it.redhat.demo.service;

import it.redhat.demo.compatator.LastVersionComparator;
import it.redhat.demo.exception.SmartGatewayException;
import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Stateless
public class ProcessDefinitionService {

    private static final int MAX_RESULT = 100;

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

    public Long startProcess(String processDefinitionId) {

        List<ProcessDefinition> definitions = getProcessDefinitionsByProcessDefinitionId(processDefinitionId);
        Optional<ProcessDefinition> lastProcessDefinition = definitions.stream().sorted(new LastVersionComparator()).findFirst();

        if (!lastProcessDefinition.isPresent()) {
            throw new SmartGatewayException("process definition id not present: " + processDefinitionId);
        }

        String containerId = lastProcessDefinition.get().getContainerId();
        log.info("starting process definition {} on container {}", processDefinitionId, containerId);

        return processServices.startProcess(containerId, processDefinitionId);

    }

}
