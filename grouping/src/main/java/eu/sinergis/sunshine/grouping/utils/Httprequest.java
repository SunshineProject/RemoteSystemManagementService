package eu.sinergis.sunshine.grouping.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import eu.sinergis.sunshine.grouping.velocity.SetupInsertSensorTemplate;

public class Httprequest {
	private static final Logger LOGGER = Logger.getLogger(Httprequest.class);
	
	/**
	 * @param url
	 * @param user
	 * @param pwd
	 * @return
	 */
	public String getAuth(String url, String user, String pwd) {
		try {
			
			String result = sendGet(url, user, pwd);
			return result;
		}
		catch (IOException e) {
			LOGGER.error("Errore in connessione verso " + url, e);
		}
		return null;
	}
	
	/**
	 * @param url
	 * @return
	 */
	public String get(String url) {
		try {
			
			String result = sendGet(url);
			return result;
		}
		catch (IOException e) {
			LOGGER.error("Errore in connessione verso " + url, e);
		}
		return null;
	}
	
	/**
	 * @param url
	 * @param offering
	 * @param offeringDescription
	 * @param listPointxy
	 * @param procedure
	 * @param pluginName
	 * @param PluginIdentifier
	 * @param PluginAbstract
	 * @param PluginLinkDownload
	 * @param PluginTaskIdentifier
	 * @return
	 */
	public String postInsertSensor(String url, String offering, String offeringDescription, List<String> listPointxy,
			String procedure, String pluginName, String PluginIdentifier, String PluginAbstract,
			String PluginLinkDownload, String PluginTaskIdentifier) {
		try {
			
			String result = sendPostAuth(url, offering, offeringDescription, listPointxy, procedure, pluginName,
					PluginIdentifier, PluginAbstract, PluginLinkDownload, PluginTaskIdentifier,
					ReadFromConfig.loadByName("userSpsTomcat"), ReadFromConfig.loadByName("pwdSpsTomcat"));
			return result;
		}
		catch (IOException e) {
			LOGGER.error("Errore in connessione verso " + url, e);
		}
		return null;
	}
	
	/**
	 * @param url
	 * @param user
	 * @param pwd
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String sendGet(String url, String user, String pwd) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String authStr = user + ":" + pwd;
		String authEncoded = Base64.encodeBase64String(authStr.getBytes());
		String result = null;
		HttpGet hget = new HttpGet(url);
		hget.setHeader("Authorization", "Basic " + authEncoded);
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = Functions.convertStreamToString(instream);
				return result;
			}
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Errore in HttpRequest get per l'url " + url, e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		finally {
			//httpclient.getConnectionManager().shutdown();
		}
		
		return result;
	}
	
	private String sendGet(String url) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String result = null;
		HttpGet hget = new HttpGet(url);
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = Functions.convertStreamToString(instream);
				return result;
			}
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Errore in HttpRequest get per l'url " + url, e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		finally {
			//httpclient.getConnectionManager().shutdown();
		}
		
		return result;
	}
	
	private String sendPostAuth(String url, String offering, String offeringDescription, List<String> listPointxy,
			String procedure, String pluginName, String PluginIdentifier, String PluginAbstract,
			String PluginLinkDownload, String PluginTaskIdentifier, String user, String pwd)
			throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String authStr = user + ":" + pwd;
		String authEncoded = Base64.encodeBase64String(authStr.getBytes());
		String result = null;
		HttpPost hpost = new HttpPost(url);
		hpost.setHeader("Authorization", "Basic " + authEncoded);
		hpost.setHeader("Content-Type", "application/xml;charset=UTF-8");
		
		SetupInsertSensorTemplate template = new SetupInsertSensorTemplate();
		String payload = template.getTemplateInsertSensor(offering, offeringDescription, listPointxy, procedure,
				pluginName, PluginIdentifier, PluginAbstract, PluginLinkDownload, PluginTaskIdentifier);
		StringEntity entity = new StringEntity(payload);
		LOGGER.info(payload);
		hpost.setEntity(entity);
		
		HttpResponse response = null;
		try {
			response = httpclient.execute(hpost);
			HttpEntity entityResponse = response.getEntity();
			if (entityResponse != null) {
				InputStream instream = entityResponse.getContent();
				result = Functions.convertStreamToString(instream);
				return result;
			}
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Errore in HttpRequest get per l'url " + url, e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		
		return result;
	}
	
	public String getCapa(String url) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet hget = new HttpGet(url);
		HttpResponse response = null;
		String result = null;
		try {
			response = httpclient.execute(hget);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = Functions.convertStreamToString(instream);
			}
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Errore in HttpRequest get per l'url " + url, e);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e);
		}
		return result;
	}
	
}
