package grouping;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import eu.sinergis.sunshine.grouping.delegate.DelegateSps;
import eu.sinergis.sunshine.grouping.pojo.ObjLampCtrl;
import eu.sinergis.sunshine.grouping.utils.HibernateUtil;

public class TestAddSps {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//		DelegateSps sps = new DelegateSps();
		//		ObjLampCtrl table = new ObjLampCtrl();
		//		table.setSosId("www.dasdsa/dasds/adasda_SOS2.it");
		//		table.setSpsId("www.dasdsa/dasds/adasda_SPS.it");
		//		String r = sps.syncronizeSps(table);
		//		
		//		System.out.println(r);
		
		List<ObjLampCtrl> lista = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		Criteria criteria = session.createCriteria(ObjLampCtrl.class);
		criteria.add(Restrictions.ne("typeGroup", "V"))
		/*
		 * .add(Restrictions.not(Restrictions.like("sosId", "http://www.sunshineproject.eu/swe/procedure/ROV-225",
		 * MatchMode.START)))un
		 */.add(Restrictions.eq("codeSpace", "ROV")).add(Restrictions.ne("typeGroup", "Q"))
				.add(Restrictions.like("sosId", "http://www.sunshineproject.eu/swe/procedure/ROV-4", MatchMode.START));
		//		criteria.add(Restrictions.ne("typeGroup", "S")).add(Restrictions.ne("typeGroup", "Q"))
		//				.add(Restrictions.ne("codeSpace", "BAS"));
		List<?> result = criteria.list();
		//		session.close("");to
		List<ObjLampCtrl> resultComposition = (List<ObjLampCtrl>) result;
		session.close();
		DelegateSps dbSps = new DelegateSps();
		for (ObjLampCtrl device : resultComposition) {
			dbSps.syncronizeSps(device);
		}
	}
}
