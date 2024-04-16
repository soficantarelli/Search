package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.SearchBean;
import ar.edu.ubp.das.db.Dao;

public class MSSearchDao extends Dao<SearchBean,SearchBean > {

	@Override
	public SearchBean delete(SearchBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchBean insert(SearchBean search) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.new_search(?,?,?,?,?,?,?,?)");
			this.setParameter(1, search.getIdUser());
			this.setParameter(2, search.getQuery());
			this.setParameter(3, search.getResults());
			this.setParameter(4, search.getSortBy());
			this.setParameter(5, search.getOrderBy());
			this.setParameter(6, search.getDateFrom());
			this.setParameter(7, search.getDateTo());
			if (search.getPopularity() != null) {
				this.setParameter(8, search.getPopularity());
			} else {
				this.setParameter(8, -1);
			}
			

			this.executeUpdate();
		} catch(SQLException e) {
			throw e;
		} finally {
			this.close();
		}
		return search;
	}

	@Override
	public SearchBean make(ResultSet result) throws SQLException {
		SearchBean search = new SearchBean();
		search.setQuery(result.getString("query"));
		search.setDate(result.getString("date"));
		search.setResults(result.getLong("results"));
		search.setSortBy(result.getString("sortBy"));
		search.setOrderBy(result.getString("orderBy"));
		search.setPopularity(result.getInt("popularity"));
		search.setType(result.getString("type"));

		return search;
	}

	@Override
	public List<SearchBean> select(SearchBean user) throws SQLException {
		try {
			this.connect();
			if (user != null) {
				this.setProcedure("dbo.get_search(?)");
				this.setParameter(1, user.getIdUser());
				List<SearchBean> usersFind = this.executeQuery();
		        return usersFind;
			} else {
				this.setProcedure("dbo.get_search_all");
				List<SearchBean> usersFind = this.executeQuery();
		        return usersFind;
			}
		} finally {
			this.close();
		}
	}

	@Override
	public SearchBean update(SearchBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean valid(SearchBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
