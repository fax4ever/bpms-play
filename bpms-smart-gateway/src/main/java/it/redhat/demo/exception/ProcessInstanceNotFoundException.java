package it.redhat.demo.exception;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */
public class ProcessInstanceNotFoundException extends NotFoundException {

    public ProcessInstanceNotFoundException(Long processInstanceId) {
        super("Process instance not found: " + processInstanceId);
    }
    
}
