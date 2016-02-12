package it.sinergis.sunshine.velocity;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public class SetupPageTemplate {
	private static final Logger LOGGER = Logger.getLogger(SetupPageTemplate.class);
	
	public final String getTemplate(String nameTable, String columA, String columB, String columC, String columD,
			String columE, String valueA, String valueB, String valueC, String valueD, String valueE) {
		VelocityTemplate vt = new VelocityTemplate();
		Hashtable<String, String> hasht = new Hashtable<String, String>();
		String queryInsert = null;
		hasht.put("nameTable", nameTable);
		hasht.put("columA", columA);
		//hasht.put("columB", columB);
		hasht.put("columC", columC);
		hasht.put("columD", columD);
		//hasht.put("columE", columE);
		hasht.put("valueA", valueA);
		//hasht.put("valueB", "'" + valueB + "'");
		hasht.put("valueC", valueC);
		hasht.put("valueD", valueD);
		//hasht.put("valueE", "'" + valueE + "'");
		
		try {
			queryInsert = vt.getRequestXmlVelocity("QueryTemplate", hasht);
		}
		catch (Exception e) {
			LOGGER.error("Impossible to create page template.");
			LOGGER.debug(e);
		}
		return queryInsert;
	}
}
