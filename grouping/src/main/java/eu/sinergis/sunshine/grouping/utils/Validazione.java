package eu.sinergis.sunshine.grouping.utils;

import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validazione {
	public final static String patternString = "[^A-Za-z0-9-_]";
	public final static String patternType = "[QSV]{1,1}";
	public final static String patternInt = "[^0-9]";
	public final static String patternUri = "^(https?|ftp|file|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	
	public static boolean validationString(List<String> param) {
		Pattern p = Pattern.compile(patternString);
		Matcher m = null;
		boolean find = false;
		for (int i = 0; i < param.size(); i++) {
			m = p.matcher(param.get(i));
			if (m.find()) {
				find = true;
				break;
			}
		}
		return find;
	}
	
	public static boolean validationString(String param) {
		Pattern p = Pattern.compile(patternString);
		Matcher m = null;
		boolean find = false;
		m = p.matcher(param);
		if (m.find()) {
			find = true;
		}
		return find;
	}
	
	public static boolean validationType(String param) {
		Pattern p = Pattern.compile(patternType);
		Matcher m = null;
		boolean find = false;
		m = p.matcher(param);
		if (param.length() == 1 && m.find()) {
			find = true;
		}
		return find;
	}
	
	public static boolean validationUri(String param) {
		boolean find = false;
		try {
			new URL(param);
		}
		catch (Exception e) {
			find = true;
		}
		
		return find;
	}
	
	public static boolean validationInt(String param) {
		Pattern p = Pattern.compile(patternInt);
		Matcher m = null;
		boolean find = false;
		m = p.matcher(param);
		if (m.find()) {
			find = true;
		}
		return find;
	}
	
}
