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
@Table(name = "data_result_access", schema = "public")
public class DataResultAccess implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence")
	@Column(name = "result_access_id")
	private int id;
	@Column(name = "data_result_reference")
	private String dataResultReference;
	@Column(name = "data_result_role")
	private String dataResultRole;
	@Column(name = "title")
	private String title;
	@Column(name = "identifier")
	private String identifier;
	
	@Column(name = "reference_abstract")
	private String referenceAbstract;
	@Column(name = "format")
	private String format;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDataResultReference() {
		return dataResultReference;
	}
	
	public void setDataResultReference(String dataResultReference) {
		this.dataResultReference = dataResultReference;
	}
	
	public String getDataResultRole() {
		return dataResultRole;
	}
	
	public void setDataResultRole(String dataResultRole) {
		this.dataResultRole = dataResultRole;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getReferenceAbstract() {
		return referenceAbstract;
	}
	
	public void setReferenceAbstract(String referenceAbstract) {
		this.referenceAbstract = referenceAbstract;
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
}
