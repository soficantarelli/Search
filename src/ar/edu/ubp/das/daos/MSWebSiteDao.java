package ar.edu.ubp.das.daos;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import ar.edu.ubp.das.beans.WebsitesBean;
import ar.edu.ubp.das.db.Dao;

public class MSWebSiteDao extends Dao<WebsitesBean, WebsitesBean>{

	@Override
	public WebsitesBean delete(WebsitesBean website) throws SQLException {
		try {
			this.connect();
			if (website.getIdWebSite() != null) {
				this.setProcedure("dbo.delete_website(?)");
				this.setParameter(1, website.getIdWebSite());
				this.executeQuery();
			} else {
				this.setProcedure("dbo.delete_website_by_service(?)");
				this.setParameter(1, website.getIdService());
				this.executeQuery();
			}
			return website;
		} finally {
			this.close();
		}
	}

	@Override
	public WebsitesBean insert(WebsitesBean website) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.new_website(?,?)");
			this.setParameter(1, website.getUrl());
			this.setParameter(2, website.getIdUser());
			this.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			this.close();
		}
		return website;
	}

	@Override
	public WebsitesBean make(ResultSet result) throws SQLException {
		WebsitesBean website = new WebsitesBean();
		website.setIdUser(result.getInt("idUser"));
		website.setUrl(result.getString("url"));
		website.setIdService(result.getInt("idService"));
		website.setReindex(result.getBoolean("reindex"));
		website.setIndexed(result.getBoolean("indexed"));
		website.setUp(result.getBoolean("up"));
		website.setIdWebSite(result.getInt("idWebSite"));
		return website;
	}

	@Override
	public List<WebsitesBean> select(WebsitesBean website) throws SQLException {
		try {
			this.connect();
			if (website != null) {
				if (website.getIdUser() != null) {
					this.setProcedure("dbo.get_websites_by_user(?)");
					this.setParameter(1, website.getIdUser());
					List<WebsitesBean> websitesFind = this.executeQuery();
			        return websitesFind;
				} else if (website.getIdService() != null) {
					this.setProcedure("dbo.get_websites_by_service(?)");
					this.setParameter(1, website.getIdService());
					List<WebsitesBean> websitesFind = this.executeQuery();
			        return websitesFind;
				} else {
					this.setProcedure("dbo.get_website_by_id(?)");
					this.setParameter(1, website.getIdWebSite());
					List<WebsitesBean> websitesFind = this.executeQuery();
			        return websitesFind;
				}
			} else {
				this.setProcedure("dbo.get_websites");
				List<WebsitesBean> websitesFind = this.executeQuery();
		        return websitesFind;
			}
		} finally {
			this.close();
		}
	}

	@Override
	public WebsitesBean update(WebsitesBean website) throws SQLException {
		try {
			this.connect();
			if (website.getUrl() != null) {
				this.setProcedure("dbo.update_website(?,?)");
				this.setParameter(1, website.getIdWebSite());
				this.setParameter(2, website.getUrl());
				this.executeQuery();
			} else {
				if (website.getUp() != null) {
					this.setProcedure("dbo.up_website(?,?)");
					this.setParameter(1, website.getIdWebSite());
					this.setParameter(2, website.getUp());
					this.executeQuery();
				} else if (website.getIdService() != null){
					this.setProcedure("dbo.update_website_service(?)");
					this.setParameter(1, website.getIdService());
					this.executeQuery();
				} else {
					this.setProcedure("dbo.reindex_website(?)");
					this.setParameter(1, website.getIdWebSite());
					this.executeQuery();
				}
			}
			
		} finally {
			this.close();
		}
		return website;
	}

	@Override
	public boolean valid(WebsitesBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}