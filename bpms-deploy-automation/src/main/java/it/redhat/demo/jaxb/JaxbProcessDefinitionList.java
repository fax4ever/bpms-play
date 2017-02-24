package it.redhat.demo.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

import org.kie.services.client.serialization.jaxb.impl.process.JaxbProcessDefinition;

@XmlRootElement(name="process-definition-list")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbProcessDefinitionList implements Serializable {

	/** Generated serial version UID */
	private static final long serialVersionUID = -4231986000605847402L;
	
	@XmlElements({
        @XmlElement(name ="process-definition", type=JaxbProcessDefinition.class)
    })
    private List<JaxbProcessDefinition> processDefinitionList = new ArrayList<JaxbProcessDefinition>();
	
	@XmlElement(name="page-number")
    @XmlSchemaType(name="int")
    private Integer pageNumber;
    
    @XmlElement(name="page-size")
    @XmlSchemaType(name="int")
    private Integer pageSize;

	public List<JaxbProcessDefinition> getProcessDefinitionList() {
		return processDefinitionList;
	}

	public void setProcessDefinitionList(List<JaxbProcessDefinition> processDefinitionList) {
		this.processDefinitionList = processDefinitionList;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}	

}
