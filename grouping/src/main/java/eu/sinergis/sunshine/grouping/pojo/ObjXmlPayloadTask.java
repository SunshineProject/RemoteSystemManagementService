package eu.sinergis.sunshine.grouping.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "taskid", "spsid" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task")
public class ObjXmlPayloadTask {
	@XmlElement(name = "TASK_ID")
	@JsonProperty(value = "task_id")
	private String taskid;
	@XmlElement(name = "SPS_ID")
	@JsonProperty(value = "sps_id")
	private String spsid;
	
	public String getTaskid() {
		return taskid;
	}
	
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	public String getSpsid() {
		return spsid;
	}
	
	public void setSpsid(String spsid) {
		this.spsid = spsid;
	}
	
}
