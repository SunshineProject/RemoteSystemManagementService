package eu.sinergis.sunshine.grouping.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "taskid", "spsid", "nextFire", "dimm", "priority", "dayofweek", "startdate", "enddate" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "task")
public class ObjXmlPayloadTaskDimming {
	@XmlElement(name = "TASK_ID")
	@JsonProperty(value = "task_id")
	private String taskid;
	@XmlElement(name = "SPS_ID")
	@JsonProperty(value = "sps_id")
	private String spsid;
	@XmlElement(name = "NEXT_FIRE")
	@JsonProperty(value = "next_fire")
	private String nextFire;
	@XmlElement(name = "DIMM")
	@JsonProperty(value = "dimm")
	private String dimm;
	@XmlElement(name = "PRIORITY")
	@JsonProperty(value = "priority")
	private String priority;
	@XmlElement(name = "DAYOFWEEK")
	@JsonProperty(value = "dayofweek")
	private String dayofweek;
	@XmlElement(name = "STARTDATE")
	@JsonProperty(value = "startdate")
	private String startdate;
	@XmlElement(name = "ENDDATE")
	@JsonProperty(value = "enddate")
	private String enddate;
	
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
	
	public String getNextFire() {
		return nextFire;
	}
	
	public void setNextFire(String nextFire) {
		this.nextFire = nextFire;
	}
	
	public String getDimm() {
		return dimm;
	}
	
	public void setDimm(String dimm) {
		this.dimm = dimm;
	}
	
	public String getPriority() {
		return priority;
	}
	
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String getDayofweek() {
		return dayofweek;
	}
	
	public void setDayofweek(String dayofweek) {
		this.dayofweek = dayofweek;
	}
	
	public String getStartdate() {
		return startdate;
	}
	
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	
	public String getEnddate() {
		return enddate;
	}
	
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	
}
