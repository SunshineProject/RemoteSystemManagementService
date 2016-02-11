package eu.sinergis.sunshine.grouping.rest;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import eu.sinergis.sunshine.grouping.delegate.DelegateDatabase;
import eu.sinergis.sunshine.grouping.delegate.DelegateSps;
import eu.sinergis.sunshine.grouping.delegate.DelegateXml;
import eu.sinergis.sunshine.grouping.pojo.ObjGroupComposition;
import eu.sinergis.sunshine.grouping.pojo.ObjLampCtrl;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlCodespace;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlGroup;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlPayloadTask;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlPayloadTaskDimming;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlToInsertGroup;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlToInsertLamp;
import eu.sinergis.sunshine.grouping.pojo.ObjXmlToInsertLampParent;
import eu.sinergis.sunshine.grouping.utils.Functions;
import eu.sinergis.sunshine.grouping.utils.HibernateUtil;
import eu.sinergis.sunshine.grouping.utils.Validazione;

// la versione va cambiata anche in manageDevices
@Path(value = "v2")
public class ManageGroups {
	static final Logger LOGGER = Logger.getLogger(ManageGroups.class);
	
	//	GET METHOD
	
	/**
	 * @param codespace
	 * @param sid
	 * @return
	 */
	@GET
	@Path(value = "css/{CODESPACE}/groups/{ID}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getGroupCompositionFromId(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid) {
		LOGGER.info("In getGroupComposition");
		//ottengo true se la validazione trova un oggetto non valido
		int id = 0;
		try {
			id = Integer.parseInt(sid);
		}
		catch (Exception e) {
			LOGGER.info("id " + id + "incorrect!", e);
			return Response.status(400).header("error", "id not a integer").build();
		}
		DelegateDatabase db = new DelegateDatabase();
		ObjXmlToInsertLamp xml = new ObjXmlToInsertLamp();
		List<ObjXmlToInsertLamp> listObjXmlToInsertLamp = new ArrayList<ObjXmlToInsertLamp>();
		
		ObjLampCtrl lampCtrList = db.getObjLampCtrlFromID(id, codespace, "V");
		if (lampCtrList == null) {
			return Response.status(404).header("error", "group not found").build();
		}
		//se e' un gruppo singolo non do la composizione
		if (!lampCtrList.getTypeGroup().equalsIgnoreCase("S")) {
			List<ObjGroupComposition> composition = db.getObjGroupCompositionFromLampCtrlListId(lampCtrList.getId());
			List<ObjLampCtrl> xmlLamps = new ArrayList<ObjLampCtrl>();
			for (ObjGroupComposition comp : composition) {
				xmlLamps.add(db.getObjLampCtrlFromID(comp.getLampId(), codespace));
			}
			//			ObjXmlGroup xmlG = new ObjXmlGroup();
			//			xmlG.setLamps(xmlLamps);
			xml.setLamps(xmlLamps);
			//			xml.setGroupComposition(xmlG);
		}
		
		xml.setTableLampCtrl(lampCtrList);
		listObjXmlToInsertLamp.add(xml);
		
		//		return Response.ok(listObjXmlToInsertLamp).contentLocation(new URI("/user-management/users/123")).build();
		ObjXmlToInsertLampParent parent = new ObjXmlToInsertLampParent();
		parent.setObjXmlInserLamp(listObjXmlToInsertLamp);
		return Response.ok(xml).build();
	}
	
