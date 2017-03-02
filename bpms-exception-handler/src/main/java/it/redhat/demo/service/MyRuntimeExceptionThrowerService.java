package it.redhat.demo.service;

import it.redhat.demo.excpetion.MyRuntimeException;

public class MyRuntimeExceptionThrowerService {
	
	public void throwException(String message) {
		throw new MyRuntimeException(message);
	}

}
