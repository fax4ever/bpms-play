package it.redhat.demo.service;

import org.kie.server.api.model.definition.ProcessDefinition;
import org.kie.server.client.QueryServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Stateless
public class ProcessDefinitionService {

    private static final int MAX_RESULT = 100;

    @Inject
    private QueryServicesClient queryServices;

    public List<ProcessDefinition> getProcessDefinitionsByProcessDefinitionId(String processDefinitionId) {

        return queryServices.findProcessesById(processDefinitionId);

    }

    public List<ProcessDefinition> getProcessDefinitions() {

        return queryServices.findProcesses(0, MAX_RESULT);

    }

}
