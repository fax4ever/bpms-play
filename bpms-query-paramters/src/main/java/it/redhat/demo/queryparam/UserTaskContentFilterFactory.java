package it.redhat.demo.queryparam;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskContentFilterFactory implements QueryParamBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(UserTaskContentFilterFactory.class);
    public static final String USER_TASK_CONTENT_FILTER = "userTaskContentFilter";

    @Override
    public boolean accept(String id) {
        return USER_TASK_CONTENT_FILTER.equalsIgnoreCase(id);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.debug("register query parameter builder --> {}", USER_TASK_CONTENT_FILTER);

        return new UserTaskContentFilter(parameters);
    }

}
