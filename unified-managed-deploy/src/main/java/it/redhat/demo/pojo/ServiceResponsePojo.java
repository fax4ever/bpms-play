package it.redhat.demo.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceResponsePojo implements Serializable {

	@XmlAttribute
	private String type;

	@XmlAttribute
	private String msg;
	
	@XmlElement(name = "kie-container")
	private KieContainerResourcePojo result;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public KieContainerResourcePojo getResult() {
		return result;
	}

	public void setResult(KieContainerResourcePojo result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ServiceResponsePojo [type=" + type + ", msg=" + msg + ", result=" + result + "]";
	}

}
