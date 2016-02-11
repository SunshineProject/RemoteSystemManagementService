package eu.sinergis.sunshine.grouping.pojo;

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
public class ObjXmlToInsertLampSimple {
	
	@JsonProperty(value = "group")
	@XmlElement(name = "TABLE_LAMP_CTRL")
	private ObjLampCtrl tableLampCtrl;
	
	public ObjLampCtrl getTableLampCtrl() {
		return tableLampCtrl;
	}
	
	public void setTableLampCtrl(ObjLampCtrl tableLampCtrl) {
		this.tableLampCtrl = tableLampCtrl;
	}
	
}
