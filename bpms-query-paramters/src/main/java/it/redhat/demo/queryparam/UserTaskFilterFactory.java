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

    private final static Logger LOG = LoggerFactory.getLogger(UserTaskFilterFactory.class);

    @Override
    public boolean accept(String id) {
        return "userTaskFilter".equalsIgnoreCase(id);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        LOG.info("register query parameter builder --> {}", "userTaskFilter");

        return new UserTaskFilter(parameters);
    }

}
