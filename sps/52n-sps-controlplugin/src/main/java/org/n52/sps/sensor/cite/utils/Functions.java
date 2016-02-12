package org.n52.sps.sensor.cite.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Functions {
	private static final Logger LOGGER = LoggerFactory.getLogger(Functions.class);
	
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
			LOGGER.error("", e);
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {
				LOGGER.error("", e);
			}
		}
		return sb.toString();
	}
	
	public static String normalizePath(String url) {
		if (!(url.substring(url.length() - 1).equals("\\") || url.substring(url.length() - 1).equals("/"))) {
			if (ReadFromConfig.loadByName("unix").equals("Y")) {
				return url.concat("/");
			}
			else
				return url.concat("\\");
		}
		return url;
	}
	
	public static String getValueFromJson(String jsonBody, String objectInJson) {
		JSONObject obj = new JSONObject(jsonBody);
		return obj.getString(objectInJson);
	}
	
	public static int getValueIntFromJson(String jsonBody, String objectInJson) {
		JSONObject obj = new JSONObject(jsonBody);
		return obj.getInt(objectInJson);
	}
	
	/**
	 * @param jsonBody
	 * @param objectInJson
	 * @return
	 */
	public static List<String> getDeviceAttributesFromJson(String jsonBody, String sosId, String ctrlId) {
		JSONObject obj = new JSONObject(jsonBody);
		JSONObject group = obj.getJSONObject(ReadFromConfig.loadByName("oggettoGroup"));
		JSONArray devices = obj.getJSONArray(ReadFromConfig.loadByName("oggettoDevices"));
		List<String> spsIds = new ArrayList<String>();
		for (int i = 0; i < devices.length(); i++) {
			JSONObject childJSONObject = devices.getJSONObject(i);
			spsIds.add(getImpianto(childJSONObject.getString(sosId)) + "," + childJSONObject.getString(ctrlId));
		}
		
		return spsIds;
	}
	
	public static String getImpianto(String sosProcedure) {
		String nProc = sosProcedure.split("/")[sosProcedure.split("/").length - 1];
		int n = Integer.parseInt(nProc.split("-")[1]);
		if (n < 200) {
			return "001";
		}
		else if (n >= 200 && n < 300) {
			return "002";
		}
		else if (n >= 300 && n < 400) {
			return "003";
		}
		else {
			return "004";
		}
		
	}
	
	public static String normalizeJsonResponse(String jsonString) {
		if (jsonString.indexOf("[") == 0 && jsonString.indexOf("]", jsonString.length() - 2) == jsonString.length() - 2) {
			return jsonString.substring(1, jsonString.length() - 2);
		}
		else
			return jsonString;
	}
	
	public static String getCronStringFromDays(String daysOfWeek) {
		String cronStrin = "";
		for (int i = 0; i < daysOfWeek.length(); i++) {
			if (daysOfWeek.substring(i, i + 1).equals(String.valueOf(1))) {
				cronStrin = cronStrin + String.valueOf(i + 1) + ",";
			}
			
		}
		cronStrin = cronStrin.substring(0, cronStrin.length() - 1);
		return cronStrin;
	}
	
	public static Date addPeriod(Date d, int offset, int misure) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(misure, offset);
		return cal.getTime();
	}
	
	public static Date getMiddleDay() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		return cal.getTime();
	}
}
