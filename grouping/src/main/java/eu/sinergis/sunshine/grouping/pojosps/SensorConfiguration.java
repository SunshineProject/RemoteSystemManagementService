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
@Table(name = "sensor_configuration", schema = "public")
public class SensorConfiguration implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "sensor_configuration_id")
	private int id;
	@Column(name = "procedure_id")
	private String procedureId;
	@Column(name = "sensor_plugin_type")
	private String sensorPluginType;
	@Column(name = "tasking_parameters")
	private String taskingParameters;
	@Column(name = "sensor_setup")
	private String sensorSetup;
	
	@Column(name = "data_result_access")
	private int dataResultAccess;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getProcedureId() {
		return procedureId;
	}
	
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	
	public String getSensorPluginType() {
		return sensorPluginType;
	}
	
	public void setSensorPluginType(String sensorPluginType) {
		this.sensorPluginType = sensorPluginType;
	}
	
	public String getTaskingParameters() {
		return taskingParameters;
	}
	
	public void setTaskingParameters(String taskingParameters) {
		this.taskingParameters = taskingParameters;
	}
	
	public String getSensorSetup() {
		return sensorSetup;
	}
	
	public void setSensorSetup(String sensorSetup) {
		this.sensorSetup = sensorSetup;
	}
	
	public int getDataResultAccess() {
		return dataResultAccess;
	}
	
	public void setDataResultAccess(int dataResultAccess) {
		this.dataResultAccess = dataResultAccess;
	}
	
}
