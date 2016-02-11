package eu.sinergis.sunshine.grouping.delegate;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import eu.sinergis.sunshine.grouping.pojo.ObjLampCtrl;
import eu.sinergis.sunshine.grouping.utils.Functions;
import eu.sinergis.sunshine.grouping.utils.Httprequest;
import eu.sinergis.sunshine.grouping.utils.ReadFromConfig;

public class DelegateSps {
	static final Logger LOGGER = Logger.getLogger(DelegateSps.class);
	
	public String syncronizeSps(Object table) {
		ObjLampCtrl lamp = (ObjLampCtrl) table;
		Httprequest http = new Httprequest();
		List<String> ss = new ArrayList<String>();
		ss.add(ReadFromConfig.loadByName("point1"));
		ss.add(ReadFromConfig.loadByName("point2"));
		ss.add(ReadFromConfig.loadByName("point3"));
		ss.add(ReadFromConfig.loadByName("point4"));
		String describeSensor = null;
		String sosIdDevice = lamp.getSosId();
		try {
			describeSensor = ReadFromConfig.loadByName("endPointSosInSps") + URLEncoder.encode(lamp.getSosId()) + "&"
					+ ReadFromConfig.loadByName("portionSosInSps");
		}
		catch (Exception e) {
			describeSensor = ReadFromConfig.loadByName("ednPointSos");
			sosIdDevice = describeSensor;
		}
		String result = http.postInsertSensor(
				Functions.addUrlSection(ReadFromConfig.loadByName("spsUri"), "admin/insert"), "offreing",
				"offeringDescription", ss, lamp.getSpsId(), ReadFromConfig.loadByName("pluginType"), sosIdDevice,
				ReadFromConfig.loadByName("abstractPluginLamp"), "http://sunshine.sinergis.it/SOS40/",
				"PluginTaskIdentifier");
		LOGGER.info(result);
		return result;
	}
	
	public final List<Boolean> deleteTask(String spsId, String taskid, String codespace) throws SchedulerException {
		List<Boolean> listResponse = new ArrayList<Boolean>();
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		//		TriggerKey keyHumanTrigger = new TriggerKey(spsId, codespace);
		//		listResponse.add(scheduler.unscheduleJob(keyHumanTrigger));
		JobKey keyFromHuman = new JobKey(spsId, taskid);
		listResponse.add(scheduler.deleteJob(keyFromHuman));
		
		return listResponse;
		
	}
	
	public final List<Trigger> getTaskFromQuartz(String spsId) throws SchedulerException {
		LOGGER.info("In getTaskFromQuartz");
		
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		
		List<Trigger> triggers = new ArrayList<Trigger>();
		for (String groupName : scheduler.getJobGroupNames()) {
			
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();
				
				//get job's trigger
				if (jobName.equalsIgnoreCase(spsId)) {
					List<Trigger> listJob = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
					for (Trigger trig : listJob) {
						triggers.add(trig);
					}
					break;
				}
			}
			
		}
		return triggers;
		
	}
	
}
