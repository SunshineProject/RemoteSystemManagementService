package eu.sinergis.sunshine.grouping.pojosps;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "sensor_task", schema = "public")
public class SensorTask implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "sensor_task_id")
	private int id;
	@Column(name = "event")
	private String event;
	@Column(name = "task_id")
	private String taskId;
	@Column(name = "procedure_id")
	private String procedureId;
	@Column(name = "update_time")
	private Date updateTime;
	
	@Column(name = "task_status")
	private String taskStatus;
	@Column(name = "time_to_complete")
	private Date timeToComplete;
	
	@Column(name = "parameter_data")
	private String parameterData;
	@Column(name = "request_status")
	private Date requestDtatus;
	
	@Column(name = "percent_of_completion")
	private Double percentOfCompletion;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getEvent() {
		return event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getProcedureId() {
		return procedureId;
	}
	
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public String getTaskStatus() {
		return taskStatus;
	}
	
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	public Date getTimeToComplete() {
		return timeToComplete;
	}
	
	public void setTimeToComplete(Date timeToComplete) {
		this.timeToComplete = timeToComplete;
	}
	
	public String getParameterData() {
		return parameterData;
	}
	
	public void setParameterData(String parameterData) {
		this.parameterData = parameterData;
	}
	
	public Date getRequestDtatus() {
		return requestDtatus;
	}
	
	public void setRequestDtatus(Date requestDtatus) {
		this.requestDtatus = requestDtatus;
	}
	
	public Double getPercentOfCompletion() {
		return percentOfCompletion;
	}
	
	public void setPercentOfCompletion(Double percentOfCompletion) {
		this.percentOfCompletion = percentOfCompletion;
	}
	
}
