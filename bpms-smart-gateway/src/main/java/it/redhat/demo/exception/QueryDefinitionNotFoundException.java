package it.redhat.demo.exception;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */
public class QueryDefinitionNotFoundException extends NotFoundException {

    public QueryDefinitionNotFoundException(String queryDefinitionId) {
        super("Query definition id not found " + queryDefinitionId);
    }

}
