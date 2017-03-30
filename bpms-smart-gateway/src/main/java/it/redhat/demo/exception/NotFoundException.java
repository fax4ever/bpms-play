package it.redhat.demo.exception;

/**
 * Created by fabio.ercoli@redhat.com on 30/03/17.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

}
