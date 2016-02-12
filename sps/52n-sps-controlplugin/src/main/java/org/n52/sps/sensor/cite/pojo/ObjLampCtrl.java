package org.n52.sps.sensor.cite.pojo;

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
@Table(name = "lampctrl", schema = "grouping")
public class ObjLampCtrl implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "devices_seq_gen")
	@SequenceGenerator(name = "devices_seq_gen", sequenceName = "hibernate_sequence", allocationSize = 1)
	@Column(name = "lampctrlid")
	private int id;
	
	@Column(name = "spsid")
	private String spsId;
	
	@Column(name = "sosid")
	private String sosId;
	
	@Column(name = "usrid")
	private String usrId;
	
	@Column(nullable = true, name = "ctrlid")
	private String ctrlId;
	
	@Column(name = "typegroup")
	private String typeGroup;
	
	@Column(nullable = true, name = "typeoflamp")
	private String typeOfLamp;
	
	@Column(nullable = true, name = "nominalpower")
	private double nominalPower = 0.0;
	
	@Column(nullable = true, name = "height")
	private double height = 0.0;
	
	@Column(nullable = true, name = "codespace")
	private String codeSpace;
	
	@Column(nullable = true, name = "connect")
	private boolean connect;
	
	@Column(nullable = true, name = "valid")
	private Boolean valid;
	
	@Column(nullable = true, name = "datevalid")
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
