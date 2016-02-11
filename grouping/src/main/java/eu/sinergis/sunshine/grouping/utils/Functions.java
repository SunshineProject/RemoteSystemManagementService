package eu.sinergis.sunshine.grouping.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.log4j.Logger;

import eu.sinergis.sunshine.grouping.pojo.ObjLampCtrl;

public final class Functions {
	final static Logger LOGGER = Logger.getLogger(Functions.class);
	
	//TODO da mettere statica in util
	public static String convertStreamToString(InputStream is) {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		}
		catch (IOException e) {
			LOGGER.error(e);
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				LOGGER.error(e);
			}
		}
		return sb.toString();
	}
	
	public static String addSeparator(String path) {
		if (path.charAt(path.length() - 1) != File.separatorChar) {
			path += File.separator;
		}
		return path;
	}
	
	public static String loadStream(InputStream s) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(s));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		return sb.toString();
	}
	
	public static boolean updateObjectLamp(ObjLampCtrl objLamp, String usrId, String ctrlId, String typeOfLamp,
			String nominalPower, String height, String connect, Boolean valid) {
		try {
			if (usrId != null) {
				objLamp.setUsrId(usrId);
			}
			if (ctrlId != null) {
				objLamp.setCtrlId(ctrlId);
			}
			if (typeOfLamp != null) {
				objLamp.setTypeOfLamp(typeOfLamp);
			}
			if (nominalPower != null) {
				objLamp.setNominalPower(Double.parseDouble(nominalPower));
			}
			if (height != null) {
				objLamp.setHeight(Double.parseDouble(height));
			}
			if (connect != null && objLamp.getTypeGroup().equals("Q")) {
				if (connect.equalsIgnoreCase("TRUE")) {
					objLamp.setConnect(true);
				}
				else {
					objLamp.setConnect(false);
				}
				
			}
			if (valid != null) {
				objLamp.setValid(valid);
				if (valid) {
					objLamp.setDateValid(new Date());
				}
				else {
					objLamp.setDateValid(null);
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Parameters in input malformed.");
			return false;
		}
		return true;
	}
	
	public static String addUrlSection(String url, String section) {
		if (url.substring(url.length() - 1) == "/") {
			if (section.substring(0, 1) == "/") {
				return url.concat(section.substring(1, section.length() - 1));
			}
			return url.concat(section);
		}
		else {
			if (section.substring(0, 1) == "/") {
				return url.concat(section);
			}
			return url + "/" + section;
		}
		
	}
}
