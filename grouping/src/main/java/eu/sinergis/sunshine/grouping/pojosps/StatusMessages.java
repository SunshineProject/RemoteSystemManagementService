package eu.sinergis.sunshine.grouping.pojosps;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "status_messages", schema = "public")
public class StatusMessages implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "status_message_id")
	private int id;
	@Column(name = "status_message")
	private String statusMessage;
	@Column(name = "sensor_task_id")
	private int sensorTaskId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getStatusMessage() {
		return statusMessage;
	}
	
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	public int getSensorTaskId() {
		return sensorTaskId;
	}
	
	public void setSensorTaskId(int sensorTaskId) {
		this.sensorTaskId = sensorTaskId;
	}
	
}
