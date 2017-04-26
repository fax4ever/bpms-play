package it.redhat.demo.rest;

import it.redhat.demo.jms.BpmsRequestSenderJms;
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
    private BpmsRequestSenderJms gateway;

    @Inject
    private ClearQueue clearQueue;

    @POST
    @Path("{container}/{definition}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void startProcess(@PathParam("container") String container, @PathParam("definition") String definition, String payload) {

        gateway.startProcess(container, definition, payload);

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
