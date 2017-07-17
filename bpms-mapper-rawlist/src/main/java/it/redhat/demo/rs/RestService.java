package it.redhat.demo.rs;

import it.redhat.demo.bpms.QueryStartup;
import org.kie.server.api.model.definition.QueryFilterSpec;
import org.kie.server.api.util.QueryFilterSpecBuilder;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by fabio.ercoli@redhat.com on 17/07/2017.
 */

@Stateless
@Path("")
public class RestService {

    @Inject
    private Logger log;

    @Inject
    private KieServicesClient kieServicesClient;

    @GET
    public String ciao() {
        return "ciao";
    }

    @GET
    @Path("rawlist")
    public List<List> testRawListQueryMapper() {

        QueryServicesClient queryServicesClient = kieServicesClient.getServicesClient(QueryServicesClient.class);

        QueryFilterSpec queryFilterSpec = new QueryFilterSpecBuilder().get();

        List<List> result = queryServicesClient.query(QueryStartup.ALL_PROCESS_INSTANCES, QueryServicesClient.QUERY_MAP_RAW, queryFilterSpec, 0, 10000, List.class);

        log.info("query result: {}", result);

        return result;

    }

}
