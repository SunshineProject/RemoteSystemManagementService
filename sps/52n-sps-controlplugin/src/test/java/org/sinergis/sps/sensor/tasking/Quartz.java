package org.sinergis.sps.sensor.tasking;

import java.util.Date;

import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class Quartz implements Job {
	public void execute(JobExecutionContext cntxt) throws JobExecutionException {
		System.out.println("Generating report - " + cntxt.getJobDetail().getJobDataMap().get("type"));
		JobKey key = cntxt.getJobDetail().getKey();
		//TODO Generate report
		
		//to read data
		JobDataMap dataMap = cntxt.getJobDetail().getJobDataMap();
		String jobSays = dataMap.getString("jobSays");
		float myFloatValue = dataMap.getFloat("myFloatValue");
		
		System.err.println("Instance " + key + " of DumbJob says: " + jobSays + ", and val is: " + myFloatValue);
		
	}
	
	//	Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dummyTriggerName", "group1")
	//					.wsithSchedule(CronScheduleBuilder.cronSchedule("1 * * * * ?")).build();
	
	public static void main(String[] args) {
		try {
			//qui salvo i dati che verranno letti dalla classe Quartz in fase di esecuzione
			JobDetail job = JobBuilder.newJob(Quartz.class).withIdentity("ctrlid", "4546846")
					.usingJobData("DateStart", "20150101010101").usingJobData("DateEnd", "20150101010102")
					.usingJobData("freq", 2).usingJobData("dimmer", 40).build();
			
			Trigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity("test", "ROV")
					.withSchedule(
							CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(40,
									IntervalUnit.MINUTE)).startAt(new Date()).endAt(new Date()).build();
			
			//			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("dummyTriggerName", "group1")
			//					.withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
			//					.build();
			
			//schedule it
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
			scheduler.scheduleJob(job, trigger);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
