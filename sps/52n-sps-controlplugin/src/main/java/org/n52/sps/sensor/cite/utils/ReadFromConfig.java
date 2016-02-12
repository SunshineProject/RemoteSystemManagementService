package org.n52.sps.sensor.cite.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ReadFromConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReadFromConfig.class);
	//private static Logger LOGGER = Logger.getLogger(ReadFromConfig.class);
	private static Properties properties;
	
	private ReadFromConfig() {
		
	}
	
	public static Properties readFromConfig() {
		InputStream inputStream = ReadFromConfig.class.getClassLoader().getResourceAsStream("config.properties");
		properties = new Properties();
		try {
			properties.load(inputStream);
		}
		catch (IOException e) {
			LOGGER.error("errore nel caricamento del file di configurazione");
			LOGGER.debug("", e);
		}
		return properties;
	}
	
	public static String loadByName(String property) {
		if (properties == null) {
			properties = readFromConfig();
		}
		return (String) properties.get(property);
	}
}
