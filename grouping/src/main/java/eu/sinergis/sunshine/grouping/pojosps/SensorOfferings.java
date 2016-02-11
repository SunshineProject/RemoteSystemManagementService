package eu.sinergis.sunshine.grouping.pojosps;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sensor_offerings", schema = "public")
public class SensorOfferings implements Serializable {
	@Id
	//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	//	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "sensor_offering_id")
	private String id;
	@Column(name = "sensor_offering")
	private String sensorOffering;
	@Column(name = "sensor_description_id")
	private int sensorDescriptionId;
	
	public String getSensorOffering() {
		return sensorOffering;
	}
	
	public void setSensorOffering(String sensorOffering) {
		this.sensorOffering = sensorOffering;
	}
	
	public int getSensorDescriptionId() {
		return sensorDescriptionId;
	}
	
	public void setSensorDescriptionId(int sensorDescriptionId) {
		this.sensorDescriptionId = sensorDescriptionId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}
