package it.redhat.demo.rest;

import it.redhat.demo.qualifier.PI;
import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.client.QueryServicesClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 27/03/17.
 */

@Path("queries")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AdvancedQueryResource {

    @Inject
    private QueryServicesClient queryServices;

    @Inject @PI
    private QueryDefinition processInstanceQuery;

    @GET
    public List<QueryDefinition> getQueryDefinitions() {

        return queryServices.getQueries(0, 10000);

    }

    @POST
    public void registerQuery() {

        queryServices.registerQuery(processInstanceQuery);

    }

    @PUT
    public void replaceQuery() {

        queryServices.replaceQuery(processInstanceQuery);

    }

    @GET
    @Path("result")
    public List<ProcessInstance> excetuteQuery() {

        return queryServices.query(processInstanceQuery.getName(), QueryServicesClient.QUERY_MAP_PI, 0, 10, ProcessInstance.class);

    }

}
