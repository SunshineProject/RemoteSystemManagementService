package eu.sinergis.sunshine.grouping.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

@XmlType(propOrder = { "lamps" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GROUP")
@JsonTypeName(value = "GROUP")
public class ObjXmlGroup {
	@XmlElement(name = "CTRL_ID")
	@JsonProperty(value = "lamp")
	private List<ObjLampCtrl> lamps;
	
	public List<ObjLampCtrl> getLamps() {
		return lamps;
	}
	
	public void setLamps(List<ObjLampCtrl> lamps) {
		this.lamps = lamps;
	}
	
}
