package it.redhat.demo.bpms;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.QueryServicesClient;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Singleton
@Startup
public class QueryStartup {

    public static final String SOURCE = "java:jboss/datasources/jbpm";
    public static final String ALL_PROCESS_INSTANCES = "allProcessInstances";

    @Inject
    private KieServicesClient kieServicesClient;

    @PostConstruct
    private void postConstruct() {

        QueryDefinition query = new QueryDefinition();
        query.setName(ALL_PROCESS_INSTANCES);
        query.setSource(SOURCE);
        query.setExpression("select * from processinstancelog");
        query.setTarget("PROCESS");

        kieServicesClient.getServicesClient(QueryServicesClient.class).replaceQuery(query);

    }

}
