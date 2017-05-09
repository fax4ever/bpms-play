package it.redhat.demo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.drools.persistence.jpa.marshaller.VariableEntity;

@Entity
public class ProposalEntity extends VariableEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8320910786841837181L;
	
	@Id
	@GeneratedValue(strategy = javax.persistence.GenerationType.AUTO, generator = "PROPOSAL_ID_GENERATOR")
	@SequenceGenerator(sequenceName = "PROPOSAL_ID_SEQ", name = "PROPOSAL_ID_GENERATOR")
	private Long id;
	
	private SubjectEntity subject;

}
