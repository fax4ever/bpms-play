package it.redhat.demo.query;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.KieServicesException;
import org.kie.server.client.QueryServicesClient;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@Singleton
@Startup
public class QueryStartup {

    private final static boolean REPLACE_ALWAYS = true;
    
    @Inject
    @Any
    private Instance<QueryDefinition> anyQueryDefinition;

    @Inject
    private QueryServicesClient queryServices;

    @Inject
    private Logger log;

    @PostConstruct
    private void postConstruct() {

        for (QueryDefinition definition : anyQueryDefinition) {

            if (REPLACE_ALWAYS) {
                queryServices.replaceQuery(definition);
            } else {
                defineUnknown(definition);
            }    
            
        }

    }

    private void defineUnknown(QueryDefinition definition) {
        
        try {

            queryServices.getQuery(definition.getName());

        } catch (KieServicesException ex) {

            log.info("Not found query definition {}", definition.getName());
            queryServices.replaceQuery(definition);
            log.info("Register or replace query definition {}", definition.getName());

        }
        
    }

}
