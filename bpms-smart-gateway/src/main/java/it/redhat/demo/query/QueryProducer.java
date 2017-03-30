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

    public static final String PROCESS = "PROCESS";
    public static final String TASK = "TASK";

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_PI)
    public QueryDefinition processInstance() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_PI);
        query.setSource("java:jboss/datasources/ExampleDS");
        query.setExpression("select * from processinstancelog");
        query.setTarget(PROCESS);

        return query;

    }

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS)
    public QueryDefinition getQueryDefinition() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS);
        query.setSource("java:jboss/datasources/ExampleDS");
        query.setExpression("select ti.*, mv.name as TVNAME, mv.value as TVVALUE from AuditTaskImpl ti inner join TaskVariableImpl mv on (mv.taskid = ti.taskId)");
        query.setTarget(TASK);

        return query;

    }

}