	/**
	 * @param codespace
	 * @param usrId
	 * @return
	 */
	@GET
	@Path(value = "css/{CODESPACE}/groups")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getGroupComposition(@PathParam("CODESPACE") String codespace, @QueryParam("usrid") String usrId) {
		LOGGER.info("In getGroupComposition");
		//ottengo true se la validazione trova un oggetto non valido
		if (usrId != null && Validazione.validationString(usrId)) {
			return null;
		}
		ObjXmlToInsertLampParent parent = new ObjXmlToInsertLampParent();
		DelegateDatabase db = new DelegateDatabase();
		ObjXmlToInsertLamp xml = new ObjXmlToInsertLamp();
		List<ObjXmlToInsertLamp> listObjXmlToInsertLamp = new ArrayList<ObjXmlToInsertLamp>();
		if (usrId != null) {
			List<ObjLampCtrl> lampCtrList = db.getObjLampCtrlFromUsrId(usrId, codespace, "V");
			if (lampCtrList.isEmpty()) {
				return Response.status(404).header("error", "group not found").build();
			}
			List<ObjGroupComposition> composition = db.getObjGroupCompositionFromLampCtrlListId(lampCtrList.get(0)
					.getId());
			List<ObjLampCtrl> xmlComposition = new ArrayList<ObjLampCtrl>();
			for (ObjGroupComposition comp : composition) {
				xmlComposition.add(db.getObjLampCtrlFromID(comp.getLampId(), codespace));
			}
			//			ObjXmlGroup xmlG = new ObjXmlGroup();
			//			xmlG.setLamps(xmlComposition);
			//			xml.setGroupComposition(xmlG);
			xml.setLamps(xmlComposition);
			xml.setTableLampCtrl(lampCtrList.get(0));
			listObjXmlToInsertLamp.add(xml);
			return Response.ok(xml).build();
		}
		else {
			//			List<ObjXmlToInsertLampSimple> listObjXmlToinsertlampSimple = new ArrayList<ObjXmlToInsertLampSimple>();
			
			List<ObjLampCtrl> lampCtrList = db.getObjLampCtrl(codespace, "V");
			for (ObjLampCtrl lamp : lampCtrList) {
				xml = new ObjXmlToInsertLamp();
				xml.setTableLampCtrl(lamp);
				listObjXmlToInsertLamp.add(xml);
			}
			parent.setObjXmlInserLamp(listObjXmlToInsertLamp);
			//			return Response.ok(parent).build();
			return Response.ok(listObjXmlToInsertLamp).build();
		}
		
		//		return Response.ok(listObjXmlToInsertLamp).contentLocation(new URI("/user-management/users/123")).build();
		//		parent.setObjXmlInserLamp(listObjXmlToInsertLamp);
		//		return Response.ok(parent).build();
	}
	
	/**
	 * ricerca tramite spsId o sosId, mi da i gruppi di appartenenza di questa
	 * 
	 * @param codespace
	 * @param spsId
	 * @return
	 * @throws URISyntaxException
	 */
	@GET
	@Path(value = "css/{CODESPACE}/groups/search")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response searchFromSpsId(@PathParam("CODESPACE") String codespace, @QueryParam("spsid") String spsId,
			@QueryParam("sosid") String sosId) throws URISyntaxException {
		LOGGER.info("In getGroupComposition");
		boolean validSps = true;//metto a true xche' l'spsid può non essere un uri
		boolean validSos = false;
		//ottengo true se la validazione trova un oggetto non valido
		if (!Validazione.validationUri(spsId)) {
			validSps = true;
		}
		if (!Validazione.validationUri(sosId)) {
			validSos = true;
		}
		if (!validSps && !validSos) {
			return Response.status(400).build();
		}
		DelegateDatabase db = new DelegateDatabase();
		List<ObjGroupComposition> lampgrp = null;
		if (validSps) {
			
			lampgrp = db.getGroupsFromSpsId(spsId, codespace, "V");
			
		}
		else {
			lampgrp = db.getGroupsFromSosId(sosId, codespace, "V");
			
		}
		List<ObjLampCtrl> listGroups = new ArrayList<ObjLampCtrl>();
		if (lampgrp != null && !lampgrp.isEmpty()) {
			for (ObjGroupComposition elem : lampgrp) {
				listGroups.add(db.getObjLampCtrlFromID(elem.getGroupId(), codespace));
			}
			return Response.ok(listGroups).build();
		}
		if (lampgrp.isEmpty()) {
			return Response.status(400).header("error", "element not found").build();
		}
		return Response.status(500).header("error", "connection error").build();
		
	}
	
	/**
	 * @return
	 */
	@GET
	@Path(value = "css")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCodespace() {
		LOGGER.info("In getCodeSpace");
		DelegateDatabase db = new DelegateDatabase();
		List<String> result = null;
		result = db.getCodeSpace();
		ObjXmlCodespace cList = new ObjXmlCodespace();
		cList.setCodespaceList(result);
		return Response.ok(cList).build();
	}
	
