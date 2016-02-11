package eu.sinergis.sunshine.grouping.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "codespaceList" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "GROUP")
public class ObjXmlCodespace {
	@XmlElement(name = "CODESPACES")
	@JsonProperty(value = "codespaces")
	private List<String> codespaceList;
	
	public List<String> getCodespaceList() {
		return codespaceList;
	}
	
	public void setCodespaceList(List<String> codespaceList) {
		this.codespaceList = codespaceList;
	}
	
}
