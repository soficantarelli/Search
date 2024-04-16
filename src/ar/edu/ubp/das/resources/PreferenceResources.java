package ar.edu.ubp.das.resources;

import org.glassfish.jersey.media.multipart.ContentDisposition;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.edu.ubp.das.beans.PreferenceBean;
import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.logger.MyLogger;
import ar.edu.ubp.das.security.Secured;

@Path("preferences")
public class PreferenceResources {
	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	
	public PreferenceResources() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "PREFERENCES: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@GET
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_preferences() {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");	 
			
			Integer value = (Integer) request.getProperty("id");
			
			PreferenceBean preference = new PreferenceBean();
			preference.setIdUser(value);
			
			List<PreferenceBean> preferenceFind = dao.select(preference);
			
			
			PreferenceBean find = preferenceFind.get(0);
			
			this.logger.log(MyLogger.INFO, "PREFERENCE: Ok get");
			
			System.out.print(find);
			
			return Response.status(Status.OK).entity(find).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "PREFERENCE: Error get");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@POST
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response post(PreferenceBean preference) {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");	 
			
			
			PreferenceBean newPreference = new PreferenceBean();
			newPreference.setIdUser(preference.getIdUser());
			
			PreferenceBean preferenceFind = dao.insert(newPreference);
			
			this.logger.log(MyLogger.INFO, "PREFERENCE: Ok post");
			
			return Response.status(Status.OK).entity(preferenceFind).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "PREFERENCE: Error post");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(PreferenceBean preference) {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");	 
			
			Integer value = (Integer) request.getProperty("id");
			preference.setIdUser(value);
			
			PreferenceBean preferenceUpdated = dao.update(preference);
			
			this.logger.log(MyLogger.INFO, "PREFERENCE: Ok put");
			
			return Response.status(Status.OK).entity(preferenceUpdated).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "PREFERENCE: Error put");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("file")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response get_file() {
		File f = new File("/Users/sofiacantarelli/Documents/DAS/buscador.js");
		
		System.out.println(f.getAbsolutePath());
	    if (!f.exists()) {
	    	this.logger.log(
	    		MyLogger.ERROR,
	    		"Descarga de componente de búsqueda con error: No se pudo crear el archivo"
	    	);
	        return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	    }
	    ContentDisposition contentDisposition = ContentDisposition.type("attachment")
	    	    .fileName("buscador.js").creationDate(new Date()).build();
	    this.logger.log(MyLogger.INFO, "Descarga de componente de búsqueda exitosa.");
	    return Response.status(Status.OK).entity(f)
	    		.header("Content-Disposition", contentDisposition).build();
	}
	
	@GET
	@Path("token/{token}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_preferencesby_token(@PathParam("token") String token) {
		try {
			Dao<PreferenceBean, PreferenceBean> dao = DaoFactory.getDao("Preference", "ar.edu.ubp.das");	 
			
			UserBean user = new UserBean();
			user.setToken(token);
			
			Dao<UserBean, UserBean> daoUser = DaoFactory.getDao("Users", "ar.edu.ubp.das");
			List<UserBean> userFind = daoUser.select(user);
			
			PreferenceBean preference = new PreferenceBean();
			preference.setIdUser(userFind.get(0).getIdUser());
			
			List<PreferenceBean> preferenceFind = dao.select(preference);
			
			
			PreferenceBean find = preferenceFind.get(0);
			
			this.logger.log(MyLogger.INFO, "PREFERENCE: Ok get");
			
			return Response.status(Status.OK).entity(find).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "PREFERENCE: Error get");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}