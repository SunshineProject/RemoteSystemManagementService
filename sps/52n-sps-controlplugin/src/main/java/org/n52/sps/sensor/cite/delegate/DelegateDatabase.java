package org.n52.sps.sensor.cite.delegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.n52.sps.sensor.cite.pojo.ObjGroupComposition;
import org.n52.sps.sensor.cite.pojo.ObjLampCtrl;
import org.n52.sps.sensor.cite.utils.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegateDatabase {
	static final Logger LOGGER = LoggerFactory.getLogger(DelegateDatabase.class);
	
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
				LOGGER.error(e.getMessage());
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
				LOGGER.error(e.getMessage());
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
			LOGGER.error(e.getLocalizedMessage());
			return null;
		}
		
		return table.getId();
	}
	
	public final boolean deleteToUpdate(ObjLampCtrl record, Transaction tx, Session session) {
		try {
			if (tx == null) {
				session = HibernateUtil.getSessionFactory().getCurrentSession();
				tx = session.beginTransaction();
			}
			Criteria criteria = session.createCriteria(ObjLampCtrl.class);
			criteria.add(Restrictions.eq("sosId", record.getSosId()));
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
			LOGGER.error(e.getLocalizedMessage());
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
			criteria.add(Restrictions.eq("lampId", elem.getId()));
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
