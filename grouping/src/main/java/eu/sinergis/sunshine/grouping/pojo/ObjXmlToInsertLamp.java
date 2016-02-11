package eu.sinergis.sunshine.grouping.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "tableLampCtrl", "groupComposition" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SPS")
//@JsonTypeName(value = "SPS")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjXmlToInsertLamp {
	
	@JsonProperty(value = "group")
	@XmlElement(name = "GROUP")
	private ObjLampCtrl tableLampCtrl;
	
	@JsonProperty(value = "devices")
	@XmlElement(name = "LAMPS")
	//	private ObjXmlGroup groupComposition;
	private List<ObjLampCtrl> lamps;
	
	public ObjLampCtrl getTableLampCtrl() {
		return tableLampCtrl;
	}
	
	public void setTableLampCtrl(ObjLampCtrl tableLampCtrl) {
		this.tableLampCtrl = tableLampCtrl;
	}
	
	public List<ObjLampCtrl> getLamps() {
		return lamps;
	}
	
	public void setLamps(List<ObjLampCtrl> lamps) {
		this.lamps = lamps;
	}
	
	//	public ObjXmlGroup getGroupComposition() {
	//		return groupComposition;
	//	}
	//	
	//	public void setGroupComposition(ObjXmlGroup groupComposition) {
	//		this.groupComposition = groupComposition;
	//	}
	
}
