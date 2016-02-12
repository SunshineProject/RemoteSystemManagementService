/**
 * Copyright (C) 2012 by 52 North Initiative for Geospatial Open Source Software GmbH Contact: Andreas Wytzisk 52 North
 * Initiative for Geospatial Open Source Software GmbH Martin-Luther-King-Weg 24 48155 Muenster, Germany
 * info@52north.org This program is free software; you can redistribute and/or modify it under the terms of the GNU
 * General Public License version 2 as published by the Free Software Foundation. This program is distributed WITHOUT
 * ANY WARRANTY; even without the implied WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA or visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.sps.sensor.cite.exec;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.opengis.swe.x20.DataRecordType;
import net.opengis.swe.x20.DataRecordType.Field;

import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.SensorTaskStatus;
import org.n52.sps.sensor.cite.pojo.Command;
import org.n52.sps.sensor.cite.utils.QuartzUtil;
import org.n52.sps.sensor.cite.utils.ReadFromConfig;
import org.n52.sps.sensor.model.SensorTask;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CiteTaskSimulation implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CiteTaskSimulation.class);
	
	private static final int ONE_TENTH_OF_EXECUTION_IN_MILLISECONDS = 60 * 1000 / 10; // 6sec.
	
	private DataRecordType[] inputParameters; // TODO do s/th with it?!
	private SensorTask sensorTask;
	private CiteTaskScheduler scheduler;
	private CiteTaskSimulation internalRechargeSimulation;
	private SensorTaskService sensorTaskService;
	
	public static CiteTaskSimulation createTaskSimulation(SensorTask sensorTask, DataRecordType[] inputParameters) {
		return new CiteTaskSimulation(sensorTask, inputParameters);
	}
	
	public static CiteTaskSimulation createInternalTaskSimulation(SensorTask sensorTask) {
		return new CiteTaskSimulation(sensorTask, null);
	}
	
	private CiteTaskSimulation(SensorTask sensorTask, DataRecordType[] inputParameters) {
		this.sensorTask = sensorTask;
		this.inputParameters = inputParameters;
	}
	
	public void setSensorTaskService(SensorTaskService sensorTaskService) {
		this.sensorTaskService = sensorTaskService;
	}
	
	public void run() {
		try {
			simulateProcessingWhileUpdatingTaskPercentCompletion();
		}
		catch (SchedulerException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getLocalizedMessage());
		}
		// **********************
		//************************
		//questa parte va tolta tutta
		//		if (scheduler.mustRechargeBatteries()) {
		//			// schedule recharge task immediately
		//			try {
		//				if (scheduler.schedule(internalRechargeSimulation)) {
		//					LOGGER.info("Running battery recharge task.");
		//					sensorTask.setRequestStatus(ACCEPTED);
		//					scheduler.resetBatteryStatus();
		//				}
		//				else {
		//					sensorTask.setRequestStatus(REJECTED);
		//				}
		//			}
		//			catch (SchedulerException e) {
		//				LOGGER.error(e.getLocalizedMessage());
		//			}
		//		}
	}
	
	private void simulateProcessingWhileUpdatingTaskPercentCompletion() throws SchedulerException {
		sensorTask.setTaskStatus(SensorTaskStatus.INEXECUTION);
		Calendar estimatedTimeToC = new GregorianCalendar();
		
		estimatedTimeToC.add(Calendar.MILLISECOND, ONE_TENTH_OF_EXECUTION_IN_MILLISECONDS);
		sensorTask.setEstimatedToC(estimatedTimeToC);
		
		LOGGER.info(sensorTask.getProcedure());
		LOGGER.info(sensorTask.getTaskId());
		String response = "";
		Command cmd = new Command();
		cmd.setDimmer(-1);
		cmd.setFrequency(-1);
		cmd.setPriority(1);
		for (DataRecordType elemInput : inputParameters) {
			Field[] fiedArray = elemInput.getFieldArray();
			int i = 0;
			for (Field field : fiedArray) {
				cmd = getDataFromField(field, i, cmd);
				i = i + 1;
			}
		}
		
		//now 
		LOGGER.info(response);
		LOGGER.info(sensorTask.getProcedure());
		//tecnologia reverberi
		if (sensorTask.getProcedure().indexOf(ReadFromConfig.loadByName("procedureReverberiBassano")) != -1
				|| sensorTask.getProcedure().indexOf(ReadFromConfig.loadByName("procedureReverberiCles")) != -1) {
			//qui lanciare elaborazione per REVERBERI
			LOGGER.info("REVERBERI, procedure: " + sensorTask.getProcedure() + " con DIMMER: " + cmd.getDimmer());
			String codespace = null;
			if (sensorTask.getProcedure().indexOf(ReadFromConfig.loadByName("procedureReverberiBassano")) != -1) {
				codespace = ReadFromConfig.loadByName("procedureReverberiBassano");
			}
			else {
				codespace = ReadFromConfig.loadByName("procedureReverberiCles");
			}
			//verifico se e' un comando di reset (dimming = 110), se lo e', metto dimmer a 0 perche' reverberi lo interpreta come luce piena
			if (cmd.getDimmer() == 110) {
				cmd.setDimmer(0);
			}
			//tolgo l'else perche' anche reverberi funziona in percentuale di utilizzo
			/*
			 * else { cmd.setDimmer(100 - cmd.getDimmer()); }
			 */
			LOGGER.info("Creazione del job con identity: " + sensorTask.getProcedure() + ";" + sensorTask.getTaskId()
					+ ";" + cmd.getDimmer());
			JobDetail job = JobBuilder.newJob(JobReverberi.class)
					.withIdentity(sensorTask.getProcedure(), sensorTask.getTaskId()).requestRecovery(true)
					.usingJobData("dimmer", cmd.getDimmer()).usingJobData("frequency", cmd.getFrequency())
					.usingJobData("codespace", codespace).usingJobData("priority", cmd.getPriority()).build();
			//schedule it.
			ArrayList<Trigger> triggerList = getNewTrigger(cmd, sensorTask.getTaskId());
			for (Trigger trigger : triggerList) {
				if (trigger != null) {
					Scheduler scheduler = new StdSchedulerFactory().getScheduler();
					scheduler.start();
					sensorTask.setPercentCompletion(50);
					//sensorTask.setTaskStatus(SensorTaskStatus.INEXECUTION);
					sensorTask.setTaskStatus(SensorTaskStatus.COMPLETED);
					//sensorTaskService.updateSensorTask(sensorTask);
					scheduler.scheduleJob(job, trigger);
				}
			}
			
		}//tecnologia SET
		else if (sensorTask.getProcedure().indexOf(ReadFromConfig.loadByName("procedureSet")) != -1) {
			LOGGER.info("SET, procedure: " + sensorTask.getProcedure() + " con DIMMER: " + cmd.getDimmer());
			//qui lanciare elaborazione per SET
			//imposto job e trigger sia per tutte le tipologie di gruppo
			String codespace = ReadFromConfig.loadByName("procedureSet");
			String command = "DIMMER";
			String dest_type = "IMP";
			if (cmd.getDimmer() == 0) {
				command = "OFF";
			}
			if (cmd.getDimmer() == 110) {
				command = "RESET";
			}
			if (cmd.getDimmer() == 200) {
				command = "CONNECT";
				dest_type = "APP";
			}
			if (cmd.getDimmer() == 300) {
				command = "DISCONNECT";
				dest_type = "APP";
			}
			LOGGER.info("Creazione del job con identity: " + sensorTask.getProcedure() + ";" + sensorTask.getTaskId()
					+ ";" + cmd.getDimmer());
			JobDetail job = JobBuilder.newJob(JobSet.class)
					.withIdentity(sensorTask.getProcedure(), sensorTask.getTaskId()).requestRecovery(true)
					.usingJobData("dimmer", cmd.getDimmer()).usingJobData("command", command)
					.usingJobData("cmd_executed", "0").usingJobData("dest_type", dest_type)
					.usingJobData("frequency", cmd.getFrequency()).usingJobData("codespace", codespace)
					.usingJobData("priority", cmd.getPriority()).build();
			//schedule it.
			ArrayList<Trigger> triggerList = getNewTrigger(cmd, sensorTask.getTaskId());
			for (Trigger trigger : triggerList) {
				if (trigger != null) {
					Scheduler scheduler = new StdSchedulerFactory().getScheduler();
					scheduler.start();
					sensorTask.setPercentCompletion(50); //setto al 10%
					//sensorTask.setTaskStatus(SensorTaskStatus.INEXECUTION);
					sensorTask.setTaskStatus(SensorTaskStatus.COMPLETED);
					//sensorTaskService.updateSensorTask(sensorTask);
					scheduler.scheduleJob(job, trigger);
				}
			}
			
		}
		else {
			LOGGER.info("Thread isn't started!");
			LOGGER.info("DateAttivation: " + cmd.getDateAttivation() + " DateExpire: " + cmd.getDateToExpiry()
					+ " Dimmer: " + cmd.getDimmer() + " Frequency: " + cmd.getFrequency());
			sensorTask.setTaskStatus(SensorTaskStatus.FAILED);
		}
		
	}
	
	/**
	 * @param cmd
	 * @param ctrlid
	 * @return
	 */
	private final ArrayList<Trigger> getNewTrigger(Command cmd, String taskId) {
		ArrayList<Trigger> triggerList = new ArrayList<Trigger>();
		//Trigger trigger = null;
		QuartzUtil quartz = new QuartzUtil();
		
		//		casi possibili
		//		start + end + freq									-> comando standard 
		//		start + freq										-> comando senza fine
		//		start (per comando unico)							-> comando secco
		//		start + end + daysOfWeek + ActivationHour			-> comando con i giorni della settimana specificati
		//		start + listOfDates									-> comando con lista di date
		//		
		
		if (cmd.getDateAttivation() != null && cmd.getDateToExpiry() != null && cmd.getFrequency() != -1) {
			//se manca la frequenza ma c'e' array dei giorni + data inizio + data fine.. va fatto altro if
			triggerList.add(quartz.createCompleteTrigger(sensorTask, taskId, cmd));
			
		}
		//thread senza fine
		else {
			if (cmd.getFrequency() != -1 && cmd.getDateAttivation() != null) {
				
				//qui se c'è l'array di date e non c'e' la freq do precedenza ad array di date (quindi devo aggiungere un if)
				triggerList.add(quartz.createNotCompleteTrigger(sensorTask, taskId, cmd));
				
			}
			//se non c'è la frequenza, viene eseguito una sola volta
			else {
				
				if (cmd.getFrequency() == -1 && cmd.getDateAttivation() != null && cmd.getDayOfWeek() == null
						&& cmd.getListOfDays() == null) {
					//qui faccio oneshot a meno che non ci sia l'array di giorni
					triggerList.add(quartz.createOneShotTrigger(sensorTask, taskId, cmd));
					
				}
				else {
					if (cmd.getDateAttivation() != null && cmd.getDateToExpiry() != null && cmd.getDayOfWeek() != null
							&& cmd.getActivationHour() != null) {
						triggerList.add(quartz.createNotCronTrigger(sensorTask, taskId, cmd));
						//creare qui un cron trigger
					}
					else {
						if (cmd.getDateAttivation() != null && cmd.getListOfDays() != null) {
							//creare qui un trigger per ogni data??? da vedere
							triggerList.addAll(quartz.createListTrigger(sensorTask, taskId, cmd));
							
						}
						
					}
				}
			}
		}
		return triggerList;
	}
	
	public SensorTask getSensorTask() {
		return sensorTask;
	}
	
	public boolean isTaskExecuting() {
		return sensorTask.isExecuting();
	}
	
	public void setRechargeAfterTaskExecutionsSimulation(CiteTaskScheduler scheduler,
			CiteTaskSimulation rechargeSimulation) {
		this.scheduler = scheduler;
		this.internalRechargeSimulation = rechargeSimulation;
	}
	
	/**
	 * Parsa l'xml generato dall'inserimento dei parametri di ingresso e ritorna i valori
	 * 
	 * @param field
	 * @return
	 */
	private Command getDataFromField(Field field, int cont, Command cmd) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		NodeList nList = null;
		ArrayList<String> categoryArray = new ArrayList(Arrays.asList("dateAttivation", "dateToExpiry", "dimmer",
				"frequency", "timezone", "dayoftheweek", "activationhour", "listofday", "priority"));
		
		try {
			builder = factory.newDocumentBuilder();
			
			LOGGER.info("");
			LOGGER.info(field.toString());
			
			InputSource is = new InputSource(new StringReader(field.toString()));
			Document document = builder.parse(is);
			//nList = document.getElementsByTagNameNS("swe", "value");
			nList = document.getFirstChild().getChildNodes();
			
		}
		catch (Exception e) {
			LOGGER.error("Error in conversion to xml document", e);
			return null;
		}
		
		//esci se non c'e' nulla da leggere
		if (nList == null) {
			return null;
		}
		
		Node node = null;
		
		for (int i = 0; i < nList.getLength(); i++) {
			node = nList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getTextContent() != null && node.getTextContent() != "") {
				LOGGER.info(node.getTextContent());
				LOGGER.info(node.getNamespaceURI());
				LOGGER.info(node.getNodeValue());
				LOGGER.info(node.getLocalName());
				LOGGER.info(node.getPrefix());
				String category = node.getPreviousSibling().getPreviousSibling().getTextContent();
				switch (categoryArray.indexOf(category)) {
					case 0://TAG: STRINGDATA
						cmd.setDateAttivation(getDateFromString(node.getTextContent()));
						break;
					case 1:
						try {//TAG: STRINGDATA
							cmd.setDateToExpiry(getDateFromString(node.getTextContent()));
						}
						catch (Exception e) {
							cmd.setFrequency(Integer.parseInt((node.getTextContent())));
						}
						break;
					case 2:
						if (cmd.getDimmer() == -1) {
							cmd.setDimmer(Integer.parseInt((node.getTextContent())));
						}
						else {
							cmd.setFrequency(Integer.parseInt((node.getTextContent())));
						}
						break;
					case 3:
						cmd.setFrequency(Integer.parseInt((node.getTextContent())));
						break;
					case 4:
						LOGGER.info("TIMEZONE");
						cmd.setTimezone(Integer.parseInt((node.getTextContent())));
						break;
					case 5:
						LOGGER.info("DAY");
						cmd.setDayOfWeek(node.getTextContent());
						break;
					case 6:
						LOGGER.info("HOUR");
						cmd.setActivationHour(node.getTextContent());
						break;
					case 7:
						LOGGER.info("LISTDATE");
						cmd.setListOfDays(node.getTextContent());
						break;
					case 8:
						LOGGER.info("PRIORITY");
						cmd.setPriority(Integer.parseInt((node.getTextContent())));
						break;
				}
			}
		}
		return cmd;
	}
	
	//	private String parseResponse(String resp) {
	//		String url = Functions.normalizePath(ReadFromConfig.loadByName("pathInterfaceService4Reverberi"));
	//		String p1 = resp.split("@")[0].split("=")[1];
	//		LOGGER.info(resp);
	//		//TODO da sistemare in base ai parametri di ingresso
	//		return url.concat("?PARAM1=" + p1);
	//	}
	
	private Date getDateFromString(String valueString) {
		Date date;
		try {
			//			String dateString = valueString.substring(0, 4) + "-" + valueString.substring(4, 6) + "-"
			//					+ valueString.substring(6, 8) + " " + valueString.substring(8, 10) + ":"
			//					+ valueString.substring(10, 12) + ":" + valueString.substring(12);
			//			DateFormat formatter;
			//			//"2011-04-15T20:08:18Z"
			//			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			date = formatter.parse(valueString);
			LOGGER.info("Command will execute in date " + date);
			//date = ISODateTimeFormat.dateTimeParser().parseDateTime(dateString);
		}
		catch (ParseException e) {
			LOGGER.error("", e);
			return null;
		}
		return date;
	}
	
}
