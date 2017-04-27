package it.redhat.demo.rest;

import it.redhat.demo.jms.ClearQueue;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Path("")
public class BpmsServiceRest {

    @Inject
    private BpmsService service;

    @Inject
    private ClearQueue clearQueue;

    @POST
    @Path("{container}/{definition}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void startProcess(@PathParam("container") String container, @PathParam("definition") String definition, String payload) {

        service.startProcess(container, definition, payload);

    }

    @PUT
    @Path("{container}/{processInstance}/{signalName}")
    public void sendSignal(@PathParam("container") String container, @PathParam("processInstance") Long pi, @PathParam("signalName") String name, String value) {

        service.sendSignal(container, pi, name, value);

    }

    @DELETE
    @Path("request")
    public int clearRequestQueue() {

        return clearQueue.purgeRequest();

    }

    @DELETE
    @Path("response")
    public int clearResponseQueue() {

        return clearQueue.purgeResponse();

    }

}
