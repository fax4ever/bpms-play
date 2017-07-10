package it.redhat.demo.queryparam;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskFilterFactory implements QueryParamBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(UserTaskFilterFactory.class);
    public static final String USER_TASK_FILTER = "userTaskFilter";

    @Override
    public boolean accept(String id) {
        return USER_TASK_FILTER.equalsIgnoreCase(id);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.debug("register query parameter builder --> {}", USER_TASK_FILTER);

        return new UserTaskFilter(parameters);
    }

}
