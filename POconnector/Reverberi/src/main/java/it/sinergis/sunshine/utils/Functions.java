package it.sinergis.sunshine.utils;

import it.sinergis.sunshine.velocity.SetupPageTemplate;

import org.apache.log4j.Logger;

public class Functions {
	private static final Logger logger = Logger.getLogger(Functions.class);
	
	/**
	 * Metodo che restituisce la query di comando fonte luce attraverso il template velocity
	 * 
	 * @param columA
	 * @param columB
	 * @param columC
	 * @param columD
	 * @param columE
	 * @param valueA
	 * @param valueB
	 * @param valueC
	 * @param valueD
	 * @param valueE
	 * @return
	 */
	public static String createQueryLamp(String nameTable, String columA, String columB, String columC, String columD,
			String columE, String valueA, String valueB, String valueC, String valueD, String valueE) {
		SetupPageTemplate temp = new SetupPageTemplate();
		String response = null;
		try {
			response = temp.getTemplate(nameTable, columA, columB, columC, columD, columE, valueA, valueB, valueC,
					valueD, valueE);
		}
		catch (Exception e) {
			logger.error("Parameters are not properly valued", e);
		}
		return response;
	}
}
