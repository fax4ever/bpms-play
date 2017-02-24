package it.redhat.demo.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="query-process-instance-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbQueryProcessInstanceInfo implements Serializable {

	/** Generated serial version UID */
	private static final long serialVersionUID = -6985386517437185816L;
	
	@XmlElement(name="process-instance")
    private JaxbProcessInstance processInstance;
	
	@XmlElement
    private List<JaxbVariableInfo> variables = new ArrayList<JaxbVariableInfo>();

	public JaxbProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(JaxbProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public List<JaxbVariableInfo> getVariables() {
		return variables;
	}

	public void setVariables(List<JaxbVariableInfo> variables) {
		this.variables = variables;
	}

	@Override
	public String toString() {
		return "JaxbQueryProcessInstanceInfo [processInstance=" + processInstance + ", variables=" + variables + "]";
	}

}
