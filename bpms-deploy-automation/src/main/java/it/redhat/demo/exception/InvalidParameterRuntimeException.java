package it.redhat.demo.exception;

public class InvalidParameterRuntimeException extends RuntimeException {

	public InvalidParameterRuntimeException(String message) {
		super(message);
	}

}
