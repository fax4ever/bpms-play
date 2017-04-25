package it.redhat.demo.rest;

import it.redhat.demo.stateless.BpmsProcessStateless;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Path("")
public class BpmsServiceRest {

    @Inject
    private BpmsProcessStateless service;

    @GET
    @Path("{container}/{definition}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Long startProcess(@PathParam("container") String container, @PathParam("definition") String definition, Map<String, Object> payload) {

        return service.startProcess(container, definition, payload);

    }

}
