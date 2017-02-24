package it.redhat.demo.jaxb;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name="variable-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxbVariableInfo implements Serializable {

	/** Generated serial version UID */
	private static final long serialVersionUID = -5149687972788077244L;
	
	@XmlElement(required=true)
    @XmlSchemaType(name="string")
    private String name;

    @XmlAnyElement
    private Object value;
    
    @XmlElement
    @XmlSchemaType(name="dateTime")
    private Date lastModificationDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	@Override
	public String toString() {
		return "JaxbVariableInfo [name=" + name + ", value=" + value + ", lastModificationDate=" + lastModificationDate
				+ "]";
	}

}
