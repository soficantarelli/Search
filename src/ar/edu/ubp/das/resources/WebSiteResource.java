package ar.edu.ubp.das.resources;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.edu.ubp.das.beans.WebsitesBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elasticsearch.ElasticSearch;
//import ar.edu.ubp.das.elasticsearch.ElasticSearch;
import ar.edu.ubp.das.logger.MyLogger;
import ar.edu.ubp.das.security.Secured;

@Path("websites")
public class WebSiteResource {
	
	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	private ElasticSearch elasticSearch;
	
	public WebSiteResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "WEBSITE: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(WebsitesBean website) {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");
			System.out.print((Integer)request.getProperty("id"));
			website.setIdUser((Integer)request.getProperty("id"));	
			
			WebsitesBean newWebsite = dao.insert(website);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok post");
			
			return Response.status(Status.OK).entity(newWebsite).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error post");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_websites() {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	 
			
			List<WebsitesBean> websites = dao.select(null);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok get");
	
			return Response.status(Status.OK).entity(websites).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error get");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("own")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_websites_user() {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");
			
			WebsitesBean website = new WebsitesBean();
			website.setIdUser((Integer)request.getProperty("id"));	
			
			List<WebsitesBean> websites = dao.select(website);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok get own");
			
			return Response.status(Status.OK).entity(websites).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error get own");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("validate")
	public Response validate(@QueryParam("url") String url) {
		try {
			url = url.replaceFirst("^https", "http");
			URL obj = new URL(url);
			
			System.out.println(obj);
			
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			
			System.out.println(connection);
			
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			connection.setRequestMethod("HEAD");
			
			int responseCode = connection.getResponseCode();
			
			System.out.println(connection);
			
			if (responseCode < 400 ) {
				this.logger.log(MyLogger.INFO, "WEBSITE: validate");
				return Response.status(Status.OK).build();
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: validate");
			return Response.status(Status.BAD_REQUEST).build();
		}
	}
	
	@GET
	@Path("{id}")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_website(@PathParam("id") Integer id) {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	 
			
			WebsitesBean service = new WebsitesBean();
			service.setIdWebSite(id);
			
			List<WebsitesBean> serviceFind = dao.select(service);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok get id");
			
			return Response.status(Status.OK).entity(serviceFind).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error get id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(WebsitesBean website) throws IOException {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	
			
			WebsitesBean websiteUpdated = dao.update(website);
			
			this.elasticSearch = new ElasticSearch();
			elasticSearch.deleteByWebsiteId(websiteUpdated.getIdWebSite());
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok put");
			
			return Response.status(Status.OK).entity(websiteUpdated).build();
		}catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error put");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("reindex/{id}")
	@Secured
	public Response reindex(@PathParam("id") Integer id) throws IOException {
			try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");
			
			WebsitesBean website = new WebsitesBean();
			website.setIdWebSite(id);
			website.setReindex(true);
			
			this.elasticSearch = new ElasticSearch();
			elasticSearch.deleteByWebsiteId(website.getIdWebSite());
			
			dao.update(website);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok put reindex id");
			
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error put reindex id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("up")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response up(WebsitesBean website) throws IOException {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	
			
			WebsitesBean websiteUpdated = dao.update(website);
			
			this.elasticSearch = new ElasticSearch();
			elasticSearch.deleteByWebsiteId(websiteUpdated.getIdWebSite());
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok put up");
			
			return Response.status(Status.OK).entity(websiteUpdated).build();
		}catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error put up");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@DELETE
	@Path("{id}")
	@Secured
	public Response delete(@PathParam("id") Integer id) throws IOException {
		try {
			Dao<WebsitesBean, WebsitesBean> dao = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");
			
			WebsitesBean website = new WebsitesBean();
			website.setIdWebSite(id);
			
			this.elasticSearch = new ElasticSearch();
			elasticSearch.deleteByWebsiteId(website.getIdWebSite());
			
			dao.delete(website);
			
			this.logger.log(MyLogger.INFO, "WEBSITE: Ok delete id");
			
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "WEBSITE: Error delete id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}
