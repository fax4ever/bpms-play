package it.redhat.demo.service;

import it.redhat.demo.producer.KieProducer;
import org.kie.api.task.model.Status;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Inject
    private QueryServicesClient queryServicesClient;

    public List<TaskSummary> getPotential(String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksAssignedAsPotentialOwner(username, 0, MAX_SIZE);

    }

    public List<TaskSummary> getOwned(String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksOwned(username, 0, MAX_SIZE);

    }

    public List<TaskSummary> getAllOwned(String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksOwned(username, getAllTaskStatus(), 0, MAX_SIZE);

    }

    public Map<String, Object> getTaskInputContentByTaskId(Long taskId) {

        TaskInstance taskInstance = taskServicesClient.findTaskById(taskId);
        return taskServicesClient.getTaskInputContentByTaskId(taskInstance.getContainerId(), taskId);

    }

    public void claimTask(String username, Long taskId) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        TaskInstance taskInstance = taskService.findTaskById(taskId);
        taskService.claimTask(taskInstance.getContainerId(), taskId, username);

    }

    public void startAndCompleteTask(String username, Long taskId, Map<String, Object> outputParams) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        TaskInstance taskInstance = taskService.findTaskById(taskId);
        taskService.startTask(taskInstance.getContainerId(), taskId, username);
        taskService.completeTask(taskInstance.getContainerId(), taskId, username, outputParams);

    }

    private UserTaskServicesClient getTaskServiceImpersonateUser(String username) {

        KieServicesClient kieServices = kieProducer.getServiceClient(username);
        return kieServices.getServicesClient(UserTaskServicesClient.class);

    }

    protected List<String> getAllTaskStatus() {

        return Arrays.stream(Status.values()).map(status -> status.name()).collect(Collectors.toList());

    }


}
