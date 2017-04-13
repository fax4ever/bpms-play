package it.redhat.demo.rest;

import it.redhat.demo.service.SmartTaskService;
import org.kie.server.api.model.instance.TaskSummary;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
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
    private SmartTaskService taskService;

    @GET
    @Path("{user}")
    public List<TaskSummary> getPotential(@PathParam("user") String username) {

        return taskService.getPotential(username);

    }

    @Path("{user}/owned")
    @GET
    public List<TaskSummary> getOwned(@PathParam("user") String username) {

        return taskService.getOwned(username);

    }

    @Path("{user}/all")
    @GET
    public List<TaskSummary> getAll(@PathParam("user") String username) {

        return taskService.getAllOwned(username);

    }

    @Path("input/{taskId}")
    @GET
    public Map<String, Object> getTaskInputContentByTaskId(@PathParam("taskId") Long taskId) {

        return taskService.getTaskInputContentByTaskId(taskId);

    }

    @Path("{taskId}/user/{user}/claim")
    @POST
    public void claim(@PathParam("taskId") Long taskId, @PathParam("user") String username) {

        taskService.claimTask(username, taskId);

    }

    @Path("{taskId}/user/{user}/complete/{feedback : OK|KO}")
    @POST
    public void complete(@PathParam("taskId") Long taskId, @PathParam("user") String username, @PathParam("feedback") String feedback) {

        HashMap<String, Object> outputParams = new HashMap<>();
        outputParams.put("feedback", feedback);

        taskService.startAndCompleteTask(username, taskId, outputParams);

    }

}