	//	@GET
	//	@Path(value = "group")
	//	@Produces({ MediaType.APPLICATION_JSON })
	//	public Response getGroupComposition() {
	//		LOGGER.info("In getGroupComposition");
	//		//ottengo true se la validazione trova un oggetto non valido
	//		DelegateDatabase db = new DelegateDatabase();
	//		ObjXmlToInsertLampParent parent = new ObjXmlToInsertLampParent();
	//		List<ObjXmlToInsertLamp> listObjXmlToInsertLamp = new ArrayList<ObjXmlToInsertLamp>();
	//		List<ObjLampCtrl> lampCtrList = db.getObjLampCtrl();
	//		if (lampCtrList.isEmpty()) {
	//			return Response.status(404).header("error", "group not found").build();
	//		}
	//		for (ObjLampCtrl lamp : lampCtrList) {
	//			ObjXmlToInsertLamp xml = new ObjXmlToInsertLamp();
	//			xml.setTableLampCtrl(lamp);
	//			listObjXmlToInsertLamp.add(xml);
	//		}
	//		parent.setObjXmlInserLamp(listObjXmlToInsertLamp);
	//		//		return Response.ok(listObjXmlToInsertLamp).contentLocation(new URI("/user-management/users/123")).build();
	//		return Response.ok(parent).build();
	//	}
	
	//	/**
	//	 * @param codespace
	//	 * @param usrId
	//	 * @return
	//	 * @throws URISyntaxException
	//	 */
	//	@GET
	//	@Path(value = "css/{CODESPACE}/group/searchff")
	//	//todo dovrebbe darti il gruppo che contiene una lampada con quel usrid
	//	@Produces({ MediaType.APPLICATION_JSON })
	//	public Response searchFromUsrId(@PathParam("CODESPACE") String codespace, @QueryParam("lamp") String usrId)
	//			throws URISyntaxException {
	//		LOGGER.info("In getGroupComposition");
	//		//ottengo true se la validazione trova un oggetto non valido
	//		if (usrId == null || Validazione.validationString(usrId)) {
	//			return Response.status(400).build();
	//		}
	//		DelegateDatabase db = new DelegateDatabase();
	//		List<ObjLampCtrl> lampCtrList = db.getObjLampCtrlParentFromUsrId(usrId, codespace);
	//		
	//		//		return Response.ok(listObjXmlToInsertLamp).contentLocation(new URI("/user-management/users/123")).build();
	//		if (lampCtrList != null && !lampCtrList.isEmpty()) {
	//			return Response.ok(lampCtrList).build();
	//		}
	//		if (lampCtrList.isEmpty()) {
	//			return Response.status(400).header("error", "element not found").build();
	//		}
	//		return Response.status(500).header("error", "connection error").build();
	//	}
	
	//	POST METHODS
	
