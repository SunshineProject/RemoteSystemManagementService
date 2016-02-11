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
@Table(name = "sensor_description", schema = "public")
public class SensorDescription implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "sensor_description_id")
	private int id;
	@Column(name = "download_link")
	private String downloadLink;
	@Column(name = "procedure_description_format")
	private String procedureDescriptionFormat;
	@Column(name = "procedure_id")
	private String procedureId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDownloadLink() {
		return downloadLink;
	}
	
	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}
	
	public String getProcedureDescriptionFormat() {
		return procedureDescriptionFormat;
	}
	
	public void setProcedureDescriptionFormat(String procedureDescriptionFormat) {
		this.procedureDescriptionFormat = procedureDescriptionFormat;
	}
	
	public String getProcedureId() {
		return procedureId;
	}
	
	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}
	
}
