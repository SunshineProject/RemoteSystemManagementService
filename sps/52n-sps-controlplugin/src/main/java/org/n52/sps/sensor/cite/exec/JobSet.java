package org.n52.sps.sensor.cite.exec;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.n52.sps.sensor.cite.delegate.DelegateDatabase;
import org.n52.sps.sensor.cite.pojo.ObjGroupComposition;
import org.n52.sps.sensor.cite.pojo.ObjLampCtrl;
import org.n52.sps.sensor.cite.utils.Functions;
import org.n52.sps.sensor.cite.utils.Httprequest;
import org.n52.sps.sensor.cite.utils.ReadFromConfig;
import org.n52.sps.sensor.cite.velocity.SetupTemplate;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DisallowConcurrentExecution
public class JobSet implements Job {
	private static final Logger LOGGER = LoggerFactory.getLogger(JobSet.class);
	
	public void execute(JobExecutionContext cntxt) throws JobExecutionException {
		
		LOGGER.info("JOB SET");
		Date dateNow = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateNow);
		calendar.set(Calendar.HOUR_OF_DAY, 24);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.HOUR, -12);
		Date minDate = calendar.getTime();
		calendar.add(Calendar.HOUR, 24);
		Date maxDate = calendar.getTime();
		JobKey key = cntxt.getJobDetail().getKey();
		//to read data
		JobDataMap dataMap = cntxt.getJobDetail().getJobDataMap();
		String codespace = dataMap.getString("codespace");
		DelegateDatabase grouping = new DelegateDatabase();
		List<ObjLampCtrl> devices = grouping.getObjLampCtrlFromSpsId(key.getName(), codespace);
		if (devices.isEmpty()) {
			LOGGER.error("Device " + codespace + " : " + key.getName() + "not founded.");
			return;
		}
		
		ObjLampCtrl device = devices.get(0);
		int dimmerValue = dataMap.getInt("dimmer");
		
		String cmd_executed = dataMap.getString("cmd_executed");
		String dest_type = dataMap.getString("dest_type"); //
		String command = dataMap.getString("command");
		String type = device.getTypeGroup();
		int thisPriority = dataMap.getInt("priority");
		int freq = dataMap.getInt("frequency");
		
		LOGGER.info("DIMMER : " + dimmerValue);
		LOGGER.info("TYPE : " + type);
		LOGGER.info("PRIORITY : " + thisPriority);
		LOGGER.info("FREQUENCY : " + freq);
		
		Integer priority = -1;
		LOGGER.info("INIZIO CONTRATTAZIONE PRIORITA'");
		priority = getPriority(minDate, maxDate, priority, key.getName());
		
		//priorita' 5 e' prioritaria rispetto alla 1
		LOGGER.info("Priority job : " + thisPriority);
		LOGGER.info("Priority obtained : " + priority);
		if (thisPriority < priority) {
			LOGGER.info("Exit due to priority");
			return;
		}
		
		//		if (freq == -1) {
		//			String xmlInsert = classTemplate.getTemplateSendCommandSet("IMP", impianto, "CONNECT", "0",
		//					String.valueOf(0), "BROADCAST");
		//			Httprequest clientHttp = new Httprequest();
		//			//ora bisogna fare una post verso SET
		//			String responseSETService = clientHttp.postCommand(ReadFromConfig.loadByName("uriSet"), xmlInsert);
		//			sensorTask.setPercentCompletion(100);
		//			sensorTask.setTaskStatus(SensorTaskStatus.COMPLETED);
		//			sensorTaskService.updateSensorTask(sensorTask);
		//			LOGGER.info(responseSETService);
		//		}
		LOGGER.info("Point type : " + type);
		if (type.equalsIgnoreCase("S") || type.equalsIgnoreCase("Q")) {
			boolean done = false;
			int cont = 0;
			while (done == false && cont < 9) {
				LOGGER.info("TENTATIVO " + cont + " INVIO DEL COMANDO");
				try {
					String impianto = Functions.getImpianto(device.getSosId());
					String ctrlId = device.getCtrlId();
					
					//se non c'e' la frequenza mando un connect perche' il comando deve essere eseguito subito
					String xmlInsert = getTemplate(type, ctrlId, command, dest_type, impianto, cmd_executed,
							dimmerValue);
					
					Httprequest clientHttp = new Httprequest();
					//ora bisogna fare una post verso SET
					String response = clientHttp.postCommand(ReadFromConfig.loadByName("uriSet"), xmlInsert);
					LOGGER.info("INVIATO");
					LOGGER.info(response);
					done = true;
				}
				catch (Exception e) {
					LOGGER.error("Errore in invio", e);
					cont = cont + 1;
				}
				
			}
			
		}
		else {
			if (type.equalsIgnoreCase("V")) {
				boolean done = false;
				int cont = 0;
				while (done == false && cont < 9) {
					LOGGER.info("TENTATIVO " + cont + "INVIO DEL COMANDO");
					try {
						List<ObjGroupComposition> composition = grouping
								.getObjGroupCompositionFromLampCtrlListId(device.getId());
						for (ObjGroupComposition elem : composition) {
							ObjLampCtrl lamp = grouping.getObjLampCtrlFromID(elem.getLampId(), codespace);
							LOGGER.info("Group with id " + elem.getGroupId());
							LOGGER.info("Element " + lamp.getCtrlId());
							String ctrlId = lamp.getCtrlId();
							String impianto = Functions.getImpianto(lamp.getSosId());
							
							String xmlInsert = getTemplate(type, ctrlId, command, dest_type, impianto, cmd_executed,
									dimmerValue);
							Httprequest clientHttp = new Httprequest();
							//ora bisogna fare una post verso SET
							String response = clientHttp.postCommand(ReadFromConfig.loadByName("uriSet"), xmlInsert);
							
							LOGGER.info(response);
							done = true;
						}
					}
					catch (Exception e) {
						LOGGER.error("Errore in invio", e);
						cont = cont + 1;
					}
				}
				
			}
			else {
				LOGGER.error("Type " + type + " not founded.");
				return;
			}
		}
	}
	
	//	private Date getEndTimeTrigger(String taskId) throws SchedulerException {
	//		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	//		TriggerKey keyHumanTrigger = new TriggerKey(taskId, "ROV");
	//		Trigger trigger = scheduler.getTrigger(keyHumanTrigger);
	//		return trigger.getEndTime();
	//		
	//	}
	//	
	//	private Date getNextFireTimeTrigger(String taskId) throws SchedulerException {
	//		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	//		TriggerKey keyHumanTrigger = new TriggerKey(taskId, "ROV");
	//		Trigger trigger = scheduler.getTrigger(keyHumanTrigger);
	//		return trigger.getNextFireTime();
	//	}
	
	private String getTemplate(String type, String ctrlId, String command, String dest_type, String impianto,
			String cmd_executed, int dimmerValue) {
		String xmlInsert = "";
		SetupTemplate classTemplate = new SetupTemplate();
		if (type.equalsIgnoreCase("Q")) {
			ctrlId = "BROADCAST";
			
		}
		
		//DIMMING -> comando completo
		//CONNECT e DISCONNECT -> solo dest_type = APP e command
		//il resto, tutto tranne dimming
		if (command.equals("DIMMER")) {
			xmlInsert = classTemplate.getTemplateSendCommandSet(dest_type, impianto, command, cmd_executed,
					String.valueOf(dimmerValue), ctrlId);
		}
		else {
			if (command.equals("CONNECT") || command.equals("DISCONNECT")) {
				xmlInsert = classTemplate.getTemplateSendCommandSetConnect(dest_type, impianto, command);
			}
			else {
				xmlInsert = classTemplate.getTemplateSendCommandSetCtrlid(dest_type, impianto, command, cmd_executed,
						ctrlId);
			}
		}
		return xmlInsert;
	}
	
	private int getPriority(Date minDate, Date maxDate, int priority, String spsid) {
		try {
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			
			List<Trigger> triggers = new ArrayList<Trigger>();
			for (String groupName : scheduler.getJobGroupNames()) {
				
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					if (jobKey.getName().equals(spsid)) {
						List<Trigger> listTrigger = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
						for (Trigger trig : listTrigger) {
							//se sono tra mezzogiorno del giorno x e mezzo giorno del giorno x+1
							if (trig.getNextFireTime() != null && trig.getNextFireTime().after(minDate)
									&& trig.getNextFireTime().before(maxDate)) {
								//int p = scheduler.getJobDetail(jobKey).getJobDataMap().getInt("priority");
								String description = trig.getDescription();
								
								String p = "";
								//int dimmerValue = 110;
								try {
									//dimmerValue = Integer.parseInt(description.split("-")[0]);
									p = description.split("-")[1];
									
								}
								catch (Exception e) {
									p = "0";
								}
								//aggiorno la massima priorita' solo se sono 
								if (Integer.valueOf(p) > priority) {
									priority = Integer.valueOf(p);
								}
							}
							
						}
					}
					
				}
				
			}
			
		}
		catch (Exception e) {
			LOGGER.error("Error reading triggers", e);
		}
		return priority;
	}
}
