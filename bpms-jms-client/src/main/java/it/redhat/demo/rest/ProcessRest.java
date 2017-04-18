package it.redhat.demo.rest;

import it.redhat.demo.stateless.KieServiceClientStateless;
import org.kie.server.client.KieServicesClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by fabio.ercoli@redhat.com on 18/04/17.
 */

@Path("")
public class ProcessRest {

    @Inject
    private KieServiceClientStateless service;

    @GET
    public String ciao() {

        return "ciao";

    }

    @GET
    @Path("test")
    public String test() {

        KieServicesClient client = service.getClient();

        return "test";

    }


}
