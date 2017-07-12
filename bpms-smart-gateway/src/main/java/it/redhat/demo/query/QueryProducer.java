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
    public static final String ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES = "activeTasksOnCompletedTasksWithCustomVariables";
    public static final String WAIT_TASK_FOR_USER_PROCESS_INSTANCE = "waitTaskForUserProcessInstance";
    public static final String ACTIVE_TASKS_FOR_GROUP = "activeTasksForGroup";
    public static final String ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED = "activeTasksForGroupInputParamContentFiltered";
    public static final String GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES = "getAllTaskInputInstancesWithVariables";
    public static final String POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS = "potOwnedTasksByVariablesAndParams";
    public static final String NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE = "notPotOwnedTasksForWorkedProcessInstance";

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

    @Produces
    @Named(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES)
    public QueryDefinition activeTasksOnCompletedTasksWithCustomVariables() {
        String expression =
        " select t.*, a.actualowner originalowner " +
        " from audittaskimpl a " +
        " inner join audittaskimpl t " +
        " on a.processinstanceid = t.processinstanceid " +
        " and a.status = 'Completed' " +
        " and not t.status = 'Completed' ";

        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_TASKS_ON_COMPLETED_TASKS_WITH_CUSTOM_VARIABLES);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;
    }

    @Produces
    @Named(WAIT_TASK_FOR_USER_PROCESS_INSTANCE)
    public QueryDefinition waitTaskForUserProcessInstance() {

        String expression =
            " select t.*, a.actualowner originalowner, tv.groupid " +
            " from audittaskimpl a " +
            " inner join audittaskimpl t " +
            " on a.processinstanceid = t.processinstanceid " +
            " inner join ( " +
            " 	select tv.taskId, tv.value groupid " +
            " 	from taskvariableimpl tv  " +
            " 	where tv.type = 0 " +
            " 	and tv.name = 'GroupId' " +
            " ) tv  " +
            " on (tv.taskId = t.taskId) " +
            " where a.status = 'Completed' " +
            " and not t.status = 'Completed' ";

        QueryDefinition query = new QueryDefinition();
        query.setName(WAIT_TASK_FOR_USER_PROCESS_INSTANCE);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;

    }

    @Produces
    @Named(ACTIVE_TASKS_FOR_GROUP)
    public QueryDefinition activeTasksForGroup() {

        String expression =
            " select ti.*, tv.name tvname, tv.value tvvalue, pot.entity_id potowner, ex.entity_id exclowner " +
            " from AuditTaskImpl ti " +
            " inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv " +
            " on (tv.taskId = ti.taskId) " +
            " inner join peopleassignments_potowners pot " +
            " on (pot.task_id = ti.taskId) " +
            " left join peopleassignments_exclowners ex " +
            " on (ex.task_id = ti.taskId) ";

        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_TASKS_FOR_GROUP);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;

    }

    @Produces
    @Named(ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED)
    public QueryDefinition activeTasksForGroupInputParamContentFiltered() {

        String expression =
                " select ti.*, tv.name tvname, tv.value tvvalue, pot.entity_id potowner, ex.entity_id exclowner, x.name xname, x.value xvalue " +
                " from AuditTaskImpl ti " +
                " inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv " +
                " on (tv.taskId = ti.taskId) " +
                " inner join peopleassignments_potowners pot " +
                " on (pot.task_id = ti.taskId) " +
                " left join peopleassignments_exclowners ex " +
                " on (ex.task_id = ti.taskId) " +
                " inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) x " +
                " on (x.taskId = ti.taskId) ";

        QueryDefinition query = new QueryDefinition();
        query.setName(ACTIVE_TASKS_FOR_GROUP_INPUT_PARAM_CONTENT_FILTERED);
        query.setSource(SOURCE);
        query.setExpression(expression);
        query.setTarget(CUSTOM);

        return query;

    }

    @Produces
    @Named(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES)
    public QueryDefinition getAllTaskInputInstancesWithVariables() {

        QueryDefinition query = new QueryDefinition();
        query.setName(GET_ALL_TASK_INPUT_INSTANCES_WITH_VARIABLES);
        query.setSource(SOURCE);
        query.setExpression("select ti.*, tv.name tvname, tv.value tvvalue "+
                "from AuditTaskImpl ti " +
                "inner join (select tv.taskId, tv.name, tv.value from TaskVariableImpl tv where tv.type = 0 ) tv "+
                "on (tv.taskId = ti.taskId)");
        query.setTarget(CUSTOM);

        return query;

    }

    @Produces
    @Named(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS)
    public QueryDefinition potOwnedTasksByVariablesAndParams() {

        QueryDefinition query = new QueryDefinition();
        query.setName(POT_OWNED_TASKS_BY_VARIABLES_AND_PARAMS);
        query.setSource(SOURCE);
        query.setExpression(" select task.taskid, task.actualowner, task.status, param.name paramname, param.value paramvalue, variable.variableid variablename, variable.value variablevalue, pot.entity_id potowner, ex.entity_id exclowner " +
                " from audittaskimpl task " +
                " inner join ( " +
                "       select param.taskid, param.name, param.value  " +
                "       from taskvariableimpl param  " +
                "       where param.type = 0  " +
                " ) param " +
                " on (param.taskid = task.taskid) " +
                " inner join peopleassignments_potowners pot " +
                " on (pot.task_id = task.taskid) " +
                " left join peopleassignments_exclowners ex " +
                " on (ex.task_id = task.taskid) " +
                " inner join variableinstancelog variable " +
                " on (variable.processinstanceid = task.processinstanceid) " +
                " inner join ( " +
                "       select vil.processinstanceid ,vil.variableid, max(vil.id) maxvilid " +
                "       from variableinstancelog vil " +
                "       group by vil.processinstanceid, vil.variableid " +
                "       order by vil.processinstanceid " +
                " ) x " +
                " on (variable.variableid = x.variableid and variable.id = x.maxvilid) ");
        query.setTarget(CUSTOM);

        return query;

    }

    @Produces
    @Named(NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE)
    public QueryDefinition notPotOwnedTasksForWorkedProcessInstance() {

        QueryDefinition query = new QueryDefinition();
        query.setName(NOT_POT_OWNED_TASKS_FOR_WORKED_PROCESS_INSTANCE);
        query.setSource(SOURCE);
        query.setExpression(" select task.taskid, task.status, task.actualowner, pot.entity_id potowner, " +
                " other.status ostatus, other.actualowner oactualowner " +
                " from audittaskimpl task " +
                " inner join audittaskimpl other " +
                " on task.processinstanceid = other.processinstanceid " +
                " inner join peopleassignments_potowners pot " +
                " on pot.task_id = task.taskid ");
        query.setTarget(CUSTOM);

        return query;

    }

}
