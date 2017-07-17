package it.redhat.demo.rs;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by fabio.ercoli@redhat.com on 17/07/2017.
 */

@Stateless
@Path("")
public class RestService {

    @GET
    public String ciao() {
        return "ciao";
    }

}
