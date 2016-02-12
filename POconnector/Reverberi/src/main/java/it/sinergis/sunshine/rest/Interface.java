package it.sinergis.sunshine.rest;

import it.sinergis.sunshine.delegate.Database;
import it.sinergis.sunshine.properties.ReadFromConfig;
import it.sinergis.sunshine.utils.Functions;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("/Action")
public class Interface {
	private static final Logger logger = Logger.getLogger(Interface.class);
	private static final int BadStatus = 400;
	private static final String CMD_STATUS = "0";
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/light_control")
	public final Response lightup(@QueryParam("PROCEDURE_ID")
	String valueA,/*
				 * @QueryParam("REC_DATE") String valueB, @QueryParam("CMD_STATUS") String valueC,
				 */@QueryParam("DIMMERPERCENT")
	String valueD, @QueryParam("USERNAME")
	String valueE) throws ClassNotFoundException, SQLException {
		logger.info("Ligth control");
		String queryLamp = Functions.createQueryLamp(ReadFromConfig.loadByName("nameTableLamp"),
				ReadFromConfig.loadByName("colonnaA"), ReadFromConfig.loadByName("colonnaB"),
				ReadFromConfig.loadByName("colonnaC"), ReadFromConfig.loadByName("colonnaD"),
				ReadFromConfig.loadByName("colonnaE"), valueA, getStringDate("dd/MM/yyyy"), CMD_STATUS, valueD, valueE);
		if (queryLamp == null) {
			return Response.status(BadStatus).build();
		}
		logger.info("Query created successfully");
		logger.info("Now intent to launch the query for lamp " + valueA);
		Database db = new Database();
		return Response.status(db.insert(queryLamp)).entity("Command launched on light source " + valueA).build();
	}
	
	private String getStringDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date();
		return sdf.format(date);
	}
}
