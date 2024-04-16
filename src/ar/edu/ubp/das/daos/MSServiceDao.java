package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.ServicesBean;
import ar.edu.ubp.das.db.Dao;

public class MSServiceDao extends Dao<ServicesBean, ServicesBean>{

	@Override
	public ServicesBean delete(ServicesBean service) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.delete_service(?)");
			this.setParameter(1, service.getIdService());
			this.executeQuery();
			return service;
		} finally {
			this.close();
		}
	}

	@Override
	public ServicesBean insert(ServicesBean service) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.services_new(?,?,?,?)");
			this.setParameter(1, service.getUrl());
			this.setParameter(2, service.getIdUser());
			this.setParameter(3, service.getProtocol());
			this.setParameter(4, service.getToken());
			this.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			this.close();
		}
		return service;
	}

	@Override
	public ServicesBean make(ResultSet result) throws SQLException {
		ServicesBean service = new ServicesBean();
		service.setIdService(result.getInt("idService"));
		service.setIdUser(result.getInt("idUser"));
		service.setUrl(result.getString("url"));
		service.setProtocol(result.getString("protocol"));
		service.setIndexed(result.getBoolean("indexed"));
		service.setReindex(result.getBoolean("reindex"));
		service.setUp(result.getBoolean("up"));
		service.setToken(result.getString("token"));
		return service;
	}

	@Override
	public List<ServicesBean> select(ServicesBean service) throws SQLException {
		try {
			this.connect();
			if (service != null) {
				if (service.getIdUser() != null) {
					this.setProcedure("dbo.get_services_by_user(?)");
					this.setParameter(1, service.getIdUser());
					List<ServicesBean> serviceFind = this.executeQuery();
			        return serviceFind;
				} else {
					this.setProcedure("dbo.get_service(?)");
					this.setParameter(1, service.getIdService());
					List<ServicesBean> serviceFind = this.executeQuery();
			        return serviceFind;
				}
			} else {
				this.setProcedure("dbo.get_all_services");
				List<ServicesBean> serviceFind = this.executeQuery();
		        return serviceFind;
			}
		} finally {
			this.close();
		}
	}

	@Override
	public ServicesBean update(ServicesBean service) throws SQLException {
		try {
			this.connect();
			if (service.getIdUser() != null) {
				System.out.print("enrte");
				this.setProcedure("dbo.update_service(?,?,?,?)");
				this.setParameter(1, service.getIdService());
				this.setParameter(2, service.getUrl());
				this.setParameter(3, service.getProtocol());
				this.setParameter(4, service.getIdUser());
				
				this.executeQuery();
			} else if (service.getReindex() != null){
				this.setProcedure("dbo.reindex_service(?,?)");
				this.setParameter(1, service.getIdService());
				this.setParameter(2, service.getReindex());
				this.executeQuery();
			} else {
				this.setProcedure("dbo.up_service(?,?)");
				this.setParameter(1, service.getIdService());
				this.setParameter(2, service.getUp());
				this.executeQuery();
			}
			
		} finally {
			this.close();
		}
		return service;
	}

	@Override
	public boolean valid(ServicesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
