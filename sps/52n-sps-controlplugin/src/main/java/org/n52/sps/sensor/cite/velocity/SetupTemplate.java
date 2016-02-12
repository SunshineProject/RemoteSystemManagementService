package org.n52.sps.sensor.cite.velocity;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetupTemplate {
	private static final Logger LOGGER = LoggerFactory.getLogger(SetupTemplate.class);
	
	//ReadFromConfig.loadByName("hostGeo")+ "ows?service=WFS&version=1.1.0&request=GetFeature&typeName=opere&srsName=EPSG:4326&outputFormat=json"
	public final String getTemplateSendCommandSet(String destType, String destValue, String command,
			String cmdExecuted, String dimmingValue, String ctrlId) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("destType", destType);
		hasht.put("destValue", destValue);
		hasht.put("command", command);
		hasht.put("cmdExecuted", cmdExecuted);
		hasht.put("dimmingValue", dimmingValue);
		hasht.put("ctrlId", ctrlId);
		
		try {
			page = vt.getRequestXmlVelocity("TemplateSetCommand", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug("", e);
		}
		return page;
	}
	
	//ReadFromConfig.loadByName("hostGeo")+ "ows?service=WFS&version=1.1.0&request=GetFeature&typeName=opere&srsName=EPSG:4326&outputFormat=json"
	public final String getTemplateSendCommandSetConnect(String destType, String destValue, String command) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("destType", destType);
		hasht.put("destValue", destValue);
		hasht.put("command", command);
		
		try {
			page = vt.getRequestXmlVelocity("TemplateSetCommandConnect", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug("", e);
		}
		return page;
	}
	
	public final String getTemplateSendCommandSetCtrlid(String destType, String destValue, String command,
			String cmdExecuted, String ctrlId) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("destType", destType);
		hasht.put("destValue", destValue);
		hasht.put("command", command);
		hasht.put("cmdExecuted", cmdExecuted);
		hasht.put("ctrlId", ctrlId);
		
		try {
			page = vt.getRequestXmlVelocity("TemplateSetCommandCtrlid", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug("", e);
		}
		return page;
	}
	
	public final String getTemplateSendCommandReverberi(String idLampada, String dimmer, String codespace) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("idLampada", idLampada);
		hasht.put("dimmer", dimmer);
		hasht.put("codespace", codespace);
		
		try {
			page = vt.getRequestXmlVelocity("TemplateReverberiCommand", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug("", e);
		}
		return page;
	}
}
