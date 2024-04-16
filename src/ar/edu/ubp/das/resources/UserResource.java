package ar.edu.ubp.das.resources;

import java.io.IOException;
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
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import ar.edu.ubp.das.security.AuthenticationFilter;

import ar.edu.ubp.das.beans.PreferenceBean;
import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elasticsearch.ElasticSearch;
//import ar.edu.ubp.das.elasticsearch.ElasticSearch;
import ar.edu.ubp.das.logger.MyLogger;
import io.jsonwebtoken.Jwts;
import ar.edu.ubp.das.security.Secured;


@Path("users")
public class UserResource {
	
	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	private ElasticSearch elasticSearch;
	
	public UserResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "USERS: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}

	@POST
	@Path("signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response signup(UserBean user) {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 	 
			
			UserBean newUser = dao.insert(user);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok post signup" + newUser);
			
			return Response.status(Status.OK).entity(newUser).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error post signup");
			return Response.status(Status.BAD_REQUEST).entity(user).build();
		}
	}
	
	@POST
	@Path("login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(UserBean user) {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");
			
			Boolean userValid = dao.valid(user);
			
			UserBean userSearch = new UserBean();
			userSearch.setUsername(user.getUsername());
			
			List<UserBean> userFind = dao.select(userSearch);
			UserBean userAux = new UserBean();
			
			userAux.setIdUser((userFind.get(0)).getIdUser());
			userAux.setUsername((userFind.get(0)).getUsername());
			userAux.setRole((userFind.get(0)).getRole());
			
			if (userValid) {
				String token = Jwts.builder().signWith(AuthenticationFilter.KEY).setSubject("user")
						.claim("id", userAux.getIdUser()).claim("username", userAux.getUsername())
						.claim("role", userAux.getRole()).compact();
				
				this.logger.log(MyLogger.INFO, "USERS: Ok post login");
				
				return Response.status(Status.OK).entity(token).build();
			}
			
			this.logger.log(MyLogger.INFO, "USERS: UNAUTHORIZED post login");
			
			return Response.status(Status.UNAUTHORIZED).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error post login");
			return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_users() {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 
			List<UserBean> users = dao.select(null);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok get all");
			
			return Response.status(Status.OK).entity(users).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error get all");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	
	@GET
	@Path("company")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_users_company() {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 
			
			UserBean user = new UserBean();
			user.setIdUser((Integer)request.getProperty("id"));
			
			List<UserBean> userFind = dao.select(user);
			
			UserBean userAux = new UserBean();
			userAux = userFind.get(0);
			
			UserBean userCompany = new UserBean();
			userCompany.setCompany(userAux.getIdUser()); 

			List<UserBean> users = dao.select(userCompany);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok get company");
			
			return Response.status(Status.OK).entity(users).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error get company");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	
	@GET
	@Path("info")
	@Secured
	@Produces(MediaType.APPLICATION_JSON)
	public Response get_info() {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 
			
			Integer value = (Integer) request.getProperty("id");
			
			UserBean user = new UserBean();
			user.setIdUser(value);
			
			List<UserBean> userFind = dao.select(user);
			
			UserBean userAux = new UserBean();
			userAux = userFind.get(0);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok get info");
			
			return Response.status(Status.OK).entity(userAux).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error get info");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	
	@DELETE
	@Path("{id}")
	public Response delete_user(@PathParam("id") Integer id) throws IOException {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 
			
			Dao<PreferenceBean, PreferenceBean> daoPreference = DaoFactory.getDao("Preference", "ar.edu.ubp.das");	 
			
			UserBean user = new UserBean();
			user.setIdUser(id);
			
			List<UserBean> userFind = dao.select(user);
			
			PreferenceBean pref = new PreferenceBean();
			
			if (userFind.get(0).getRole().equals("COMPANY")) {
				UserBean userDelete = new UserBean();
				userDelete.setCompany(userFind.get(0).getCompany());
				
				List<UserBean> userList = dao.select(userDelete);

				for (UserBean userToDelete : userList) {
					userToDelete.setIdUser(userToDelete.getIdUser());
					
					pref.setIdUser(userToDelete.getIdUser());
					daoPreference.delete(pref);
					this.logger.log(MyLogger.INFO, "PREFERENCE: Ok delete");
					
					this.elasticSearch = new ElasticSearch();
					elasticSearch.deleteByWebsiteId(userToDelete.getIdUser());
					
					dao.delete(userToDelete);
				}
				
				pref.setIdUser(userFind.get(0).getIdUser());
				daoPreference.delete(pref);
				this.logger.log(MyLogger.INFO, "PREFERENCE: Ok delete");
				
				dao.delete(user);
			} else {
				this.elasticSearch = new ElasticSearch();
				elasticSearch.deleteByWebsiteId(user.getIdUser());
				pref.setIdUser(user.getIdUser());
				daoPreference.delete(pref);
				this.logger.log(MyLogger.INFO, "PREFERENCE: Ok delete");
				
				dao.delete(user);
			}
			
			this.logger.log(MyLogger.INFO, "USERS: Ok delete id");
			
			return Response.status(Status.OK).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error delete id");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@PUT
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update(UserBean user) {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");
			
			
			Integer value = (Integer) request.getProperty("id");
			user.setIdUser(value);
			
			UserBean userUpdated = dao.update(user);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok put");
			
			return Response.status(Status.OK).entity(userUpdated).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error put");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	
	@PUT
	@Path("password")
	@Secured
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response update_password(UserBean user) {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");	 
			
			UserBean userToUpdate = new UserBean();

			userToUpdate.setUsername(user.getUsername());
			userToUpdate.setPassword(user.getPassword());
			
			UserBean userUpdated = dao.update(userToUpdate);
			
			this.logger.log(MyLogger.INFO, "USERS: Ok put password");

			return Response.status(Status.OK).entity(userUpdated).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error put password");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
	
	@POST
	@Path("impersonate/{id}")
	public Response login(@PathParam("id") Integer id) {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");
			
			UserBean userSearch = new UserBean();
			userSearch.setIdUser(id);
			
			List<UserBean> userFind = dao.select(userSearch);
			UserBean userAux = new UserBean();
			
			userAux.setIdUser((userFind.get(0)).getIdUser());
			userAux.setUsername((userFind.get(0)).getUsername());
			userAux.setRole((userFind.get(0)).getRole());
			

			String token = Jwts.builder().signWith(AuthenticationFilter.KEY).setSubject("user")
					.claim("id", userAux.getIdUser()).claim("username", userAux.getUsername())
					.claim("role", userAux.getRole()).compact();
			
			this.logger.log(MyLogger.INFO, "USERS: Ok post login");
			
			return Response.status(Status.OK).entity(token).build();

		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "USERS: Error post login");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}
