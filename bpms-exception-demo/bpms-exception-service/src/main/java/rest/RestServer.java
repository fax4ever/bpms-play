package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fabio.ercoli@redhat.com on 14/01/17.
 */
@Path("")
public class RestServer {

    private static Logger log = LoggerFactory.getLogger(RestServer.class);

    @GET
    public String ciao() {

        log.info("ciao invoked");

        return "ciao";
    }

}
