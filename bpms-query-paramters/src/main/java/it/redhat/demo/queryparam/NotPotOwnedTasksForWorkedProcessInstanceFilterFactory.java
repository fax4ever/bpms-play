package it.redhat.demo.queryparam;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class NotPotOwnedTasksForWorkedProcessInstanceFilterFactory implements QueryParamBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NotPotOwnedTasksForWorkedProcessInstanceFilterFactory.class);
    public static final String FILTER_NAME = "notPotOwnedTasksForWorkedProcessInstanceFilter";

    @Override
    public boolean accept(String identifier) {
        return FILTER_NAME.equalsIgnoreCase(identifier);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.debug("register query parameter builder --> {}", FILTER_NAME);
        return new NotPotOwnedTasksForWorkedProcessInstanceFilter(parameters);
    }

}
