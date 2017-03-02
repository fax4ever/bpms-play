package it.redhat.demo.excpetion;

public class MyRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -3486313630215298391L;

	public MyRuntimeException(String message) {
		super(message);
	}

}
