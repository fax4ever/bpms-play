package it.redhat.demo.jaxb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name="process-instance")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbProcessInstance implements Serializable {

	/** Generated serial version UID */
	private static final long serialVersionUID = 8112235855955251492L;
	
	@XmlElement(name="process-id")
    @XmlSchemaType(name="string")
    private String processId;

    @XmlElement
    @XmlSchemaType(name="long")
    private long id;

    @XmlElement
    @XmlSchemaType(name="int")
    private int state; 

    @XmlElement(name="event-types")
    private List<String> eventTypes = new ArrayList<String>();

    @XmlElement
    @XmlSchemaType(name="long")
    private long parentProcessInstanceId;

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public List<String> getEventTypes() {
		return eventTypes;
	}

	public void setEventTypes(List<String> eventTypes) {
		this.eventTypes = eventTypes;
	}

	public long getParentProcessInstanceId() {
		return parentProcessInstanceId;
	}

	public void setParentProcessInstanceId(long parentProcessInstanceId) {
		this.parentProcessInstanceId = parentProcessInstanceId;
	}

	@Override
	public String toString() {
		return "JaxbProcessInstance [processId=" + processId + ", id=" + id + ", state=" + state + ", eventTypes="
				+ eventTypes + ", parentProcessInstanceId=" + parentProcessInstanceId + "]";
	}

}
