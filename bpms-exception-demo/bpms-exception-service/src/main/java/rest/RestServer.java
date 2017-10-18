package rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fabio.ercoli@redhat.com on 14/01/17.
 */
@Path("/")
public class RestServer {

    private static Logger log = LoggerFactory.getLogger(RestServer.class);

    @GET
    public String ciao() {
        log.info("ciao invoked");
        return "ciao";
    }
    
    @Path("/1")
    @GET
    public String ciao1() {
        log.info("ciao 1 invoked");
        return "ciao 1";
    }
    
    @Path("/2")
    @GET
    public String ciao2() {
        log.info("ciao 2 invoked");
        return "ciao 2";
    }
    
    @Path("/3")
    @GET
    public String ciao3() {
        log.info("ciao 3 invoked");
        return "ciao 3";
    }
    
    @Path("/slow")
    @GET
    public String ciaoSlow() throws InterruptedException {
        log.info("ciao slow start");
        
        Thread.sleep(10000l);
        
        log.info("ciao slow end");
        return "ciao slow";
    }
    
    @Path("/error")
    @GET
    public String ciaoError() {
    	
    	log.info("ciao error");
    	throw new RuntimeException();
    	
    }

}