	/**
	 * @param userid
	 * @param ctrlid
	 * @param spsid
	 * @param sosid
	 * @param type
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML })
	@Path(value = "css/{CODESPACE}/groups")
	public Response insert(@PathParam("CODESPACE") String codespace, JAXBElement<ObjXmlToInsertLamp> xmlInsertOperaJaxb) {
		LOGGER.info("In insert with xml");
		try {
			ObjXmlToInsertLamp xmlInput = xmlInsertOperaJaxb.getValue();
			ObjLampCtrl lampCtrl = xmlInput.getTableLampCtrl();
			DelegateDatabase db = new DelegateDatabase();
			//controllo se il codespace in url e' uguale al codespace nell'oggetto
			if (!lampCtrl.getCodeSpace().equalsIgnoreCase(codespace)) {
				return Response.status(400).header("error", codespace + " not conform").build();
			}
			//vado a salvare il lampione semplice o gruppo
			Transaction tx = null;
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			Serializable lampCtrlId = (Integer) db.save(lampCtrl, tx, session);
			if (lampCtrlId != null) {
				int lampCtrlId_intValue = (Integer) lampCtrlId;
				DelegateXml xmlutil = new DelegateXml();
				List<ObjLampCtrl> grpComp = xmlInput.getLamps();
				//salvo la composizione dell'eventuale gruppo, se il type e' S non salvo nulla
				if (grpComp != null
						&& !grpComp.isEmpty()
						&& (lampCtrl.getTypeGroup().equalsIgnoreCase("Q") || lampCtrl.getTypeGroup().equalsIgnoreCase(
								"V"))) {
					List<String> listCtrlid = new ArrayList<String>();
					for (ObjLampCtrl lamp : grpComp) {
						listCtrlid.add(lamp.getCtrlId());
					}
					List<ObjGroupComposition> listGroup = xmlutil.getObjGroupComposition(listCtrlid,
							lampCtrlId_intValue, tx, session, codespace);
					for (ObjGroupComposition record : listGroup) {
						//in controllo di NON inserimento gruppi con type V mi viene in automatico dal fatto che non hanno il ctrlId..
						db.save(record, tx, session);
					}
				}
			}
			return Response.status(200).build();
		}
		catch (Exception e) {
			LOGGER.info("Error in insert", e);
			return Response.status(400).build();
		}
		
	}
	
	/**
	 * inserimento di lampade o gruppi di lampade
	 * 
	 * @param document
	 * @return
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	@Path(value = "css/{CODESPACE}/groups")
	public Response insert(@PathParam("CODESPACE") String codespace, String document) {
		LOGGER.info("In insert with json");
		Transaction tx = null;
		boolean finded = false;
		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjXmlToInsertGroup jsonInput = mapper.readValue(document, ObjXmlToInsertGroup.class);
			
			ObjLampCtrl lampCtrl = jsonInput.getTableLampCtrl();
			DelegateDatabase db = new DelegateDatabase();
			//controllo se il codespace in url e' uguale al codespace nell'oggetto
			if (!lampCtrl.getCodeSpace().equalsIgnoreCase(codespace)) {
				return Response.status(400).header("error", codespace + " not conform").build();
			}
			//vado a salvare il lampione semplice o gruppo
			Session session = HibernateUtil.getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			if (db.checkExistsGroup(lampCtrl, tx, session)) {
				db.deleteToUpdate(lampCtrl, tx, session);
				finded = true;
				//				return Response.status(400).header("error", "Device already exists").build();
			}
			Serializable lampCtrlId = db.save(lampCtrl, tx, session);
			//			session = HibernateUtil.getSessionFactory().getCurrentSession();
			//			tx = session.beginTransaction();
			if (lampCtrlId != null) {
				int lampCtrlId_intValue = (Integer) lampCtrlId;
				lampCtrl.setId(lampCtrlId_intValue);
				DelegateXml xmlutil = new DelegateXml();
				//salvo la composizione dell'eventuale gruppo, se il type e' S non salvo nulla
				if (lampCtrl.getTypeGroup().equalsIgnoreCase("Q") || lampCtrl.getTypeGroup().equalsIgnoreCase("V")) {
					List<Integer> listdeviceId = jsonInput.getListLampId();
					List<ObjGroupComposition> listGroup = xmlutil.getObjGroupComposition(listdeviceId,
							lampCtrlId_intValue, tx, session, codespace);
					for (ObjGroupComposition record : listGroup) {
						//in controllo di NON inserimento gruppi con type V mi viene in automatico dal fatto che non hanno il ctrlId..
						db.saveAll(record, tx, session);
					}
					//ora sincronizzo con sps solo se il gruppo e' inserito per la prima volta
					if (!finded) {
						DelegateSps spsObject = new DelegateSps();
						spsObject.syncronizeSps(lampCtrl);
					}
					if (!tx.wasCommitted()) {
						
						tx.commit();
					}
				}
			}
			return Response.ok(lampCtrl).build();
		}
		catch (Exception e) {
			LOGGER.info("Error in insert", e);
			tx.rollback();
			return Response.status(400).build();
		}
		
	}
	
	//	PUT METHODS
	
	//	/**
	//	 * modifica dei parametri di un gruppo
	//	 * 
	//	 * @param codespace
	//	 * @param sid
	//	 * @param usrId
	//	 * @param typeOfLamp
	//	 * @param nominalPower
	//	 * @param height
	//	 * @return
	//	 */
	//	@PUT
	//	@Path(value = "css/{CODESPACE}/groups/{ID}")
	//	@Produces({ MediaType.APPLICATION_JSON })
	//	public Response update(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid,
	//			@QueryParam("USRID") String usrId, @QueryParam("TYPEOFLAMP") String typeOfLamp,
	//			@QueryParam("NOMINALPOWER") String nominalPower, @QueryParam("HEIGHT") String height) {
	//		LOGGER.info("In update");
	//		//ottengo true se la validazione trova un oggetto non valido
	//		int id = 0;
	//		try {
	//			id = Integer.parseInt(sid);
	//		}
	//		catch (Exception e) {
	//			LOGGER.info("id " + id + "incorrect!", e);
	//			return Response.status(400).header("error", "id not a integer").build();
	//		}
	//		
	//		return commonUpdate(id, codespace, usrId, null, typeOfLamp, nominalPower, height, null);
	//	}
	
