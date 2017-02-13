package it.redhat.demo.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XmlRootElement(name="release-id")
@XStreamAlias( "release-id" )
@JsonIgnoreProperties({"snapshot"})
public class ReleaseIdDto implements Serializable {
	
	@XStreamAlias( "group-id" )
    private String groupId;

    @XStreamAlias( "artifact-id" )
    private String artifactId;

    @XStreamAlias( "version" )
    private String version;
    
    public ReleaseIdDto() {
    	
    }

	public ReleaseIdDto(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
    
    

}
