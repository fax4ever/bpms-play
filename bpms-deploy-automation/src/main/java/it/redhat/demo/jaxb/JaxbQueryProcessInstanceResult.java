package it.redhat.demo.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name="query-process-instance-result")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbQueryProcessInstanceResult implements Serializable {
	
	/** Generated serial version UID */
	private static final long serialVersionUID = 7204169009611156904L;
	
	@XmlElement
    private List<JaxbQueryProcessInstanceInfo> processInstanceInfoList = new ArrayList<JaxbQueryProcessInstanceInfo>();

	public List<JaxbQueryProcessInstanceInfo> getProcessInstanceInfoList() {
		return processInstanceInfoList;
	}

	public void setProcessInstanceInfoList(List<JaxbQueryProcessInstanceInfo> processInstanceInfoList) {
		this.processInstanceInfoList = processInstanceInfoList;
	}

	@Override
	public String toString() {
		return "JaxbQueryProcessInstanceResult [processInstanceInfoList=" + processInstanceInfoList + "]";
	}

}
