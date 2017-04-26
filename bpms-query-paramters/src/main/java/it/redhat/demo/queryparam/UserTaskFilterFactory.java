package it.redhat.demo.queryparam;

import org.jbpm.services.api.query.QueryParamBuilder;
import org.jbpm.services.api.query.QueryParamBuilderFactory;

import java.util.Map;

/**
 * Created by fabio.ercoli@redhat.com on 26/04/17.
 */
public class UserTaskFilterFactory implements QueryParamBuilderFactory {

    @Override
    public boolean accept(String id) {
        return "userTaskFilter".equalsIgnoreCase(id);
    }

    @Override
    public QueryParamBuilder<?> newInstance(Map<String, Object> parameters) {
        return new UserTaskFilter(parameters);
    }

}
