package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "processConfig")
public class ProcessConfigDto extends ContainerConfigDto implements Serializable {
	
	@XmlElement(name = "runtimeStrategy")
    private String runtimeStrategy;
    @XmlElement(name = "kbase")
    private String kBase = "";
    @XmlElement(name = "ksession")
    private String kSession = "";
    @XmlElement(name = "mergeMode")
    private String mergeMode;
    
    public ProcessConfigDto() {
    	
    }
    
	public ProcessConfigDto(String runtimeStrategy, String kBase, String kSession, String mergeMode) {
		super();
		this.runtimeStrategy = runtimeStrategy;
		this.kBase = kBase;
		this.kSession = kSession;
		this.mergeMode = mergeMode;
	}

	public String getRuntimeStrategy() {
		return runtimeStrategy;
	}

	public void setRuntimeStrategy(String runtimeStrategy) {
		this.runtimeStrategy = runtimeStrategy;
	}

	public String getkBase() {
		return kBase;
	}

	public void setkBase(String kBase) {
		this.kBase = kBase;
	}

	public String getkSession() {
		return kSession;
	}

	public void setkSession(String kSession) {
		this.kSession = kSession;
	}

	public String getMergeMode() {
		return mergeMode;
	}

	public void setMergeMode(String mergeMode) {
		this.mergeMode = mergeMode;
	}

}
