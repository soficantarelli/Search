package ar.edu.ubp.das.resources;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.edu.ubp.das.beans.SearchBean;
import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.elasticsearch.ElasticSearch;
import ar.edu.ubp.das.logger.MyLogger;

@Path("search")
public class SearchResource {
	@Context
	ContainerRequestContext request;
	
	private MyLogger logger;
	
	public SearchResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "SEARCH: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@Path("{token}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)	
	public Response search(@PathParam("token") String token, SearchBean search) throws Exception {
		try {
			Dao<UserBean, UserBean> dao = DaoFactory.getDao("Users", "ar.edu.ubp.das");
			
			UserBean user = new UserBean();
			user.setToken(token);
			
			List<UserBean> userFind = dao.select(user);
			System.out.print(userFind.get(0).getIdUser());
			search.setIdUser(userFind.get(0).getIdUser());
			
			ElasticSearch elasticSearch = new ElasticSearch();
			
			this.logger.log(MyLogger.INFO, "SEARCH: Ok search");
			
			return Response.status(Status.OK).entity(elasticSearch.search(search)).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.ERROR, "SEARCH: Error search");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}

	@Path("popular/{id}")
	@POST
	public Response increasePopularity(@PathParam("id") String id) {
		try {
			System.out.println("POPULARITY" + id);
			ElasticSearch elastic = new ElasticSearch();
			elastic.increasePopularity(id);
			
			this.logger.log(MyLogger.INFO, "SEARCH: increse popularity");
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			this.logger.log(MyLogger.ERROR, "SEARCH: Error increse popularity");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}