	/**
	 * metodo per impostare a connect = TRUE il gruppo
	 * 
	 * @param codespace
	 * @param sid
	 * @param connect
	 * @return
	 */
	@PUT
	@Path(value = "css/{CODESPACE}/devices/{ID}/connect")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateConnect(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid,
			@QueryParam("connect") String connect) {
		LOGGER.info("In update of CONNECT");
		//ottengo true se la validazione trova un oggetto non valido
		int id = 0;
		try {
			id = Integer.parseInt(sid);
		}
		catch (Exception e) {
			LOGGER.info("id " + id + "incorrect!", e);
			return Response.status(400).header("error", "id not a integer").build();
		}
		
		//il valore true string del connect è gestito internamente
		return commonUpdate(id, codespace, null, null, null, null, null, connect, null);
	}
	
	//	DELETE METHODS
	
	/**
	 * cancellazione del gruppo
	 * 
	 * @param codespace
	 * @param usrId
	 * @return
	 */
	@DELETE
	@Path(value = "css/{CODESPACE}/groups/{ID}")
	public Response delete(@PathParam("CODESPACE") String codespace, @PathParam("ID") int id) {
		LOGGER.info("In delete");
		try {
			DelegateDatabase db = new DelegateDatabase();
			ObjLampCtrl objLamp = db.getObjLampCtrlFromID(id, codespace);
			if (objLamp == null) {
				return Response.status(404).header("message", id + " not present in db.").build();
			}
			if (db.delete(objLamp)) {
				return Response.status(200).build();
			}
			else {
				return Response.status(400).build();
			}
			
		}
		catch (Exception e) {
			LOGGER.info("Error in delete", e);
			return Response.status(400).build();
		}
		
	}
	
	/**
	 * cancellazione del tasksps
	 * 
	 * @param codespace
	 * @param usrId
	 * @return
	 */
	@DELETE
	@Path(value = "css/{CODESPACE}/tasks")
	public Response deleteTasks(@PathParam("CODESPACE") String codespace, @QueryParam("spsid") String spsid,
			@QueryParam("taskid") String taskid) {
		LOGGER.info("In delete task");
		
		try {
			DelegateDatabase db = new DelegateDatabase();
			List<Boolean> listResp = db.deleteTask(spsid, taskid, codespace);
			if (!listResp.get(0)) {
				return Response.status(400).header("error", spsid + " not present in db.").build();
			}
			return Response.status(200).build();
		}
		catch (Exception e) {
			LOGGER.info("Error in delete", e);
			return Response.status(400).build();
		}
		
	}
	
	//	@DELETE
	//	@Path(value = "css/{CODESPACE}/tasks")
	//	public Response deleteTasks(@PathParam("CODESPACE") String codespace, String document) {
	//		LOGGER.info("In delete task");
	//		ObjectMapper mapper = new ObjectMapper();
	//		ObjXmlPayloadTask jsonInput = null;
	//		try {
	//			jsonInput = mapper.readValue(document, ObjXmlPayloadTask.class);
	//		}
	//		catch (JsonParseException e1) {
	//			LOGGER.error("Parsing json failed", e1);
	//			return Response.status(400).build();
	//		}
	//		catch (JsonMappingException e1) {
	//			LOGGER.error("Parsing json failed", e1);
	//			return Response.status(400).build();
	//		}
	//		catch (IOException e1) {
	//			LOGGER.error("Parsing json failed", e1);
	//			return Response.status(400).build();
	//		}
	//		
	//		try {
	//			DelegateDatabase db = new DelegateDatabase();
	//			List<Boolean> listResp = db.deleteTask(jsonInput.getSpsid(), jsonInput.getTaskid(), codespace);
	//			if (!listResp.get(0)) {
	//				return Response.status(400).header("error", jsonInput.getSpsid() + " not present in db.").build();
	//			}
	//			if (!listResp.get(1)) {
	//				return Response.status(400).header("error", jsonInput.getTaskid() + " not present in db.").build();
	//			}
	//			return Response.status(200).build();
	//		}
	//		catch (Exception e) {
	//			LOGGER.info("Error in delete", e);
	//			return Response.status(400).build();
	//		}
	//		
	//	}
	
