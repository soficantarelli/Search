package ar.edu.ubp.das.resources;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import ar.edu.ubp.das.beans.StatisticsBean;
import ar.edu.ubp.das.beans.UserBean;
import ar.edu.ubp.das.db.Dao;
import ar.edu.ubp.das.db.DaoFactory;
import ar.edu.ubp.das.logger.MyLogger;
import ar.edu.ubp.das.security.Secured;

@Path("statistics")
public class StatisticsResource {

	@Context
	ContainerRequestContext request;
	
private MyLogger logger;
	
	public StatisticsResource() {
		this.logger = new MyLogger(this.getClass().getSimpleName());
	}
	
	@GET
	@Path("ping")
	public Response ping() {
		this.logger.log(MyLogger.INFO, "STATISTICS: Ok ping");
		return Response.status(Status.OK).entity("pong!").build();
	}
	
	@GET
	@Path("by-user")
	@Secured
	public Response get_statistics() {
		try {
			Dao<StatisticsBean, StatisticsBean> dao = DaoFactory.getDao("Statistics", "ar.edu.ubp.das");
			
			StatisticsBean statistic = new StatisticsBean();
			statistic.setIdUser((Integer)request.getProperty("id"));	
			
			List<StatisticsBean> statistics = dao.select(statistic);
			
			this.logger.log(MyLogger.INFO, "STATISTICS: Ok get by user");
			
			return Response.status(Status.OK).entity(statistics).build();
		} catch (SQLException e) {
			this.logger.log(MyLogger.INFO, "STATISTICS: Error get by user");
			return Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		}
	}
}