package it.redhat.demo.queryparam;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.dashbuilder.dataset.filter.FilterFactory.*;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class NotPotOwnedTasksForWorkedProcessInstanceFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(NotPotOwnedTasksForWorkedProcessInstanceFilter.class);

    private Map<String, Object> parameters;
    private boolean built = false;

    public NotPotOwnedTasksForWorkedProcessInstanceFilter(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public ColumnFilter build() {

        if (built) {
            return null;
        }

        // extract mandatory parameters
        String user = (String) parameters.get("user");
        List<String> groups = (List<String>) parameters.get("groups");

        LOG.debug("user: {}", user);
        LOG.debug("groups: {}", groups);

        // base filter based on common pot owner criteria
        ColumnFilter potOwnerFilter = AND(
            OR(equalsTo("actualOwner", user), equalsTo("actualOwner", "")),
            OR(equalsTo("potOwner", user), in("potOwner", groups)),
            OR(isNull("exclOwner"), NOT(equalsTo("exclOwner", user)))
        );

        potOwnerFilter = AND(
            NOT(potOwnerFilter),
            NOT(equalsTo("status", "Completed")),
            equalsTo("ostatus", "Completed"),
            equalsTo("oactualOwner", user)
        );

        built = true;

        LOG.debug("filter instance: {}", potOwnerFilter);

        return potOwnerFilter;

    }

}
