package it.redhat.demo.bpm.process.exception;

public class CipOrAgencyCodeNotValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4482360861539668788L;

	public CipOrAgencyCodeNotValidException(String cip, String agencyCode) {
		super("Cip or Agency Code is not valid. Cip: " + cip + ". Agency code: " + agencyCode );
	}
	
}
