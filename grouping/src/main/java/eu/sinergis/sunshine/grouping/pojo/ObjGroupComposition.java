package eu.sinergis.sunshine.grouping.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "grpcomposition", schema = "grouping")
public class ObjGroupComposition implements Serializable {
	@Id
	@Column(name = "lampctrlgroupid")
	private int groupId;
	@Id
	@Column(name = "lampctrllampid")
	private int lampId;
	
	public int getGroupId() {
		return groupId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public int getLampId() {
		return lampId;
	}
	
	public void setLampId(int lampId) {
		this.lampId = lampId;
	}
	
}
