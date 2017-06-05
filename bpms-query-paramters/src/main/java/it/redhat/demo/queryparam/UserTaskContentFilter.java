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
public class UserTaskContentFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(UserTaskContentFilter.class);

    private Map<String, Object> parameters;
    private boolean built = false;

    public UserTaskContentFilter(Map<String, Object> parameters) {
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

        String paramName = (String) parameters.get("paramName");
        Object paramValue = parameters.get("paramValue");
        List<Object> multiParamValue = null;

        // param value could be both single than multi value
        if (paramValue instanceof List<?>) {
            multiParamValue = (List) paramValue;
        }

        LOG.info("user: {}", user);
        LOG.info("status: {}", status);
        LOG.info("groups: {}", groups);
        LOG.info("paramName: {}", paramName);
        LOG.info("paramValue: {}", paramValue);

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

        // exclOwnerIsTheUser == user
        ColumnFilter exclOwnerIsTheUser = equalsTo("exclOwner", user);

        // xname == paramName
        ColumnFilter paramNameCondition = equalsTo("xname", paramName);

        ColumnFilter paramValueCondition = null;

        if (multiParamValue != null) {
            // xvalue in :paramValue
            paramValueCondition = in("xvalue", multiParamValue);
        } else {
            // xvalue == :paramValue
            paramValueCondition = equalsTo("xvalue", paramValue.toString());
        }

        built = true;

        return AND(
            OR(actualOwnerIsTheUser, actualOwnerIsNull),
            statusIsOk,
            OR(potOwnerIsTheUser, potOwnerIsAGroup),
            OR(exclOwnerIsNull, NOT(exclOwnerIsTheUser)),
            paramNameCondition,
            paramValueCondition
        );

    }

}
