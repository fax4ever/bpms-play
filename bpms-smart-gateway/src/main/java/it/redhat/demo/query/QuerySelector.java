package it.redhat.demo.query;

import it.redhat.demo.exception.QueryDefinitionNotFoundException;
import org.kie.server.api.model.definition.QueryDefinition;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */

@ApplicationScoped
public class QuerySelector {

    @Inject
    private Instance<QueryDefinition> queryDefinitions;

    public QueryDefinition selectQuery(String name) {

        Instance<QueryDefinition> queryDefinition = queryDefinitions.select(new NamedLiteral(name));
        if (queryDefinition.isUnsatisfied()) {
            throw new QueryDefinitionNotFoundException(name);
        }

        return queryDefinition.get();

    }

    private class NamedLiteral extends AnnotationLiteral<Named> implements Named {

        private String value;

        NamedLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

}
