package it.redhat.demo.rest;

import it.redhat.demo.stateless.BpmsProcessStateless;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Path("")
public class BpmsServiceRest {

    @Inject
    private BpmsProcessStateless service;

    @POST
    @Path("{container}/{definition}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Long startProcess(@PathParam("container") String container, @PathParam("definition") String definition, Map<String, Object> payload) {

        return service.startProcess(container, definition, payload);

    }

    @PUT
    @Path("{container}/{processInstance}/{signalName}")
    public void sendSignal(@PathParam("container") String container, @PathParam("processInstance") Long pi, @PathParam("signalName") String name, String value) {

        service.sendSignal(container, pi, name, value);

    }

}
