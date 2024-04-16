package ar.edu.ubp.das.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.edu.ubp.das.beans.MetadataBean;
import ar.edu.ubp.das.beans.PreferenceBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elasticsearch.ElasticSearch;
import ar.edu.ubp.das.logger.MyLogger;
import ar.edu.ubp.das.security.Secured;

@Path("metadata")
public class MetadatoResource {
	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	
	public MetadatoResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "METADATA: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	public Response getMetadata() {
		try {
			ElasticSearch elasticSearch = new ElasticSearch();
			
			List<MetadataBean> metadata = elasticSearch.getMetadata((Integer)request.getProperty("id"), false);
			//List<MetadataBean> metadata = elasticSearch.getMetadata(1005, false);
					
			this.logger.log(MyLogger.INFO, "Get metadatos");
			
			return Response.status(Status.OK).entity(metadata).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Get Metadata");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
	
	@GET
	@Path("approved")
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	public Response getMetadataApproved() {
		try {
			ElasticSearch elasticSearch = new ElasticSearch();
			
			List<MetadataBean> metadata = elasticSearch.getMetadata((Integer)request.getProperty("id"), true);
			//List<MetadataBean> metadata = elasticSearch.getMetadata(1005, true);
					
			this.logger.log(MyLogger.INFO, "Get metadatos Approved");
			
			return Response.status(Status.OK).entity(metadata).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Get Metadata");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	public Response updateMetadata(MetadataBean metadata) {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");
			List<PreferenceBean> preference = dao.select(null);
			
			Integer time = preference.get(0).getTimeMetadata();
			
			ElasticSearch elasticSearch = new ElasticSearch();
			
			elasticSearch.update(metadata, time);
			
			this.logger.log(MyLogger.INFO, "Put metadata");
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Put Metadata");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured
	@Path("/batch")
	public Response updateBatch(List<MetadataBean> metadataList) {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");
			List<PreferenceBean> preference = dao.select(null);
			
			Integer time = preference.get(0).getTimeMetadata();
			
			ElasticSearch elasticSearch = new ElasticSearch();
			
			elasticSearch.updateBatch(metadataList, time);
			
			this.logger.log(MyLogger.INFO, "Put metadata batch");
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Put Metadata");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	
	@DELETE
	@Path("{id}")
	//@Secured
	public Response deleteMetadata(@PathParam("id") String id) {
		try {
			ElasticSearch elasticSearch = new ElasticSearch();
			
			elasticSearch.deleteByIdMetadato(id);
					
			this.logger.log(MyLogger.INFO, "Delete metadata" + id);
			
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Delete Metadata");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
		}
	}
	
	@DELETE
	@Secured
	@Path("/batch")
	public Response deleteBatch(List<MetadataBean> metadataList) {
		try {
			ElasticSearch elasticSearch = new ElasticSearch();
			
			elasticSearch.deleteBatch(metadataList);
			
			this.logger.log(MyLogger.INFO, "Delete metadata batch");
			
			return Response.status(Status.NO_CONTENT).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "ERROR: Delete Metadata batch");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
