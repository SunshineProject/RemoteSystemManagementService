package it.sinergis.sunshine.delegate;

import it.sinergis.sunshine.utils.HibernateUtil;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class Database {
	private static final Logger logger = Logger.getLogger(Database.class);
	
	/**
	 * metodo per l'esecuzione di query sul db
	 * 
	 * @param queryParam
	 * @return
	 */
	public final int insert(String queryParam) {
		logger.info("Insert");
		logger.info("Try to connect to database");
		int status = 400;
		try {
			
			//Class.forName("net.sourceforge.jtds.jdbc.Driver");
			
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			
			//Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
			
			SQLQuery query = session.createSQLQuery(queryParam);
			logger.info("Connection established");
			status = query.executeUpdate();
			//			session.close();
			session.getTransaction().commit();//l'operazione di commit chiude anche la connessione
			
		}
		catch (Exception e) {
			logger.error("Query execution interrupted", e);
		}
		return status;
	}
}
