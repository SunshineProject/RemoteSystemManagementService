package org.n52.sps.sensor.cite.ServletContextListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.n52.sps.sensor.cite.utils.QuartzUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListenerQuartz implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ListenerQuartz.class);
	
	//da qui posso ricaricare tutti i job quartz presenti nel db
	
	public void contextInitialized(ServletContextEvent e) {
		//Call your function from the event object here
		QuartzUtil qUtil = new QuartzUtil();
		try {
			qUtil.resumeJob();
		}
		catch (SchedulerException e1) {
			LOGGER.error("Error during the resume operation", e1);
		}
	}
	
	public void contextDestroyed(ServletContextEvent e) {
		
	}
}
