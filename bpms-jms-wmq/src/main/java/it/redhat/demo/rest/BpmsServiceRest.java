package it.redhat.demo.rest;

import it.redhat.demo.jms.BpmsRequestSenderJms;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Path("")
public class BpmsServiceRest {

    @Inject
    private BpmsRequestSenderJms gateway;

    @POST
    @Path("{container}/{definition}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void startProcess(@PathParam("container") String container, @PathParam("definition") String definition, String payload) {

        gateway.startProcess(container, definition, payload);

    }

}
