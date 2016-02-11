package eu.sinergis.sunshine.grouping.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "tableLampCtrl", "listLampId" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SPS")
//@JsonTypeName(value = "SPS")
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjXmlToInsertGroup {
	
	@JsonProperty(value = "group")
	@XmlElement(name = "GROUP")
	private ObjLampCtrl tableLampCtrl;
	
	@JsonProperty(value = "deviceids")
	@XmlElement(name = "LAMPS")
	private List<Integer> listLampId;
	
	public ObjLampCtrl getTableLampCtrl() {
		return tableLampCtrl;
	}
	
	public void setTableLampCtrl(ObjLampCtrl tableLampCtrl) {
		this.tableLampCtrl = tableLampCtrl;
	}
	
	public List<Integer> getListLampId() {
		return listLampId;
	}
	
	public void setListLampId(List<Integer> listLampId) {
		this.listLampId = listLampId;
	}
	
}
