package it.redhat.demo.bpms.exception;

public class SignalNotAccepted extends Exception {

	public SignalNotAccepted(Long processInstanceId, String signalName) {
		super(" Signal " + signalName + " not accepted on Process Instance ID " + processInstanceId + ".");
	}
}
