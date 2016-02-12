package org.n52.sps.sensor.cite.pojo;

import java.util.Date;

public class Command {
	private Date dateAttivation;
	private Date dateToExpiry;
	private int frequency;
	private int dimmer;
	private int timezone;
	private String dayOfWeek;
	private String activationHour;
	private String listOfDays;
	private int priority;
	
	public Date getDateAttivation() {
		return dateAttivation;
	}
	
	public void setDateAttivation(Date dateAttivation) {
		this.dateAttivation = dateAttivation;
	}
	
	public Date getDateToExpiry() {
		return dateToExpiry;
	}
	
	public void setDateToExpiry(Date dateToExpiry) {
		this.dateToExpiry = dateToExpiry;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public int getDimmer() {
		return dimmer;
	}
	
	public void setDimmer(int dimmer) {
		this.dimmer = dimmer;
	}
	
	public int getTimezone() {
		return timezone;
	}
	
	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}
	
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	public String getActivationHour() {
		return activationHour;
	}
	
	public void setActivationHour(String activationHour) {
		this.activationHour = activationHour;
	}
	
	public String getListOfDays() {
		return listOfDays;
	}
	
	public void setListOfDays(String listOfDays) {
		this.listOfDays = listOfDays;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
}
