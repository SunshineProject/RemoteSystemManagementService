package org.n52.sps.sensor.cite.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.n52.sps.sensor.cite.pojo.Command;
import org.n52.sps.sensor.model.SensorTask;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuartzUtil.class);
	
	public void resumeJob() throws SchedulerException {
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		LOGGER.info("Resuming Quartz's job");
		for (String groupName : scheduler.getJobGroupNames()) {
			
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();
				
				//get job's trigger
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				Date nextFireTime = triggers.get(0).getNextFireTime();
				LOGGER.info("[jobName] : " + jobName + " [groupName] : " + jobGroup + " - " + nextFireTime);
				
			}
			
		}
		scheduler.start();
	}
	
	public Trigger createCompleteTrigger(SensorTask sensorTask, String taskId, Command cmd) {
		String activationHour = cmd.getActivationHour();
		Date startDate = cmd.getDateAttivation();
		int minute = -1;
		int hour = -1;
		try {
			minute = Integer.parseInt(activationHour.substring(2, 4));
			hour = Integer.parseInt(activationHour.substring(0, 2));
		}
		catch (Exception e) {
			LOGGER.info("activationHour not present");
		}
		if (minute != -1 && hour != -1) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(cmd.getDateAttivation());
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			startDate = cal.getTime();
		}
		
		if (startDate.before(Functions.addPeriod(Functions.getMiddleDay(), -1, Calendar.MINUTE))) {
			startDate = Functions.addPeriod(startDate, 24, Calendar.HOUR_OF_DAY);
		}
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(sensorTask.getProcedure(), taskId)
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(cmd.getFrequency()).repeatForever())
				/*
				 * .withSchedule(
				 * CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(cmd.getFrequency(),
				 * IntervalUnit.HOUR))
				 */
				.startAt(startDate)
				.endAt(cmd.getDateToExpiry())
				.withDescription(
						String.valueOf(cmd.getDimmer()) + "-" + String.valueOf(cmd.getPriority()) + "-"
								+ cmd.getDayOfWeek()).build();
		return trigger;
	}
	
	public Trigger createNotCompleteTrigger(SensorTask sensorTask, String taskId, Command cmd) {
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(sensorTask.getProcedure(), taskId)
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(cmd.getFrequency()).repeatForever())
				/*
				 * .withSchedule(
				 * CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(cmd.getFrequency(),
				 * IntervalUnit.HOUR))
				 */
				.startAt(cmd.getDateAttivation())
				.withDescription(
						String.valueOf(cmd.getDimmer()) + "-" + String.valueOf(cmd.getPriority()) + "-"
								+ cmd.getDayOfWeek()).build();
		return trigger;
	}
	
	public Trigger createOneShotTrigger(SensorTask sensorTask, String taskId, Command cmd) {
		Date expire = DateUtils.addMinutes(cmd.getDateAttivation(), 1);
		//Trigger trigger = TriggerBuilder.newTrigger().startAt(cmd.getDateAttivation()).endAt(expire).build();
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(sensorTask.getProcedure(), taskId)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0))
				.startNow()
				.withDescription(
						String.valueOf(cmd.getDimmer()) + "-" + String.valueOf(cmd.getPriority()) + "-"
								+ cmd.getDayOfWeek()).build();
		return trigger;
	}
	
	public Trigger createNotCronTrigger(SensorTask sensorTask, String taskId, Command cmd) {
		String activationHour = cmd.getActivationHour();
		
		int second = 0;//prepare from the time selected from UI(fire time)
		int minute = Integer.parseInt(activationHour.substring(2, 4));
		int hour = Integer.parseInt(activationHour.substring(0, 2));
		String dayOfWeek = Functions.getCronStringFromDays(cmd.getDayOfWeek());//"1,3";//prepare it from the days you get from UI(give check box values as 1 for SUN,....)
		
		String cronExpression = String.format("%d %d %d ? * %s", second, minute, hour, dayOfWeek);
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(sensorTask.getProcedure(), taskId)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.startAt(cmd.getDateAttivation())
				.endAt(cmd.getDateToExpiry())
				.withDescription(
						String.valueOf(cmd.getDimmer()) + "-" + String.valueOf(cmd.getPriority()) + "-"
								+ cmd.getDayOfWeek()).build();
		
		return trigger;
	}
	
	public ArrayList<Trigger> createListTrigger(SensorTask sensorTask, String taskId, Command cmd) {
		String[] dates = cmd.getListOfDays().split(";");
		ArrayList<Trigger> listTrigger = new ArrayList<Trigger>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		Calendar cal = Calendar.getInstance();
		
		for (String d : dates) {
			try {
				Date tmpDate = formatter.parse(d);
				cal.setTime(tmpDate);
				cal.add(Calendar.MINUTE, 2);
				listTrigger.add(TriggerBuilder.newTrigger().withIdentity(sensorTask.getProcedure(), taskId)
						.withSchedule(SimpleScheduleBuilder.simpleSchedule().withRepeatCount(0)).startAt(tmpDate)
						.endAt(cal.getTime()).build());
			}
			catch (ParseException e) {
				LOGGER.error("Date malformed");
			}
			//Date expire = DateUtils.addMinutes(cmd.getDateAttivation(), 1);
			//Trigger trigger = TriggerBuilder.newTrigger().startAt(cmd.getDateAttivation()).endAt(expire).build();
		}
		return listTrigger;
	}
	
	public void verifyPriorityTrigger() {
		//		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		//		List<Trigger> triggers = new ArrayList<Trigger>();
		//		for (String groupName : scheduler.getJobGroupNames()) {
		//			
		//			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
		//				
		//				String jobName = jobKey.getName();
		//				String jobGroup = jobKey.getGroup();
		//				
		//				//get job's trigger
		//				if (jobName.equalsIgnoreCase(spsId)) {
		//					List<Trigger> listJob = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
		//					for (Trigger trig : listJob) {
		//						triggers.add(trig);
		//					}
		//					break;
		//				}
		//			}
		//			
		//		}
	}
	
}
