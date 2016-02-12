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

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.sps.sensor.SensorTaskService;
import org.n52.sps.sensor.model.SensorTask;
import org.n52.sps.tasking.TaskingRequestStatus;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CiteTaskScheduler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CiteTaskScheduler.class);
	
	private static final int MAXIMUM_RUNS_WITHOUT_BATTERY_RECHARGE = 2;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private CiteTaskSimulation scheduledSimulation;
	
	private int countOfExecutedTasks = 0;
	
	private CiteTaskSimulation rechargeSimulation;
	
	public CiteTaskScheduler(SensorTaskService sensorTaskService) {
		createRechargeSimulation(sensorTaskService);
	}
	
	private void createRechargeSimulation(SensorTaskService sensorTaskService) {
		SensorTask rechargeTask = sensorTaskService.createNewTask();
		rechargeTask.setRequestStatus(TaskingRequestStatus.ACCEPTED); // internal task
		addRechargeStatusMessages(rechargeTask);
		rechargeSimulation = CiteTaskSimulation.createInternalTaskSimulation(rechargeTask);
		rechargeSimulation.setSensorTaskService(sensorTaskService);
	}
	
	public boolean schedule(CiteTaskSimulation citeTaskSimulation) throws SchedulerException {
		SensorTask sensorTask = citeTaskSimulation.getSensorTask();
		citeTaskSimulation.setRechargeAfterTaskExecutionsSimulation(this, rechargeSimulation);
		boolean schedulingPossible = isSchedulingPossible();
		//Qui devo verificare sul db se è stato scritto completed e 100% e metto in OR
		TriggerKey keyHumanTrigger = new TriggerKey(sensorTask.getProcedure(), "ROV");
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		Trigger trigger = scheduler.getTrigger(keyHumanTrigger);
		boolean expired = true;
		//richiamare una funzione che listi tutti i job in quartz per un determinata procedure
		//di questi job, scorrere i trigger e verificare se ve n'è uno 
		//oppure questo controllo demandarlo a livello di TaskSimulation dove si hanno a disposizione anche le date e comando..quindi se è uguale si esce
		if (trigger != null) {
			if ((new Date()).before((trigger.getEndTime()))) {
				expired = false;
			}
		}
		if (/* schedulingPossible || */expired) {
			scheduledSimulation = citeTaskSimulation;
			executorService.execute(citeTaskSimulation);
			LOGGER.info("Executing task: {}", sensorTask);
			countOfExecutedTasks++;
		}
		else {
			if (isRechargingBatteries()) {
				addRechargeStatusMessages(sensorTask);
			}
			else {
				sensorTask.addStatusMessage("Sensor is currently in execution.");
			}
		}
		return schedulingPossible;
	}
	
	private void addRechargeStatusMessages(SensorTask sensorTask) {
		sensorTask.addStatusMessage("The Sensor you have called is temporarily not available!");
		sensorTask.addStatusMessage("Sensor is loading batteries at base station. Please try again later.");
	}
	
	private boolean isSchedulingPossible() {
		return scheduledSimulation == null || !scheduledSimulation.isTaskExecuting();
	}
	
	public void cancel(String taskId) {
		// TODO cancel running simulation in case of a task with higher priority 
	}
	
	public void resetBatteryStatus() {
		countOfExecutedTasks = 0;
	}
	
	public boolean mustRechargeBatteries() {
		return countOfExecutedTasks == MAXIMUM_RUNS_WITHOUT_BATTERY_RECHARGE;
	}
	
	public boolean isRechargingBatteries() {
		return rechargeSimulation != null && rechargeSimulation.isTaskExecuting();
	}
	
}
