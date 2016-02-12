package eu.sinergis.sunshine.obj;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "COMMANDS")
public class AccessTables {
	
	@XmlElement(name = "COMMAND")
	private List<AccessTable> listAccessTable;
	
	public List<AccessTable> getListAccessTable() {
		return listAccessTable;
	}
	
	public void setListAccessTable(List<AccessTable> listAccessTable) {
		this.listAccessTable = listAccessTable;
	}
}
