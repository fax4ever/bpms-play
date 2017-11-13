package it.redhat.demo.bpm.process.entity;

import org.drools.persistence.jpa.marshaller.VariableEntity;

import javax.persistence.*;

@Entity
public class Ownership extends VariableEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -203137255935692848L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "OWNERSHIP_ID_GENERATOR")
	@SequenceGenerator(sequenceName = "OWNERSHIP_ID_SEQ", name = "OWNERSHIP_ID_GENERATOR")
	private Long id;

	private String cip;
	private String agency;
	
	public Ownership() {}

	public Ownership(String cip, String agency) {
		this.cip = cip;
		this.agency = agency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Transient
	public String getGroup() {
		return "INSURANCE_AGENT_ROLE_"+agency;
	}

	public String getCip() {
		return cip;
	}

	public void setCip(String cip) {
		this.cip = cip;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	@Override
	public String toString() {
		return "Ownership [cip=" + cip + ", agency=" + agency + "]";
	}

}
