package it.redhat.demo.exception;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */
public class ProcessDefinitionNotFoundException extends NotFoundException {

    public ProcessDefinitionNotFoundException(String processDefinitionId) {
        super("Process definition not found: " + processDefinitionId);
    }

}
