package it.workshop.model;

import java.io.Serializable;

public class SoggettoDebitore implements Serializable {

	private String fiscalCode;
	private String firstName;
	private String lastName;
	
	public SoggettoDebitore() {}

	public SoggettoDebitore(String fiscalCode, String firstName, String lastName) {
		this.fiscalCode = fiscalCode;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
