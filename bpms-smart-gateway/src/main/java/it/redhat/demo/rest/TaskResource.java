package it.redhat.demo.rest;

import org.kie.server.api.model.instance.TaskSummary;
import org.kie.server.client.UserTaskServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Path("tasks/{user}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {

    private static final int MAX_SIZE = 10000;

    @Inject
    private UserTaskServicesClient taskService;

    @GET
    public List<TaskSummary> getPotential(@PathParam("user") String username) {
        return taskService.findTasksAssignedAsPotentialOwner(username, 0, MAX_SIZE);
    }

    @Path("owned")
    @GET
    public List<TaskSummary> getOwned(@PathParam("user") String username) {
        return taskService.findTasksOwned(username, 0, MAX_SIZE);
    }


}