	@GET
	@Path(value = "jsonTestTask")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response testTask() {
		LOGGER.info("In test task");
		ObjXmlPayloadTask p = new ObjXmlPayloadTask();
		p.setSpsid("spsDiprova");
		p.setTaskid("taskDiProva");
		return Response.ok(p).build();
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/tasks")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTasksFromSpsId(@PathParam("CODESPACE") String codespace, @QueryParam("spsid") String spsId) {
		LOGGER.info("In GET task from spsId");
		DelegateSps dbsps = new DelegateSps();
		List<Trigger> triggers = null;
		List<ObjXmlPayloadTaskDimming> resp = new ArrayList<ObjXmlPayloadTaskDimming>();
		try {
			triggers = dbsps.getTaskFromQuartz(spsId);
		}
		catch (SchedulerException e) {
			LOGGER.error(e);
		}
		for (Trigger trigger : triggers) {
			//cmd.getDimmer()) + "-" + String.valueOf(cmd.getPriority()) + "-"+ cmd.getDayOfWeek()
			String description = trigger.getDescription();
			
			Integer dimmerValue;
			String dayofweek = "";
			String priority = "";
			try {
				dimmerValue = Integer.parseInt(description.split("-")[0]);
				priority = description.split("-")[1];
				dayofweek = description.split("-")[2];
			}
			catch (Exception e) {
				dimmerValue = 0;
			}
			ObjXmlPayloadTaskDimming payload = new ObjXmlPayloadTaskDimming();
			payload.setSpsid(trigger.getJobKey().getName());
			payload.setTaskid(trigger.getJobKey().getGroup());
			payload.setDimm(Integer.toString(dimmerValue));
			payload.setPriority(priority);
			payload.setDayofweek(dayofweek);
			payload.setStartdate(trigger.getStartTime().toString());
			try {
				payload.setEnddate(trigger.getEndTime().toString());
			}
			catch (Exception e) {
				payload.setEnddate(null);
			}
			
			try {
				payload.setNextFire(trigger.getNextFireTime().toString());
			}
			catch (Exception e) {
				payload.setNextFire("EXPIRED");
			}
			resp.add(payload);
		}
		//		if (resp.isEmpty()) {
		//			return Response.status(404).build();
		//		}
		return Response.ok(resp).build();
	}
	
	@GET
	@Path(value = "jsonTest")
	@Produces({ MediaType.APPLICATION_JSON })
	//	@Produces({ MediaType.APPLICATION_XML })
	public Response testQ() {
		LOGGER.info("In test");
		
		ObjXmlToInsertLamp xml = new ObjXmlToInsertLamp();
		ObjLampCtrl lampc = new ObjLampCtrl();
		lampc.setCtrlId("dasdda");
		lampc.setSosId("asdssada");
		lampc.setSpsId("sdfgdfg");
		lampc.setTypeGroup("S");
		lampc.setTypeOfLamp("led");
		lampc.setUsrId("asdasd");
		xml.setTableLampCtrl(lampc);
		
		List<ObjLampCtrl> cmp = new ArrayList<ObjLampCtrl>();
		ObjLampCtrl ob = new ObjLampCtrl();
		ob.setCtrlId("sds");
		cmp.add(ob);
		ob = new ObjLampCtrl();
		ob.setCtrlId("sdsds");
		cmp.add(ob);
		ob = new ObjLampCtrl();
		ob.setCtrlId("dasda");
		cmp.add(ob);
		ob = new ObjLampCtrl();
		ob.setCtrlId("dasdas");
		cmp.add(ob);
		ob = new ObjLampCtrl();
		ob.setCtrlId("sfsdfsds");
		cmp.add(ob);
		ObjXmlGroup xmlG = new ObjXmlGroup();
		xmlG.setLamps(cmp);
		
		xml.setLamps(cmp);
		
		ObjXmlToInsertGroup xmlGi = new ObjXmlToInsertGroup();
		xmlGi.setTableLampCtrl(lampc);
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(2);
		ids.add(43);
		ids.add(1);
		xmlGi.setListLampId(ids);
		
		return Response.ok(xmlGi).build();
	}
	
