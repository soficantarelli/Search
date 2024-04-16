package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.PreferenceBean;
import ar.edu.ubp.das.db.Dao;

public class MSPreferenceDao extends Dao<PreferenceBean, PreferenceBean>{


	@Override
	public PreferenceBean make(ResultSet result) throws SQLException {
		PreferenceBean preference = new PreferenceBean();
		preference.setIdUser(result.getInt("idUser"));
		preference.setColor(result.getString("color"));
		preference.setIconUrl(result.getString("iconUrl"));
		preference.setSearch(result.getString("search"));
		preference.setBorderRadius(result.getDouble("borderRadius"));
		preference.setBorderWith(result.getDouble("borderWith"));
		preference.setTimeMetadata(result.getInt("timeMetadata"));
		preference.setIconSize(result.getInt("iconSize"));
		return preference;
	}

	@Override
	public List<PreferenceBean> select(PreferenceBean preference) throws SQLException {
		try {
			if (preference == null) {
				this.connect();
				this.setProcedure("dbo.get_preference_admin");
				return this.executeQuery();
			} else {
				this.connect();
				this.setProcedure("dbo.get_preference_user(?)");
				System.out.println("dbo.get_preference_user(?)" + preference.getIdUser());
				this.setParameter(1, preference.getIdUser());
				return this.executeQuery();
			}
		} finally {
			this.close();
		}
	}

	@Override
	public PreferenceBean update(PreferenceBean preference) throws SQLException {
		try {
			this.connect();
			System.out.println(preference.getBorderRadius());
			this.setProcedure("dbo.update_preference(?,?,?,?,?,?,?,?)");
			this.setParameter(1, preference.getIdUser());
			this.setParameter(2, preference.getColor());
			this.setParameter(3, preference.getIconUrl());
			this.setParameter(4, preference.getSearch());
			this.setParameter(5, preference.getBorderRadius());
			this.setParameter(6, preference.getBorderWith());
			this.setParameter(7, preference.getTimeMetadata());
			this.setParameter(8, preference.getIconSize());
			this.executeQuery();
		} finally {
			this.close();
		}
		return preference;
	}

	@Override
	public PreferenceBean delete(PreferenceBean preference) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.delete_preference(?)");
			this.setParameter(1, preference.getIdUser());
			this.executeQuery();
			return preference;
		} finally {
			this.close();
		}
	}

	@Override
	public PreferenceBean insert(PreferenceBean pre) throws SQLException {
		try {
			this.connect();
				this.setProcedure("dbo.new_preference(?)");
				this.setParameter(1, pre.getIdUser());
			this.executeUpdate();
		} catch(SQLException e) {
			System.out.print(e);
			throw e;
		} finally {
			this.close();
		}
		return pre;	
	}

	@Override
	public boolean valid(PreferenceBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	
}