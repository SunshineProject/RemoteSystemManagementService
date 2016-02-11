package eu.sinergis.sunshine.grouping.velocity;

import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

public class SetupInsertSensorTemplate {
	private static final Logger LOGGER = Logger.getLogger(SetupInsertSensorTemplate.class);
	
	//ReadFromConfig.loadByName("hostGeo")+ "ows?service=WFS&version=1.1.0&request=GetFeature&typeName=opere&srsName=EPSG:4326&outputFormat=json"
	public final String getTemplateInsertSensor(String offering, String offeringDescription, List<String> listPointxy,
			String procedure, String pluginName, String PluginIdentifier, String PluginAbstract,
			String PluginLinkDownload, String PluginTaskIdentifier) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String page = "Error creating the insertSensor request";
		
		hasht.put("offering", offering);
		hasht.put("offeringDescription", offeringDescription);
		hasht.put("point1", listPointxy.get(0));
		hasht.put("point2", listPointxy.get(1));
		hasht.put("point3", listPointxy.get(2));
		hasht.put("point4", listPointxy.get(3));
		hasht.put("procedure", procedure);
		hasht.put("pluginName", pluginName);
		hasht.put("PluginIdentifier", PluginIdentifier);
		hasht.put("PluginAbstract", PluginAbstract);
		hasht.put("PluginLinkDownload", PluginLinkDownload);
		hasht.put("PluginTaskIdentifier", PluginTaskIdentifier);
		
		try {
			page = vt.getRequestXmlVelocity("InsertSensorTemplate", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Load of template, unsuccessfully");
			LOGGER.debug(e);
		}
		return page;
	}
}
