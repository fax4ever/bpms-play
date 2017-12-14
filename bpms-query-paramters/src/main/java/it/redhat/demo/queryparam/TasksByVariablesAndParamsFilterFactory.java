package it.redhat.demo.queryparam;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;

/**
 * Created by fabio.ercoli@redhat.com on 04/07/2017.
 */
public class TasksByVariablesAndParamsFilterFactory implements QueryParamBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TasksByVariablesAndParamsFilterFactory.class);
    public static final String FILTER_NAME = "tasksByVariablesAndParamsFilter";

    @Override
    public boolean accept(String identifier) {
        return FILTER_NAME.equalsIgnoreCase(identifier);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.debug("register query parameter builder --> {}", FILTER_NAME);
        return new TasksByVariablesAndParamsFilter(parameters);
    }

}
