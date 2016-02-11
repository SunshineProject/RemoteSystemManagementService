package grouping;

import java.util.ArrayList;
import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class Testgeneral {
	
	public static void main(String[] args) throws SchedulerException {
		// TODO Auto-generated method stub
		
		//		DelegateSps sps = new DelegateSps();
		//		ObjLampCtrl table = new ObjLampCtrl();
		//		table.setSosId("www.dasdsa/dasds/adasda_SOS2.it");
		//		table.setSpsId("www.dasdsa/dasds/adasda_SPS.it");
		//		String r = sps.syncronizeSps(table);
		//		
		//		System.out.println(r);
		
		//		DelegateSps sps = new DelegateSps();
		//		sps.getTaskFromQuartz("http://www.sunshineproject.eu/sps/procedure/ROV-111");
		
		List<Boolean> listResponse = new ArrayList<Boolean>();
		Scheduler scheduler = new StdSchedulerFactory().getScheduler();
		//		TriggerKey keyHumanTrigger = new TriggerKey("http://www.sunshineproject.eu/sps/procedure/ROV-225", codespace);
		//		listResponse.add(scheduler.unscheduleJob(keyHumanTrigger));
		JobKey keyFromHuman = new JobKey("http://www.sunshineproject.eu/sps/procedure/ROV-225",
				"http://www.sunshineproject.eu/sps/procedure/ROV-225/01d8eb23-49c0-4353-99da-9a4fecd2bf43");
		boolean response = scheduler.deleteJob(keyFromHuman);
		
		listResponse.add(scheduler.deleteJob(keyFromHuman));
		
	}
}
