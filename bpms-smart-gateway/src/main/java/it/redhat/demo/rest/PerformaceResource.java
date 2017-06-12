package it.redhat.demo.rest;

import it.redhat.demo.performance.Perfomer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by fabio.ercoli@redhat.com on 12/06/2017.
 */

@Path("perf")
public class PerformaceResource {
    @Inject
    private Perfomer perfomer;

    @GET
    @Path("{times}")
    public Integer ciao(@PathParam("times") Integer times) {

        for (int i = 0; i< times; i++) {
            perfomer.go();
        }

        return times;

    }

}
