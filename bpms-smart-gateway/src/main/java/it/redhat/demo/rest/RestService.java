package it.redhat.demo.rest;

import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by fabio.ercoli@redhat.com on 24/03/17.
 */

@Path("")
public class RestService {

    @Inject
    private Logger log;

    @GET
    public String ciao() {

        log.info("invoke ciao");
        return "ciao";

    }

}
