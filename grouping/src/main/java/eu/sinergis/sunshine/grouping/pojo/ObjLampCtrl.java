package eu.sinergis.sunshine.grouping.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlType(propOrder = { "id", "spsId", "sosId", "usrId", "ctrlId", "typeGroup", "typeOfLamp", "nominalPower", "height",
		"codeSpace", "connect", "valid", "dateValid" })
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SENSOR_CONTROL")
//@JsonTypeName(value = "SENSOR_CONTROL")
@Entity
@Table(name = "lampctrl", schema = "grouping")
public class ObjLampCtrl implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence", allocationSize = 1)
	@XmlElement(name = "LAMPCTRL_ID")
	@JsonProperty(value = "id")
	@Column(name = "lampctrlid")
	private int id;
	@XmlElement(name = "SPS_ID")
	@JsonProperty(value = "sps_id")
	@Column(name = "spsid")
	private String spsId;
	@XmlElement(name = "SOS_ID")
	@JsonProperty(value = "sos_id")
	@Column(name = "sosid")
	private String sosId;
	@XmlElement(name = "USR_ID")
	@JsonProperty(value = "usr_id")
	@Column(name = "usrid")
	private String usrId;
	@XmlElement(name = "CTRL_ID")
	@JsonProperty(value = "ctrl_id")
	@Column(nullable = true, name = "ctrlid")
	private String ctrlId;
	@XmlElement(name = "TYPE_GROUP")
	@JsonProperty(value = "type")
	@Column(name = "typegroup")
	private String typeGroup;
	@XmlElement(name = "TYPE_OF_LAMP")
	@JsonProperty(value = "type_of_lamp")
	@Column(nullable = true, name = "typeoflamp")
	private String typeOfLamp;
	@XmlElement(name = "NOMINAL_POWER")
	@JsonProperty(value = "nominalpower")
	@Column(nullable = true, name = "nominalpower")
	private double nominalPower = 0.0;
	@XmlElement(name = "HEIGHT")
	@JsonProperty(value = "height")
	@Column(nullable = true, name = "height")
	private double height = 0.0;
	@XmlElement(name = "CODESPACE")
	@JsonProperty(value = "codespace")
	@Column(nullable = true, name = "codespace")
	private String codeSpace;
	@XmlElement(name = "CONNECT")
	@JsonProperty(value = "connect")
	@Column(nullable = true, name = "connect")
	private boolean connect;
	@XmlElement(name = "VALID")
	@JsonProperty(value = "valid")
	@Column(nullable = true, name = "valid")
	private Boolean valid;
	@XmlElement(name = "DATEVALID")
	@JsonProperty(value = "datevalid")
	@Column(nullable = true, name = "datevalid")
	@JsonIgnore
	private Date dateValid;
	
	public String getSpsId() {
		return spsId;
	}
	
	public void setSpsId(String spsId) {
		this.spsId = spsId;
	}
	
	public String getSosId() {
		return sosId;
	}
	
	public void setSosId(String sosId) {
		this.sosId = sosId;
	}
	
	public String getUsrId() {
		return usrId;
	}
	
	public void setUsrId(String usrId) {
		this.usrId = usrId;
	}
	
	public String getCtrlId() {
		return ctrlId;
	}
	
	public void setCtrlId(String ctrlId) {
		this.ctrlId = ctrlId;
	}
	
	public String getTypeGroup() {
		return typeGroup;
	}
	
	public void setTypeGroup(String typeGroup) {
		this.typeGroup = typeGroup;
	}
	
	public String getTypeOfLamp() {
		return typeOfLamp;
	}
	
	public void setTypeOfLamp(String typeOfLamp) {
		this.typeOfLamp = typeOfLamp;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public double getNominalPower() {
		return nominalPower;
	}
	
	public void setNominalPower(double nominalPower) {
		this.nominalPower = nominalPower;
	}
	
	public double getHeight() {
		return height;
	}
	
	public void setHeight(double height) {
		this.height = height;
	}
	
	public String getCodeSpace() {
		return codeSpace;
	}
	
	public void setCodeSpace(String codeSpace) {
		this.codeSpace = codeSpace;
	}
	
	public boolean isConnect() {
		return connect;
	}
	
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public Date getDateValid() {
		return dateValid;
	}
	
	public void setDateValid(Date dateValid) {
		this.dateValid = dateValid;
	}
	
}
