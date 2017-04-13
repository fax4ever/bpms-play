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
    public static final String SOURCE = "java:jboss/datasources/jbpm";

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_PI)
    public QueryDefinition processInstance() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_PI);
        query.setSource(SOURCE);
        query.setExpression("select * from processinstancelog");
        query.setTarget(PROCESS);

        return query;

    }

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_PI_WITH_VARS)
    public QueryDefinition processInstanceWithVariables() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_PI_WITH_VARS);
        query.setSource(SOURCE);

        query.setExpression("select pil.*, v.variableId, v.value " +
                "from ProcessInstanceLog pil " +
                "INNER JOIN (select vil.processInstanceId ,vil.variableId, MAX(vil.ID) maxvilid  FROM VariableInstanceLog vil " +
                "GROUP BY vil.processInstanceId, vil.variableId ORDER BY vil.processInstanceId)  x " +
                "ON (v.variableId = x.variableId  AND v.id = x.maxvilid )" +
                "INNER JOIN VariableInstanceLog v " +
                "ON (v.processInstanceId = pil.processInstanceId)");

        query.setTarget(PROCESS);

        return query;

    }

    @Produces
    @Named(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS)
    public QueryDefinition getQueryDefinition() {

        QueryDefinition query = new QueryDefinition();
        query.setName(QueryServicesClient.QUERY_MAP_TASK_WITH_VARS);
        query.setSource(SOURCE);
        query.setExpression("select ti.*, tv.name tvname, tv.value tvvalue "+
                "from AuditTaskImpl ti " +
                "inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv "+
                "on (tv.taskId = ti.taskId)");
        query.setTarget(TASK);

        return query;

    }

}
