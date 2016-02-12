package org.n52.sps.sensor.cite.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Httprequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(Httprequest.class);
	
	public String getRequest(String url) {
		try {
			
			String result = sendGet(url);
			
			//System.out.println(response.getStatusLine());
			if (result != null) {
				return result;
			}
			
		}
		catch (IOException e) {
			LOGGER.error("Errore in connessione verso " + url, e);
		}
		return null;
	}
	
	public String postCommand(String url, String xmlInsert) {
		try {
			
			String result = sendPost(url, xmlInsert);
			return result;
		}
		catch (IOException e) {
			LOGGER.error("Errore in connessione verso " + url, e);
		}
		return null;
	}
	
	private String sendGet(String url) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		//String authStr = ReadFromConfig.loadByName("userRedis") + ":" + ReadFromConfig.loadByName("pwdRedis");
		//String authEncoded = Base64.encodeBase64String(authStr.getBytes());
		String result = null;
		HttpGet hget = new HttpGet(url);
		//hget.setHeader("Authorization", "Basic " + authEncoded);
		
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
			LOGGER.error("", e);
		}
		finally {
			//httpclient.getConnectionManager().shutdown();
		}
		
		return result;
	}
	
	private String sendPost(String url, String xmlPost) throws UnsupportedEncodingException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		String result = null;
		HttpPost hpost = new HttpPost(url);
		hpost.setHeader("Content-Type", "application/xml;charset=UTF-8");
		
		String payload = xmlPost;
		StringEntity entity = new StringEntity(payload);
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
			LOGGER.error("", e);
		}
		
		return result;
	}
	
}
