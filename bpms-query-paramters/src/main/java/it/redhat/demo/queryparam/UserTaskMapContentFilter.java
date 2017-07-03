package it.redhat.demo.queryparam;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static org.dashbuilder.dataset.filter.FilterFactory.*;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskMapContentFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(UserTaskMapContentFilter.class);

    private Map<String, Object> parameters;
    private boolean built = false;

    public UserTaskMapContentFilter(Map<String, Object> parameters) {
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
        Map<String, List<String>> paramMap = (Map<String, List<String>>) parameters.get("paramMap");

        LOG.info("user: {}", user);
        LOG.info("status: {}", status);
        LOG.info("groups: {}", groups);
        LOG.info("paramMap: {}", paramMap);

        // actualOwner == :user
        ColumnFilter actualOwnerIsTheUser = equalsTo("actualOwner", user);

        // actualOwner == ''
        ColumnFilter actualOwnerIsNull = equalsTo("actualOwner", "");

        // status in :status
        ColumnFilter statusIsOk = in("status", status);

        // potOwner == :user
        ColumnFilter potOwnerIsTheUser = equalsTo("potOwner", user);

        // potOwner in :groups
        ColumnFilter potOwnerIsAGroup = in("potOwner", groups);

        // exclOwner == null
        ColumnFilter exclOwnerIsNull = isNull("exclOwner");

        // exclOwnerIsTheUser == :user
        ColumnFilter exclOwnerIsTheUser = equalsTo("exclOwner", user);

        ColumnFilter[] columnFilters = new ColumnFilter[paramMap.size()];
        int i=0;

        for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {

            String paramName = entry.getKey();
            List<String> paramValues = entry.getValue();

            // xname == :paramName
            ColumnFilter paramNameCondition = equalsTo("xname", paramName);
            // xvalue in :paramValues
            ColumnFilter paramValueCondition = in("xvalue", paramValues);

            columnFilters[i] = AND(paramNameCondition, paramValueCondition);
            i++;

        }

        built = true;

        return AND(
            OR(actualOwnerIsTheUser, actualOwnerIsNull),
            statusIsOk,
            OR(potOwnerIsTheUser, potOwnerIsAGroup),
            OR(exclOwnerIsNull, NOT(exclOwnerIsTheUser)),
            OR(columnFilters)
        );

    }

}
