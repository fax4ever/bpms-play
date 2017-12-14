package it.redhat.demo.queryparam;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.services.api.query.QueryParamBuilder;

import static org.dashbuilder.dataset.filter.FilterFactory.AND;
import static org.dashbuilder.dataset.filter.FilterFactory.OR;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class TasksByVariablesAndParamsFilter implements QueryParamBuilder<ColumnFilter> {

    private static final Logger LOG = LoggerFactory.getLogger(TasksByVariablesAndParamsFilter.class);

    private Map<String, Object> parameters;
    private boolean built = false;

    public TasksByVariablesAndParamsFilter(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public ColumnFilter build() {

        if (built) {
            return null;
        }

        // extract parameters
        List<String> status = (List<String>) parameters.get("status");
        Map<String, List<String>> paramsMap = (Map<String, List<String>>) parameters.get("paramsMap");
        Map<String, List<String>> variablesMap = (Map<String, List<String>>) parameters.get("variablesMap");

        LOG.debug("status: {}", status);
        LOG.debug("paramsMap: {}", paramsMap);
        LOG.debug("variablesMap: {}", variablesMap);

        ColumnFilter filter = in("status", status);

        if (paramsMap != null) {
            filter = AND(filter, buildMapFilter(paramsMap, "paramname", "paramvalue"));
        }

        if (variablesMap != null) {
            filter = AND(filter, buildMapFilter(variablesMap, "variablename", "variablevalue"));
        }

        built = true;

        LOG.debug("filter instance: {}", filter);
        return filter;
    }

    private ColumnFilter buildMapFilter(Map<String, List<String>> map, String name, String value) {

        return OR(map.entrySet().stream()
            .map(entry -> AND(equalsTo(name, entry.getKey()), in(value, entry.getValue()))
        ).toArray(ColumnFilter[]::new));

    }

}
