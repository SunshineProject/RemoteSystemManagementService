package eu.sinergis.sunshine.grouping.delegate;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import eu.sinergis.sunshine.grouping.pojo.ObjGroupComposition;

public class DelegateXml {
	static final Logger LOGGER = Logger.getLogger(DelegateXml.class);
	
	public final List<ObjGroupComposition> getObjGroupComposition(List<?> devices, int groupId, Transaction tx,
			Session session, String codespace) {
		LOGGER.info("Set up to insert group wirh id " + groupId);
		List<ObjGroupComposition> groupRecords = new ArrayList<ObjGroupComposition>();
		DelegateDatabase db = new DelegateDatabase();
		try {
			List<String> listStringLamp = (List<String>) devices;
			for (String lamp : listStringLamp) {
				int idLamp = db.getKeyOfLampCtrl(lamp, tx, session, codespace);//TODO qui recupero l'id
				if (idLamp != -1) {
					ObjGroupComposition record = new ObjGroupComposition();
					record.setGroupId(groupId);
					record.setLampId(idLamp);
					groupRecords.add(record);
				}
			}
		}
		catch (Exception e) {
			List<Integer> listIntegerLamp = (List<Integer>) devices;
			for (Integer lampId : listIntegerLamp) {
				ObjGroupComposition record = new ObjGroupComposition();
				record.setGroupId(groupId);
				record.setLampId(lampId);
				groupRecords.add(record);
			}
		}
		
		return groupRecords;
	}
}
