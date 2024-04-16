package ar.edu.ubp.das.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import ar.edu.ubp.das.beans.StatisticsBean;
import ar.edu.ubp.das.db.Dao;

public class MSStatisticsDao extends Dao<StatisticsBean, StatisticsBean>{

	@Override
	public StatisticsBean delete(StatisticsBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StatisticsBean insert(StatisticsBean arg0) throws SQLException {
		// TODO Auto-generated method stub
				return null;
	}
	@Override
	public StatisticsBean make(ResultSet result) throws SQLException {
		StatisticsBean statistic = new StatisticsBean();
		statistic.setIdUser(result.getInt("idUser"));
		statistic.setTotal(result.getInt("total"));
		statistic.setWithResult(result.getInt("withResult"));
		statistic.setWithoutResult(result.getInt("withoutResult"));
		statistic.setFalling(result.getInt("falling"));
		statistic.setUpward(result.getInt("upward"));
		statistic.setDateStat(result.getInt("dateStat"));
		statistic.setToday(result.getInt("today"));
		statistic.setPopularity(result.getInt("popularity"));
		
		return statistic;
	}
	
	@Override
	public List<StatisticsBean> select(StatisticsBean statistics) throws SQLException {
		try {
			this.connect();
			this.setProcedure("dbo.get_statistics(?)");
			this.setParameter(1, statistics.getIdUser());
			List<StatisticsBean> statisticsFind = this.executeQuery();
	        return statisticsFind;
		} finally {
			this.close();
		}
	}
	
	@Override
	public StatisticsBean update(StatisticsBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean valid(StatisticsBean arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}