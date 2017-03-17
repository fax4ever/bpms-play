package it.redhat.demo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.drools.persistence.jpa.marshaller.VariableEntity;

@Entity
public class MemoryEntity extends VariableEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 93291101664318004L;

	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "MEMORY_GENERATOR")
	@SequenceGenerator(name = "MEMORY_GENERATOR", sequenceName = "MEMORY_SEQ")
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date moment;
	
	private String subject;
	
	private String location;
	
	private String note;

	public MemoryEntity() {
	}

	public MemoryEntity(Date moment, String subject, String location, String note) {
		super();
		this.moment = moment;
		this.subject = subject;
		this.location = location;
		this.note = note;
	}

	public Long getId() {
		return id;
	}

	public Date getMoment() {
		return moment;
	}

	public void setMoment(Date moment) {
		this.moment = moment;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}