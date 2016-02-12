package eu.sinergis.sunshine.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import eu.sinergis.sunshine.obj.AccessTable;
import eu.sinergis.sunshine.obj.AccessTables;
import eu.sinergis.sunshine.utils.HibernateUtil;

@Path("/commands")
public class SetService {
	
	static final Logger LOGGER = Logger.getLogger(SetService.class);
	
	public static final String COMMAND_DIMMING = "DIMMING";
	
	/**
	 * @param spsId
	 * @return
	 */
	@GET
	@Path("/{spsId}")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response readAccessTableBySpsId(@PathParam("spsId") String spsId) {
		LOGGER.info("read access table by spsId " + spsId);
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(AccessTable.class);
			Object result = criteria.add(Restrictions.idEq(new Integer(spsId))).uniqueResult();
			
			if (result == null || result.equals("")) {
				return Response.status(Response.Status.NO_CONTENT).build();
			}
			return Response.ok(result).build();
		}
		catch (Exception e) {
			LOGGER.info("Reading access table error ", e);
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		finally {
			session.close();
		}
	}
	
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response readAccessTable(@QueryParam("CTRLID") String ctrlId) {
		LOGGER.info("read access table");
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			Criteria criteria = session.createCriteria(AccessTable.class);
			AccessTables listAccessTable = new AccessTables();
			
			if (ctrlId != null && !ctrlId.equals("")) {
				List<AccessTable> list = criteria.add(Restrictions.eq("sps_cb_sn", ctrlId)).list();
				if (list != null && list.isEmpty()) {
					return Response.status(Response.Status.NO_CONTENT).build();
				}
				else {
					listAccessTable.setListAccessTable(list);
				}
			}
			else {
				if (criteria.list() != null && criteria.list().isEmpty()) {
					return Response.status(Response.Status.NO_CONTENT).build();
				}
				listAccessTable.setListAccessTable(criteria.list());
			}
			
			return Response.ok(listAccessTable).build();
		}
		catch (Exception e) {
			LOGGER.info("Reading access table error ", e);
			session.getTransaction().rollback();
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		finally {
			session.close();
		}
	}
	
	/**
	 * insert into access table the xml parameters
	 * 
	 * @param accessTable
	 * @return the element and 200 if the insert is ok, 400 otherwise
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML })
	public Response insert(JAXBElement<AccessTable> accessTable) {
		LOGGER.info("insert into access table");
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			AccessTable xmlInput = accessTable.getValue();
			
			Response response = checkInput(xmlInput);
			if (response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
				return response;
			}
			
			session.beginTransaction();
			
			AccessTable acess = new AccessTable();
			
			//restituisce l'ultimo id inserito
			Integer newSpsID = getSpsId() + 1;
			acess.setSps_id(newSpsID);
			LOGGER.debug("setSps_id " + newSpsID);
			
			acess.setSps_dest(xmlInput.getSps_dest());
			LOGGER.debug("setSps_dest " + xmlInput.getSps_dest());
			
			acess.setSps_command(xmlInput.getSps_command());
			LOGGER.debug("setSps_command " + xmlInput.getSps_command());
			
			acess.setSps_cb_sn(xmlInput.getSps_cb_sn());
			LOGGER.debug("setSps_cb_sn " + xmlInput.getSps_cb_sn());
			
			acess.setSps_dest_param(xmlInput.getSps_dest_param());
			LOGGER.debug("setSps_dest_param " + xmlInput.getSps_dest_param());
			
			acess.setSps_param1(xmlInput.getSps_param1());
			LOGGER.debug("setSps_param1 " + xmlInput.getSps_param1());
			
			acess.setSps_param2(xmlInput.getSps_param2());
			LOGGER.debug("setSps_param2 " + xmlInput.getSps_param2());
			
			session.save(acess);
			session.getTransaction().commit();
			
			return Response.ok(acess).build();
		}
		catch (Exception e) {
			LOGGER.info("Error in insert", e);
			session.getTransaction().rollback();
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}
	
	/**
	 * controllo degli input
	 * 
	 * @param xmlInput
	 */
	private Response checkInput(AccessTable xmlInput) {
		
		//controllo che il dimming sia compreso tra 1 e 100 e che sia diverso da null nel caso il commando sia DIMMING
		String dimmingStr = xmlInput.getSps_param1();
		if (xmlInput.getSps_command() != null && xmlInput.getSps_command().equalsIgnoreCase(COMMAND_DIMMING)) {
			if (dimmingStr != null && !dimmingStr.equals("")) {
				try {
					Integer dimming = Integer.parseInt(dimmingStr);
					if (dimming <= 0 || dimming > 100) {
						LOGGER.error("il dimming deve essere compresa tra 0 e 100 " + dimmingStr);
						return Response.status(Response.Status.BAD_REQUEST).header("error", dimming + " not conform")
								.build();
					}
				}
				catch (Exception e) {
					LOGGER.error("Checking input error, dimming is not a integer " + dimmingStr, e);
					return Response.status(Response.Status.BAD_REQUEST)
							.header("error", "dimming " + dimmingStr + " is not valid").build();
				}
			}
			else {
				LOGGER.error("dimming is null or empty " + dimmingStr);
				return Response.status(Response.Status.BAD_REQUEST)
						.header("error", "dimming " + dimmingStr + " not conform").build();
			}
		}
		return Response.ok().build();
	}
	
	/**
	 * get the last sps id
	 * 
	 * @return sps id
	 */
	private Integer getSpsId() {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Criteria criteria = session.createCriteria(AccessTable.class).setProjection(Projections.max("id"));
		Integer spsId = (Integer) criteria.uniqueResult();
		if (spsId == null) {
			spsId = 0;
		}
		return spsId;
	}
}
