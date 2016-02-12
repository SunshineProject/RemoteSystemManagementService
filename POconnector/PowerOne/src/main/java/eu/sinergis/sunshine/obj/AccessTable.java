package eu.sinergis.sunshine.obj;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "COMMAND")
@Entity
@Table(name = "SPS_DATA")
public class AccessTable {
	
	@XmlElement(name = "ID")
	@Column(name = "sps_id")
	@Id
	private int sps_id;
	
	@XmlElement(name = "DEST_TYPE")
	@Column(name = "sps_dest")
	private String sps_dest;
	
	@XmlElement(name = "DEST_VALUE")
	@Column(name = "sps_dest_param")
	private String sps_dest_param;
	
	@XmlElement(name = "COMMAND")
	@Column(name = "sps_command")
	private String sps_command;
	
	@XmlElement(name = "CTRLID")
	@Column(name = "sps_cb_sn")
	private String sps_cb_sn;
	
	@XmlElement(name = "DIMMING_VALUE")
	@Column(name = "sps_param1")
	private String sps_param1;
	
	@Column(name = "sps_param2")
	private String sps_param2;
	
	@XmlElement(name = "COMMAND_EXECUTED")
	@Column(name = "sps_sended")
	private String sps_sended;
	
	public int getSps_id() {
		return sps_id;
	}
	
	public void setSps_id(int sps_id) {
		this.sps_id = sps_id;
	}
	
	public String getSps_dest() {
		return sps_dest;
	}
	
	public void setSps_dest(String sps_dest) {
		this.sps_dest = sps_dest;
	}
	
	public String getSps_dest_param() {
		return sps_dest_param;
	}
	
	public void setSps_dest_param(String sps_dest_param) {
		this.sps_dest_param = sps_dest_param;
	}
	
	public String getSps_command() {
		return sps_command;
	}
	
	public void setSps_command(String sps_command) {
		this.sps_command = sps_command;
	}
	
	public String getSps_cb_sn() {
		return sps_cb_sn;
	}
	
	public void setSps_cb_sn(String sps_cb_sn) {
		this.sps_cb_sn = sps_cb_sn;
	}
	
	public String getSps_param1() {
		return sps_param1;
	}
	
	public void setSps_param1(String sps_param1) {
		this.sps_param1 = sps_param1;
	}
	
	public String getSps_param2() {
		return sps_param2;
	}
	
	public void setSps_param2(String sps_param2) {
		this.sps_param2 = sps_param2;
	}
	
	public String getSps_sended() {
		return sps_sended;
	}
	
	public void setSps_sended(String sps_sended) {
		this.sps_sended = sps_sended;
	}
}
