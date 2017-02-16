package it.redhat.demo.pojo;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "config-items")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConfigItemPojo implements Serializable {
	
	@XmlElement
	private String itemName;
	
	@XmlElement
	private String itemValue;
	
	@XmlElement
	private String itemType;

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemValue() {
		return itemValue;
	}

	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	@Override
	public String toString() {
		return "ConfigItemPojo [itemName=" + itemName + ", itemValue=" + itemValue + ", itemType=" + itemType + "]";
	}

}
