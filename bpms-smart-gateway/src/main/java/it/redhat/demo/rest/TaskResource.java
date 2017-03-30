package it.redhat.demo.rest;

import it.redhat.demo.producer.KieProducer;
import org.kie.server.api.model.instance.TaskInstance;
import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.UserTaskServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Path("tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    private static final int MAX_SIZE = 10000;

    @Inject
    private KieProducer kieProducer;

    @Inject
    private UserTaskServicesClient taskServicesClient;

    @GET
    @Path("{user}")
    public List<TaskSummary> getPotential(@PathParam("user") String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksAssignedAsPotentialOwner(username, 0, MAX_SIZE);

    }

    @Path("{user}/owned")
    @GET
    public List<TaskSummary> getOwned(@PathParam("user") String username) {

        UserTaskServicesClient taskService = getTaskServiceImpersonateUser(username);
        return taskService.findTasksOwned(username, 0, MAX_SIZE);

    }

    @Path("input/{taskId}")
    @GET
    public Map<String, Object> getTaskInputContentByTaskId(@PathParam("taskId") Long taskId) {

        TaskInstance taskInstance = taskServicesClient.findTaskById(taskId);
        return taskServicesClient.getTaskInputContentByTaskId(taskInstance.getContainerId(), taskId);

    }

    private UserTaskServicesClient getTaskServiceImpersonateUser(String username) {

        KieServicesClient kieServices = kieProducer.getServiceClient(username);
        return kieServices.getServicesClient(UserTaskServicesClient.class);

    }


}
