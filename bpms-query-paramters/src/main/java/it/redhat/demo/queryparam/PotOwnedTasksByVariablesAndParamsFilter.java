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
public class PotOwnedTasksByVariablesAndParamsFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(PotOwnedTasksByVariablesAndParamsFilter.class);

    private Map<String, Object> parameters;
    private boolean built = false;

    public PotOwnedTasksByVariablesAndParamsFilter(Map<String, Object> parameters) {
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

        // extract optional parameters
        List<String> status = (List<String>) parameters.get("status");
        Map<String, List<String>> paramsMap = (Map<String, List<String>>) parameters.get("paramsMap");
        Map<String, List<String>> variablesMap = (Map<String, List<String>>) parameters.get("variablesMap");

        LOG.debug("status: {}", status);
        LOG.debug("paramsMap: {}", paramsMap);
        LOG.debug("variablesMap: {}", variablesMap);

        if (status != null) {
            potOwnerFilter = AND(potOwnerFilter, in("status", status));
        }

        if (paramsMap != null) {
            potOwnerFilter = AND(potOwnerFilter, buildMapFilter(paramsMap, "paramname", "paramvalue"));
        }

        if (variablesMap != null) {
            potOwnerFilter = AND(potOwnerFilter, buildMapFilter(variablesMap, "variablename", "variablevalue"));
        }

        built = true;

        LOG.debug("filter instance: {}", potOwnerFilter);
        return potOwnerFilter;
    }

    private ColumnFilter buildMapFilter(Map<String, List<String>> map, String name, String value) {

        return OR(map.entrySet().stream()
            .map(entry -> AND(equalsTo(name, entry.getKey()), in(value, entry.getValue()))
        ).toArray(ColumnFilter[]::new));

    }

}
