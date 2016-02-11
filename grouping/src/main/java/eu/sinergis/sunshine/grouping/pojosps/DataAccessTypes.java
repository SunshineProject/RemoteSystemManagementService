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
@Table(name = "data_access_types", schema = "public")
public class DataAccessTypes implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "data_access_type_id")
	private int id;
	@Column(name = "data_access_type")
	private String dataAccessType;
	@Column(name = "result_access_id")
	private int resultAccessId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDataAccessType() {
		return dataAccessType;
	}
	
	public void setDataAccessType(String dataAccessType) {
		this.dataAccessType = dataAccessType;
	}
	
	public int getResultAccessId() {
		return resultAccessId;
	}
	
	public void setResultAccessId(int resultAccessId) {
		this.resultAccessId = resultAccessId;
	}
	
}
