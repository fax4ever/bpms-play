package it.redhat.demo.rest;

import it.redhat.demo.service.SmartTaskService;
import org.kie.server.api.model.instance.TaskSummary;

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

    @Path("input/{taskId}")
    @GET
    public Map<String, Object> getTaskInputContentByTaskId(@PathParam("taskId") Long taskId) {

        return taskService.getTaskInputContentByTaskId(taskId);

    }


}
