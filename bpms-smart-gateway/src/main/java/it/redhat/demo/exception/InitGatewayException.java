package it.redhat.demo.exception;

/**
 * Created by fabio.ercoli@redhat.com on 13/04/17.
 */
public class InitGatewayException extends RuntimeException {

    public InitGatewayException(int retry, int loopCount, int sleep) {
        super("Error on establishing connection with remote process server using rest api. Retry: "
                + retry + " Loop count: " + loopCount + " Sleep" + sleep);
    }

}
