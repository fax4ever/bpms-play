package it.redhat.demo.queryparam;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskMapContentFilterFactory implements QueryParamBuilderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(UserTaskMapContentFilterFactory.class);
    public static final String FILTER_NAME = "userTaskMapContentFilter";

    @Override
    public boolean accept(String id) {
        return FILTER_NAME.equalsIgnoreCase(id);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.debug("register query parameter builder --> {}", FILTER_NAME);

        return new UserTaskMapContentFilter(parameters);
    }

}
