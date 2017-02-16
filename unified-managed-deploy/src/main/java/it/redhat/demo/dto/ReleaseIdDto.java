package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="release-id")
public class ReleaseIdDto implements Serializable {
	
	@XmlElement(name = "group-id")
    private String groupId;

	@XmlElement(name = "artifact-id")
    private String artifactId;

	@XmlElement(name ="version")
    private String version;
    
    public ReleaseIdDto() {
    	
    }

	public ReleaseIdDto(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "ReleaseIdDto [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + "]";
	}
	
	

}
