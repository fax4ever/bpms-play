package it.redhat.demo.service;

import it.redhat.demo.producer.KieProducer;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 31/03/17.
 */

@Stateless
public class SmartTaskService {

    private static final int MAX_SIZE = 10000;

    @Inject
    private KieProducer kieProducer;

    @Inject
    private UserTaskServicesClient taskServicesClient;


    public List<TaskSummary> getPotential(String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksAssignedAsPotentialOwner(username, 0, MAX_SIZE);

    }

    public List<TaskSummary> getOwned(String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksOwned(username, 0, MAX_SIZE);

    }

    public Map<String, Object> getTaskInputContentByTaskId(Long taskId) {

        TaskInstance taskInstance = taskServicesClient.findTaskById(taskId);
        return taskServicesClient.getTaskInputContentByTaskId(taskInstance.getContainerId(), taskId);

    }

    private UserTaskServicesClient getTaskServiceImpersonateUser(String username) {

        KieServicesClient kieServices = kieProducer.getServiceClient(username);
        return kieServices.getServicesClient(UserTaskServicesClient.class);

    }


}
