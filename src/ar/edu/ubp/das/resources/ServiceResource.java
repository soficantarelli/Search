package ar.edu.ubp.das.resources;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.apache.cxf.endpoint.Client;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.time.Duration;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;

import ar.edu.ubp.das.beans.ServicesBean;
import ar.edu.ubp.das.beans.WebsitesBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elasticsearch.ElasticSearch;
//import ar.edu.ubp.das.elasticsearch.ElasticSearch;
import ar.edu.ubp.das.logger.MyLogger;
import ar.edu.ubp.das.security.Secured;

@Path("services")
public class ServiceResource {

	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	private ElasticSearch elasticSearch;
	private HttpClient MyHttpClient;
	
	public ServiceResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
		this.MyHttpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(5)).build();
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "SERVICES: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(ServicesBean service) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");
			
			service.setIdUser((Integer)request.getProperty("id"));
			
			ServicesBean newService = dao.insert(service);
			this.logger.log(MyLogger.INFO, "SERVICES: Ok post");
			
			return Response.status(Status.OK).entity(newService).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error post");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_services() {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");	 
			List<ServicesBean> services = dao.select(null);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok get");
			
			return Response.status(Status.OK).entity(services).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error get");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("by-user")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_services_user() {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");
			
			ServicesBean service = new ServicesBean();
			service.setIdUser((Integer)request.getProperty("id"));
			
			List<ServicesBean> services = dao.select(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok get by user");
			
			return Response.status(Status.OK).entity(services).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error get by user");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_service(@PathParam("id") Integer id) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");	 
			
			ServicesBean service = new ServicesBean();
			service.setIdService(id);
			
			List<ServicesBean> serviceFind = dao.select(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok get id");
			
			return Response.status(Status.OK).entity(serviceFind.get(0)).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error get id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("reindex/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response reindex(@PathParam("id") Integer id) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");	
			Dao<WebsitesBean, WebsitesBean> daoWebSite = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	
			
			WebsitesBean websites = new WebsitesBean();
			websites.setIdService(id);
			List<WebsitesBean> websitesList = daoWebSite.select(websites);
			
			if (websitesList.size() > 0) {
				//delete all websites of this service
				daoWebSite.delete(websites);
				//delete all in elasticSearch -> hacer un for
			}
			
			ServicesBean service = new ServicesBean();
			service.setIdService(id);
			service.setReindex(true);
			
			ServicesBean serviceUpdated = dao.update(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok update reindex/id");
			
			return Response.status(Status.OK).entity(serviceUpdated).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error update reindex/id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	public Response update(@PathParam("id") Integer id, ServicesBean service) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");	
			Dao<WebsitesBean, WebsitesBean> daoWebSite = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	
			
			WebsitesBean websites = new WebsitesBean();
			websites.setIdService(id);
			List<WebsitesBean> websitesList = daoWebSite.select(websites);

			if (websitesList.size() > 0) {
				System.out.print("entre");
				//delete all websites of this service
				//daoWebSite.delete(websites);
				//delete all in elasticSearch -> hacer un for
			}
			
			System.out.print(service.getUrl());
			System.out.print(service.getProtocol());
			
			service.setIdUser((Integer)request.getProperty("id"));
			
			ServicesBean serviceUpdated = dao.update(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok update by id");
			
			return Response.status(Status.OK).entity(serviceUpdated).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error update by id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Path("up/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response up(@PathParam("id") Integer id, ServicesBean service) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");
			
			service.setIdService(id);
			
			ServicesBean serviceUpdated = dao.update(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok put up");
			
			return Response.status(Status.OK).entity(serviceUpdated).build();
		}catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error put up");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@DELETE
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") Integer id, @QueryParam("website") Boolean website) {
		try {
			Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");	
			Dao<WebsitesBean, WebsitesBean> daoWebSite = DaoFactory.getDao("WebSite", "ar.edu.ubp.das");	
			
			ServicesBean service = new ServicesBean();
			service.setIdService(id);
			
			WebsitesBean websites = new WebsitesBean();
			websites.setIdService(id);
			List<WebsitesBean> websitesList = daoWebSite.select(websites);
			
			if (websitesList.size() > 0) {
				if (website) {
					//update all website with this id with null in id service
					daoWebSite.update(websites);
					
				} else {
					//delete all websites of this service
					daoWebSite.delete(websites);
					//delete all in elasticSearch -> hacer un for
					//elasticSearch
				}
			}
			
			dao.delete(service);
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok delete id");
			
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SERVICES: Error delete id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	

	@POST
	@Path("ping-service")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ping_service(ServicesBean service) throws SQLException {
		Dao<ServicesBean, ServicesBean> dao = DaoFactory.getDao("Service", "ar.edu.ubp.das");
		try {
			if (service.getProtocol().equals("REST")) {
				
				if (service.getUrl().toLowerCase().contains("?wsdl")) {
					this.logger.log(MyLogger.INFO, "SERVICES: Error ping service - Protocolo incorrecto REST");
					throw new BadRequestException("REST - Protocolo incorrecto");
				}
				
				HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(service.getUrl() + "ping")).build();
				HttpResponse<String> response = MyHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() >= 400) {
					throw new BadRequestException("El servicio no responde");
				}
				
			} else {
				
				if (!service.getUrl().toLowerCase().contains("?wsdl")) {
					this.logger.log(MyLogger.INFO, "SERVICES: Error ping service - Protocolo incorrecto SOAP");
					throw new BadRequestException("SOAP - Protocolo incorrecto");
				}
				
				JaxWsDynamicClientFactory jdcf = JaxWsDynamicClientFactory.newInstance();
				Client client = jdcf.createClient(service.getUrl());
				client.invoke("ping");
				client.close();
			}
			
			if (service.getIdService() != null) {
				ServicesBean serviceUpdate = new ServicesBean();
				serviceUpdate.setIdService(service.getIdService());;
				serviceUpdate.setUp(true);
				dao.update(serviceUpdate);
			}
			
			this.logger.log(MyLogger.INFO, "SERVICES: Ok ping service");
			return Response.status(Status.OK).entity("pong!").build();
		} catch (Exception e) {
			
			if (service.getIdService() != null) {
				ServicesBean serviceNew = new ServicesBean();
				serviceNew.setIdService(service.getIdService());
				serviceNew.setUp(false);
				dao.update(serviceNew);
			}
			
			this.logger.log(MyLogger.ERROR, "SERVICES: Error ping service");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}