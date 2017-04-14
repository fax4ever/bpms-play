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
    public static final String CUSTOM = "CUSTOM";

    public static final String SOURCE = "java:jboss/datasources/jbpm";

    // custom advanced queries
    public static final String ACTIVE_TASKS_ON_COMPLETED_TASKS = "activeTasksOnCompletedTasks";
    public static final String ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES = "activeTasksOnCompletedTasksWithVariables";

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

        query.setExpression(" select pil.*, v.variableId, v.value " +
                " from ProcessInstanceLog pil " +
                " INNER JOIN VariableInstanceLog v " +
                " ON (v.processInstanceId = pil.processInstanceId) " +
                " INNER JOIN ( " +
                " 	select vil.processInstanceId ,vil.variableId, MAX(vil.ID) maxvilid " +
                " 	FROM VariableInstanceLog vil " +
                " 	GROUP BY vil.processInstanceId, vil.variableId " +
                " 	ORDER BY vil.processInstanceId " +
                " ) x " +
                " ON (v.variableId = x.variableId  AND v.id = x.maxvilid) ");

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

    @Produces
    @Named(ACTIVE_TASKS_ON_COMPLETED_TASKS)
    public QueryDefinition activeTasksOnCompletedTasks() {
        String expression =
            " select t.* " +
            " from audittaskimpl a " +
            " inner join audittaskimpl t " +
            " on a.processinstanceid = t.processinstanceid " +
            " where a.actualowner = 'giacomo' " +
            " and a.status = 'Completed' " +
            " and not t.status = 'Completed' ";

        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_TASKS_ON_COMPLETED_TASKS);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;
    }

    @Produces
    @Named(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES)
    public QueryDefinition activeTasksOnCompletedTasksWithVariables() {
        String expression =
            " select ti.*, tv.name tvname, tv.value tvvalue " +
            " from ( " +
            " 	select t.* " +
            " 	from audittaskimpl a " +
            " 	inner join audittaskimpl t " +
            " 	on a.processinstanceid = t.processinstanceid " +
            " 	where a.actualowner = 'giacomo' " +
            " 	and a.status = 'Completed' " +
            " 	and not t.status = 'Completed' " +
            " ) ti  " +
            " inner join ( " +
            " 	select tv.taskId, tv.name, tv.value  " +
            " 	from taskvariableimpl tv  " +
            " 	where tv.type = 0  " +
            " ) tv  " +
            " on (tv.taskId = ti.taskId) ";

        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_VARIABLES);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;
    }

}