	//	########################################## DEVICE ##########################
	
	/**
	 * @param codespace
	 * @param usrId
	 * @return
	 */
	@GET
	@Path(value = "css/{CODESPACE}/devices")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getGroupCompositionDevice(@PathParam("CODESPACE") String codespace,
			@QueryParam("type") String typeGroup, @QueryParam("usrid") String usrId, @QueryParam("spsid") String spsId) {
		LOGGER.info("In getGroupComposition");
		
		if (usrId != null && spsId != null) {
			return Response.status(400).header("error", "only one parameter").build();
		}
		
		//ottengo true se la validazione trova un oggetto non valido
		if (typeGroup != null && !Validazione.validationType(typeGroup)) {
			return Response.status(400).header("type", "type not correct").build();
		}
		DelegateDatabase db = new DelegateDatabase();
		List<ObjLampCtrl> lampCtrList = null;
		if (typeGroup != null) {
			lampCtrList = db.getObjLampCtrl(codespace, typeGroup);
			if (lampCtrList.isEmpty()) {
				return Response.status(404).header("error", "group not found").build();
			}
		}
		else if (usrId != null) {
			if (usrId != null && Validazione.validationString(usrId)) {
				return Response.status(400).header("error", "validation parameter failed").build();
			}
			else {
				lampCtrList = db.getObjLampCtrlFromUsrId(usrId, codespace);
				if (lampCtrList.isEmpty()) {
					return Response.status(404).header("error", "group not found").build();
				}
			}
		}
		else if (spsId != null) {
			//ottengo true se la validazione trova un oggetto non valido
			
			List<ObjLampCtrl> devices = null;
			if (!Validazione.validationUri(spsId)) {
				
				devices = db.getObjLampCtrlFromSpsId(spsId, codespace);
				
			}
			if (devices != null && !devices.isEmpty()) {
				
				return Response.ok(devices.get(0)).build();
			}
		}
		else {
			lampCtrList = db.getObjLampCtrl(codespace);
			if (lampCtrList.isEmpty()) {
				return Response.status(404).header("error", "group not found").build();
			}
		}
		return Response.ok(lampCtrList).build();
	}
	
	/**
	 * @param codespace
	 * @param sid
	 * @return
	 */
	@GET
	@Path(value = "css/{CODESPACE}/devices/{ID}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDeviceFromId(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid) {
		LOGGER.info("In getGroupComposition");
		//ottengo true se la validazione trova un oggetto non valido
		int id = 0;
		try {
			id = Integer.parseInt(sid);
		}
		catch (Exception e) {
			LOGGER.info("id " + id + "incorrect!", e);
			return Response.status(400).header("error", "id not a integer").build();
		}
		DelegateDatabase db = new DelegateDatabase();
		
		ObjLampCtrl lampCtrList = db.getObjLampCtrlFromID(id, codespace);
		if (lampCtrList == null) {
			return Response.status(404).header("error", "group not found").build();
		}
		return Response.ok(lampCtrList).build();
	}
	
	/**
	 * modifica dei parametri di un gruppo
	 * 
	 * @param codespace
	 * @param sid
	 * @param usrId
	 * @param typeOfLamp
	 * @param nominalPower
	 * @param height
	 * @return
	 */
	@PUT
	@Path(value = "css/{CODESPACE}/devices/{ID}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateDevice(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid,
			@QueryParam("usrid") String usrId, @QueryParam("typeoflamp") String typeOfLamp,
			@QueryParam("nominalpower") String nominalPower, @QueryParam("height") String height) {
		LOGGER.info("In update");
		//ottengo true se la validazione trova un oggetto non valido
		int id = 0;
		try {
			id = Integer.parseInt(sid);
		}
		catch (Exception e) {
			LOGGER.info("id " + id + "incorrect!", e);
			return Response.status(400).header("error", "id not a integer").build();
		}
		
		return commonUpdate(id, codespace, usrId, null, typeOfLamp, nominalPower, height, null, null);
	}
	
