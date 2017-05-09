package it.redhat.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.drools.persistence.jpa.marshaller.VariableEntity;

@Entity
public class SubjectEntity extends VariableEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2625852101333359050L;
	
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "SUBJECT_ID_GENERATOR")
	@SequenceGenerator(sequenceName = "SUBJECT_ID_SEQ", name = "SUBJECT_ID_GENERATOR")
	private Long id;

}
