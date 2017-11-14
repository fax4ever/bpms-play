package it.redhat.demo.bpm.process.exception;

public class SignalNotAvilableException extends RuntimeException {

	public SignalNotAvilableException(long processInstanceId, String signalName) {
		super("Signal " + signalName + " not available on process instance " + processInstanceId);
	}

}
