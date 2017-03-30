package it.redhat.demo.query;

import org.kie.server.api.model.definition.QueryDefinition;
import org.kie.server.client.QueryServicesClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@ApplicationScoped
public class QueryProducer {

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_PI)
    public QueryDefinition processInstance() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_PI);
        query.setSource("java:jboss/datasources/ExampleDS");
        query.setExpression("select * from processinstancelog");
        query.setTarget("CUSTOM");

        return query;

    }



}
