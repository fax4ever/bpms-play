package it.redhat.demo.rest;

import it.redhat.demo.query.QuerySelector;
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

    @Inject
    private QuerySelector querySelector;

    @GET
    public List<QueryDefinition> getQueryDefinitions() {

        return queryServices.getQueries(0, 10000);

    }

    @POST
    @Path("{query}")
    public void registerQuery(@PathParam("query") String query) {

        queryServices.registerQuery(querySelector.selectQuery(query));

    }

    @PUT
    @Path("{query}")
    public void replaceQuery(@PathParam("query") String query) {

        queryServices.replaceQuery(querySelector.selectQuery(query));

    }

    @GET
    @Path("{query}")
    public List<ProcessInstance> excetuteQuery(@PathParam("query") String query) {

        return queryServices.query(query, QueryServicesClient.QUERY_MAP_PI, 0, 10, ProcessInstance.class);

    }

}
