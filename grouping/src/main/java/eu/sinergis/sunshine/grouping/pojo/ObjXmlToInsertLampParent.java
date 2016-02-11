package eu.sinergis.sunshine.grouping.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "objXmlInserLamp" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SPS")
//@JsonTypeName(value = "SPS")
public class ObjXmlToInsertLampParent {
	@JsonProperty(value = "groups")
	@XmlElement(name = "groups")
	private List<ObjXmlToInsertLamp> objXmlInserLamp;
	
	//	@JsonProperty(value = "groups")
	//	private List<ObjXmlToInsertLampSimple> objXmlInsertLampSimple;
	
	public List<ObjXmlToInsertLamp> getObjXmlInserLamp() {
		return objXmlInserLamp;
	}
	
	public void setObjXmlInserLamp(List<ObjXmlToInsertLamp> objXmlInserLamp) {
		this.objXmlInserLamp = objXmlInserLamp;
	}
	
	//	public List<ObjXmlToInsertLampSimple> getObjXmlInsertLampSimple() {
	//		return objXmlInsertLampSimple;
	//	}
	//	
	//	public void setObjXmlInsertLampSimple(List<ObjXmlToInsertLampSimple> objXmlInsertLampSimple) {
	//		this.objXmlInsertLampSimple = objXmlInsertLampSimple;
	//	}
}
