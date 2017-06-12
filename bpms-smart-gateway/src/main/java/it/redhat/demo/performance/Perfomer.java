package it.redhat.demo.performance;

import it.redhat.demo.correlation.StringCorrelationKey;
import it.redhat.demo.producer.KieProducer;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.ProcessServicesClient;
import org.kie.server.client.UserTaskServicesClient;
import org.slf4j.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.*;

/**
 * Created by fabio.ercoli@redhat.com on 12/06/2017.
 */
@Stateless
public class Perfomer {

    public static final String USER_ID = "maurizio";
    public static final String CONTAINER_ID = "main";

    @Inject
    private Logger log;

    @Inject
    private KieProducer kieProducer;

    @Asynchronous
    public void go() {

        long start = new Date().getTime();

        KieServicesClient kieServices = kieProducer.getServiceClient(USER_ID);
        ProcessServicesClient processService = kieServices.getServicesClient(ProcessServicesClient.class);
        UserTaskServicesClient taskService = kieServices.getServicesClient(UserTaskServicesClient.class);

        String uuid = UUID.randomUUID().toString();

        Long processInstanceId = processService.startProcess(CONTAINER_ID, "it.redhat.demo.user-sub-process", new StringCorrelationKey(uuid));
        List<TaskSummary> tasks = taskService.findTasksAssignedAsPotentialOwner(USER_ID, 0, 1000);

        Optional<TaskSummary> task = tasks.stream().filter(taskSummary -> taskSummary.getProcessInstanceId().equals(processInstanceId)).findFirst();
        if (!task.isPresent()) {
            throw new RuntimeException("task not present");
        }

        TaskSummary taskSummary = task.get();

        taskService.claimTask(CONTAINER_ID, taskSummary.getId(), USER_ID);
        taskService.startTask(CONTAINER_ID, taskSummary.getId(), USER_ID);

        HashMap<String, Object> outputs = new HashMap<>();
        outputs.put("output", "this is my output");

        taskService.completeTask(CONTAINER_ID, taskSummary.getId(), USER_ID, outputs);

        long end = new Date().getTime();

        log.info("process instance completed in {}", end-start);

    }

}
