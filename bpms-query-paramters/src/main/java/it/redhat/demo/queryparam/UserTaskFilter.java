package it.redhat.demo.queryparam;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;

import java.util.List;
import java.util.Map;

import static org.dashbuilder.dataset.filter.FilterFactory.*;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskFilter implements QueryParamBuilder<ColumnFilter> {

    /*
        # Task is owned by the user or is not owned by anyone
        (actualOwner = :userId or t.taskData.actualOwner is null)
        and
        # The status is active
        t.taskData.status in (:status)
        and
        # Is assigned direct on user or by group
        (potentialOwners.id  = :userId or potentialOwners.id in (:groupIds))
        and
        # The user is not excluded
        (t.peopleAssignments.excludedOwners is empty or excludedOwners.id != :userId)
     */

    private Map<String, Object> parameters;
    private boolean built = false;

    public UserTaskFilter(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public ColumnFilter build() {

        if (built) {
            return null;
        }

        // extract parameters
        String user = (String) parameters.get("user");
        List<String> status = (List<String>) parameters.get("status");
        List<String> groups = (List<String>) parameters.get("groups");

        // actualOwner == :user
        ColumnFilter actualOwnerIsTheUser = equalsTo("actualOwner", user);

        // actualOwner == null
        ColumnFilter actualOwnerIsNull = isNull("actualOwner");

        // status in :status
        ColumnFilter statusIsOk = in("status", status);

        // potOwner == :user
        ColumnFilter potOwnerIsTheUser = equalsTo("potOwner", user);

        // potOwner in :groups
        ColumnFilter potOwnerIsAGroup = in("potOwner", groups);

        // exclOwner == null
        ColumnFilter exclOwnerIsNull = isNull("exclOwner");

        // exclOwnerIsTheUser == user
        ColumnFilter exclOwnerIsTheUser = equalsTo("exclOwner", user);

        return AND(
            OR(actualOwnerIsTheUser, actualOwnerIsNull),
            statusIsOk,
            OR(potOwnerIsTheUser, potOwnerIsAGroup),
            OR(exclOwnerIsNull, NOT(exclOwnerIsTheUser))
        );

    }



}
