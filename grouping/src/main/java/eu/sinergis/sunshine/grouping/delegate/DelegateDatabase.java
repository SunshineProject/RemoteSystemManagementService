package eu.sinergis.sunshine.grouping.delegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.quartz.SchedulerException;

import eu.sinergis.sunshine.grouping.pojo.ObjGroupComposition;
import eu.sinergis.sunshine.grouping.pojo.ObjLampCtrl;
import eu.sinergis.sunshine.grouping.pojosps.DataAccessTypes;
import eu.sinergis.sunshine.grouping.pojosps.DataResultAccess;
import eu.sinergis.sunshine.grouping.pojosps.SensorConfiguration;
import eu.sinergis.sunshine.grouping.pojosps.SensorDescription;
import eu.sinergis.sunshine.grouping.pojosps.SensorOfferings;
import eu.sinergis.sunshine.grouping.utils.HibernateUtil;

public class DelegateDatabase {
	static final Logger LOGGER = Logger.getLogger(DelegateDatabase.class);
	
	public final Serializable save(Object table, Transaction tx, Session session) {
		Serializable tableId = checkExitsRecord(table, tx, session);
		if (tableId == null) {
			
			try {
				if (tx == null) {
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					tx = session.beginTransaction();
				}
				tableId = session.save(table);
				//				if (!tx.wasCommitted()) {
				//					
				//					tx.commit();
				//				}
				//session.close();
			}
			catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				LOGGER.error(e);
				return null;
			}
		}
		return tableId;
	}
	
	public final Serializable saveAll(Object table, Transaction tx, Session session) {
		Serializable tableId = null;
		if (tableId == null) {
			
			try {
				if (tx == null) {
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					tx = session.beginTransaction();
				}
				tableId = session.save(table);
				
			}
			catch (HibernateException e) {
				if (tx != null)
					tx.rollback();
				LOGGER.error(e);
				return null;
			}
		}
		return tableId;
	}
	
	public final boolean checkExistsGroup(ObjLampCtrl device, Transaction tx, Session session) {
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("usrId", device.getUsrId()));
		List<ObjLampCtrl> result = criteria.list();
		return !result.isEmpty();
	}
	
	public final Serializable update(ObjLampCtrl table, Transaction tx, Session session) {
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				tx = session.beginTransaction();
			}
			session.saveOrUpdate(table);
			if (!tx.wasCommitted()) {
				
				tx.commit();
			}
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return null;
		}
		
		return table.getId();
	}
	
	public final boolean delete(ObjLampCtrl record) {
		Transaction tx = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			Criteria criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("groupId", record.getId()));
			List<?> result = criteria.list();
			//prima devo eliminare i record referenziati
			List<ObjGroupComposition> resultComposition = (List<ObjGroupComposition>) result;
			for (ObjGroupComposition elem : resultComposition) {
				session.delete(elem);
			}
			String spsId = record.getSpsId();
			session.delete(record);
			if (!deleteSpsElement(spsId)) {
				tx.rollback();
				return false;
			}
			if (!tx.wasCommitted()) {
				
				tx.commit();
			}
			//session.close();
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return false;
		}
		return true;
	}
	
	public final List<Boolean> deleteTask(String spsId, String taskid, String codespace) {
		DelegateSps sps = new DelegateSps();
		try {
			return sps.deleteTask(spsId, taskid, codespace);
		}
		catch (SchedulerException e) {
			LOGGER.error("Trigger or Job in Quartz not found", e);
			return null;
		}
	}
	
	public final boolean deleteToUpdate(ObjLampCtrl record, Transaction tx, Session session) {
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				tx = session.beginTransaction();
			}
			Criteria criteria = session.createCriteria(ObjLampCtrl.class);
			criteria.add(Restrictions.eq("spsId", record.getSpsId()));
			List<?> result = criteria.list();
			List<ObjLampCtrl> resultObjLamp = (List<ObjLampCtrl>) result;
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("groupId", resultObjLamp.get(0).getId()));
			List<?> resultGroup = criteria.list();
			//prima devo eliminare i record referenziati
			List<ObjGroupComposition> resultComposition = (List<ObjGroupComposition>) resultGroup;
			for (ObjGroupComposition elem : resultComposition) {
				session.delete(elem);
			}
			session.delete(record);
		}
		catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return false;
		}
		return true;
	}
	
	private final boolean deleteSpsElement(String idSps) {
		Session session = HibernateUtil.getSessionFactorySps().getCurrentSession();
		Transaction tx = session.beginTransaction();
		try {
			Criteria criteria = session.createCriteria(DataResultAccess.class);
			criteria.add(Restrictions.eq("identifier", idSps));
			List<?> result = criteria.list();
			List<DataResultAccess> dataResultAccessList = (List<DataResultAccess>) result;
			if (dataResultAccessList.isEmpty()) {
				//restituisco false perche' nonho trovato il record in tabella
				return true;
			}
			criteria = session.createCriteria(DataAccessTypes.class);
			criteria.add(Restrictions.eq("id", dataResultAccessList.get(0).getId()));
			List<?> resultAccessType = criteria.list();
			List<DataAccessTypes> dataAccessTypeList = (List<DataAccessTypes>) resultAccessType;
			for (DataAccessTypes elem : dataAccessTypeList) {
				session.delete(elem);
			}
			//cancellazione sensorConfiguration
			criteria = session.createCriteria(SensorConfiguration.class);
			criteria.add(Restrictions.eq("procedureId", idSps));
			List<?> resultSensor = criteria.list();
			List<SensorConfiguration> sensorList = (List<SensorConfiguration>) resultSensor;
			for (SensorConfiguration elem : sensorList) {
				session.delete(elem);
			}
			session.delete(dataResultAccessList.get(0));
			//ora ricerca in sensorDescription
			criteria = session.createCriteria(SensorDescription.class);
			criteria.add(Restrictions.eq("procedureId", idSps));
			List<?> resultSensorDesc = criteria.list();
			List<SensorDescription> sensorListDesc = (List<SensorDescription>) resultSensorDesc;
			for (SensorDescription elem : sensorListDesc) {
				//ora ricerca in sensorOfferings
				criteria = session.createCriteria(SensorOfferings.class);
				criteria.add(Restrictions.eq("id", idSps)).add(Restrictions.eq("sensorDescriptionId", elem.getId()));
				List<?> resultSensorOff = criteria.list();
				List<SensorOfferings> sensorListOff = (List<SensorOfferings>) resultSensorOff;
				if (!sensorListOff.isEmpty()) {
					//prendo quello di indice 0 perche' la coppia id+sensorDescriptionId e' chiave della tabella
					session.delete(sensorListOff.get(0));
				}
				session.delete(elem);
			}
			
			if (!tx.wasCommitted()) {
				
				tx.commit();
			}
		}
		catch (Exception e) {
			if (tx != null)
				tx.rollback();
			LOGGER.error(e);
			return false;
		}
		return true;
	}
	
	/**
	 * verifico con il ctrlId perche' una lampada deve essere provvista del ctrlId e lo stesso vale per un gruppo quadro
	 * fisico
	 * 
	 * @param ctrlId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final int getKeyOfLampCtrl(String ctrlId, Transaction tx, Session session, String codespace) {
		if (tx == null) {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
		}
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("ctrlId", ctrlId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		//		session.close();
		List<ObjLampCtrl> resultComposition = (List<ObjLampCtrl>) result;
		if (resultComposition.isEmpty()) {
			return -1;
		}
		else {
			return resultComposition.get(0).getId();
		}
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrlFromSosId(String sosId, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("sosId", sosId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrlFromSpsId(String spsId, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("spsId", spsId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjGroupComposition> getGroupsFromSpsId(String spsId, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("spsId", spsId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		List<ObjGroupComposition> listaComposizione = new ArrayList<ObjGroupComposition>();
		//ciclo sulla lista anche se l'spsId dovrebbe essere unico
		for (ObjLampCtrl elem : resultCtrl) {
			//per ogni elemento di questa lista...
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("lampId", elem.getId()));
			listaComposizione.addAll(criteria.list());
		}
		//ora ho la lista dei gruppi che hanno come figlio la lampada con spsId
		session.close();
		return listaComposizione;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjGroupComposition> getGroupsFromSpsId(String spsId, String codespace, String type) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("spsId", spsId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		List<ObjGroupComposition> listaComposizione = new ArrayList<ObjGroupComposition>();
		//ciclo sulla lista anche se l'spsId dovrebbe essere unico
		for (ObjLampCtrl elem : resultCtrl) {
			//per ogni elemento di questa lista...
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("groupId", elem.getId()));
			listaComposizione.addAll(criteria.list());
		}
		//ora ho la lista dei gruppi che hanno come figlio la lampada con spsId
		session.close();
		return listaComposizione;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjGroupComposition> getGroupsFromSosId(String sosId, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("sosId", sosId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		List<ObjGroupComposition> listaComposizione = new ArrayList<ObjGroupComposition>();
		//ciclo sulla lista anche se l'spsId dovrebbe essere unico
		for (ObjLampCtrl elem : resultCtrl) {
			//per ogni elemento di questa lista...
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("lampId", elem.getId()));
			listaComposizione.addAll(criteria.list());
		}
		//ora ho la lista dei gruppi che hanno come figlio la lampada con spsId
		session.close();
		return listaComposizione;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjGroupComposition> getGroupsFromSosId(String sosId, String codespace, String type) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("sosId", sosId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		List<ObjGroupComposition> listaComposizione = new ArrayList<ObjGroupComposition>();
		//ciclo sulla lista anche se l'spsId dovrebbe essere unico
		for (ObjLampCtrl elem : resultCtrl) {
			//per ogni elemento di questa lista...
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("lampId", elem.getId()));
			listaComposizione.addAll(criteria.list());
		}
		//ora ho la lista dei gruppi che hanno come figlio la lampada con spsId
		session.close();
		return listaComposizione;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrlFromUsrId(String usrId, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("usrId", usrId)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrlFromUsrId(String usrId, String codespace, String type) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("usrId", usrId)).add(Restrictions.eq("codeSpace", codespace))
				.add(Restrictions.eq("typeGroup", type));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrlParentFromUsrId(String usrId, String codespace) {
		List<ObjLampCtrl> resultCtrl = null;
		try {
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			Criteria criteria = session.createCriteria(ObjLampCtrl.class);
			criteria.add(Restrictions.eq("usrId", usrId)).add(Restrictions.eq("codeSpace", codespace));
			List<?> result = criteria.list();
			resultCtrl = (List<ObjLampCtrl>) result;
			//ritorno la lista vuota cosi' so il motivo dell'errore
			if (resultCtrl.isEmpty()) {
				return resultCtrl;
			}
			criteria = session.createCriteria(ObjGroupComposition.class);
			criteria.add(Restrictions.eq("lampId", resultCtrl.get(0).getId()));
			result = criteria.list();
			List<ObjGroupComposition> resultGrp = (List<ObjGroupComposition>) result;
			criteria = session.createCriteria(ObjLampCtrl.class);
			criteria.add(Restrictions.eq("id", resultGrp.get(0).getGroupId()));
			result = criteria.list();
			resultCtrl = (List<ObjLampCtrl>) result;
			session.close();
		}
		catch (IndexOutOfBoundsException e) {
			LOGGER.info("Object in db not found!", e);
			return null;
		}
		catch (Exception e) {
			LOGGER.error("Generic error", e);
			return null;
		}
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrl(String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjLampCtrl> getObjLampCtrl(String codespace, String type) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class).add(Restrictions.eq("codeSpace", codespace))
				.add(Restrictions.eq("typeGroup", type));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final List<String> getCodeSpace() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class).setProjection(Projections.property("codeSpace"))
				.setProjection(Projections.groupProperty("codeSpace"));
		List<?> result = criteria.list();
		List<String> resultCtrl = (List<String>) result;
		session.close();
		return resultCtrl;
	}
	
	@SuppressWarnings("unchecked")
	public final ObjLampCtrl getObjLampCtrlFromID(int id, String codespace) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("id", id)).add(Restrictions.eq("codeSpace", codespace));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		if (resultCtrl.isEmpty()) {
			return null;
		}
		return resultCtrl.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public final ObjLampCtrl getObjLampCtrlFromID(int id, String codespace, String type) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.eq("id", id)).add(Restrictions.eq("codeSpace", codespace))
				.add(Restrictions.eq("typeGroup", type));
		List<?> result = criteria.list();
		List<ObjLampCtrl> resultCtrl = (List<ObjLampCtrl>) result;
		session.close();
		if (resultCtrl.isEmpty()) {
			return null;
		}
		return resultCtrl.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public final List<ObjGroupComposition> getObjGroupCompositionFromLampCtrlListId(int id) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjGroupComposition.class);
		criteria.add(Restrictions.eq("groupId", id));
		List<?> result = criteria.list();
		List<ObjGroupComposition> resultGComposition = (List<ObjGroupComposition>) result;
		session.close();
		return resultGComposition;
	}
	
	@SuppressWarnings("unchecked")
	private final Serializable checkExitsRecord(Object table, Transaction tx, Session session) {
		try {
			ObjLampCtrl lampControl = (ObjLampCtrl) table;
			if (tx == null) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				tx = session.beginTransaction();
			}
			
			Criteria criteria = session.createCriteria(ObjLampCtrl.class);
			criteria.add(Restrictions.eq("usrId", lampControl.getUsrId()));
			List<?> result = criteria.list();
			List<ObjLampCtrl> resultComposition = (List<ObjLampCtrl>) result;
			//session.close();
			if (!resultComposition.isEmpty()) {
				return resultComposition.get(0).getId();
			}
			else {
				return null;
			}
		}
		catch (Exception e) {
			LOGGER.info("Object conversion to LampControl not appropriate", e);
			try {
				ObjGroupComposition grpComp = (ObjGroupComposition) table;
				if (tx == null) {
					session = HibernateUtil.getSessionFactory().getCurrentSession();
					tx = session.beginTransaction();
				}
				if (!session.isOpen()) {
					session.getSessionFactory().openSession();
				}
				Criteria criteria = session.createCriteria(ObjGroupComposition.class);
				criteria.add(Restrictions.eq("groupId", grpComp.getGroupId())).add(
						Restrictions.eq("lampId", grpComp.getLampId()));
				List<?> result = criteria.list();
				List<ObjGroupComposition> resultComposition = (List<ObjGroupComposition>) result;
				//session.close();
				if (!resultComposition.isEmpty()) {
					return resultComposition.get(0).getGroupId();
				}
				else {
					return null;
				}
				
			}
			catch (Exception ee) {
				LOGGER.info("Invalid cast", ee);
				return null;
			}
		}
	}
	
}