	/**
	 * @param codespace
	 * @param sid
	 * @param ctrlId
	 * @return
	 */
	@PUT
	@Path(value = "css/{CODESPACE}/devices/{ID}/ctrlid")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateCtrlId(@PathParam("CODESPACE") String codespace, @PathParam("ID") String sid,
			@QueryParam("ctrlid") String ctrlId, @QueryParam("valid") Boolean valid) {
		LOGGER.info("In update of CTRLID");
		//ottengo true se la validazione trova un oggetto non valido
		int id = 0;
		try {
			id = Integer.parseInt(sid);
		}
		catch (Exception e) {
			LOGGER.info("id " + id + "incorrect!", e);
			return Response.status(400).header("error", "id not a integer").build();
		}
		if (ctrlId != null) {
			return commonUpdate(id, codespace, null, ctrlId, null, null, null, null, null);
		}
		else {
			if (valid != null) {
				return commonUpdate(id, codespace, null, null, null, null, null, null, valid);
			}
		}
		return Response.status(400).header("error", "you must set at least one parameter").build();
	}
	
	private final Response commonUpdate(int id, String codespace, String usrId, String ctrlId, String typeOfLamp,
			String nominalPower, String height, String connect, Boolean valid) {
		
		DelegateDatabase db = new DelegateDatabase();
		ObjLampCtrl objLamp = db.getObjLampCtrlFromID(id, codespace);
		Transaction tx = null;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		tx = session.beginTransaction();
		if (objLamp != null) {
			if (!Functions.updateObjectLamp(objLamp, usrId, ctrlId, typeOfLamp, nominalPower, height, connect, valid)) {
				return Response.status(400).header("error", "Parameters in input malformed").build();
			}
			db.update(objLamp, tx, session);
			LOGGER.info("Update done");
			return Response.ok(objLamp).build();
		}
		return Response.status(204).header("error", "group " + id + " not present in db.").build();
	}
	
	@GET
	@Path(value = "css/{CODESPACE}/devices/search")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response searchDeviceFromSpsId(@PathParam("CODESPACE") String codespace, @QueryParam("sosid") String sosId)
			throws URISyntaxException {
		LOGGER.info("In getGroupComposition");
		//ottengo true se la validazione trova un oggetto NON valido
		if (Validazione.validationUri(sosId)) {
			return Response.status(400).build();
		}
		DelegateDatabase db = new DelegateDatabase();
		
		List<ObjLampCtrl> devices = db.getObjLampCtrlFromSosId(sosId, codespace);
		
		if (devices != null && !devices.isEmpty()) {
			
			return Response.ok(devices.get(0)).build();
		}
		
		return Response.status(400).header("error", "element not found").build();
		
	}
	
	//	/**
	//	 * @param codespace
	//	 * @param usrId
	//	 * @return
	//	 */
	//	@GET
	//	@Path(value = "css/{CODESPACE}/devices")
	//	@Produces({ MediaType.APPLICATION_JSON })
	//	public Response getDevicesFromUsrid(@PathParam("CODESPACE") String codespace, @QueryParam("USRID") String usrId) {
	//		LOGGER.info("In getGroupComposition");
	//		//ottengo true se la validazione trova un oggetto non valido
	//		if (usrId != null && Validazione.validationString(usrId)) {
	//			return null;
	//		}
	//		DelegateDatabase db = new DelegateDatabase();
	//		List<ObjLampCtrl> lampCtrList = null;
	//		if (usrId != null) {
	//			lampCtrList = db.getObjLampCtrlFromUsrId(usrId, codespace);
	//			if (lampCtrList.isEmpty()) {
	//				return Response.status(404).header("error", "group not found").build();
	//			}
	//		}
	//		else {
	//			return Response.status(400).build();
	//		}
	//		return Response.ok(lampCtrList).build();
	//	}
	
}

/*
 * se e' un Q.. lo comando direttamente quindi valorizzo in grpcmp ma devo mettere le lampade in lampq se e' un V..
 * niente valorizzazione in grpcmp ma metto in lampv se e' un S.. niente..da vedere se salvare o meno..io direi di si
 */