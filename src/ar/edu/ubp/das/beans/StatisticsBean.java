package ar.edu.ubp.das.beans;

public class StatisticsBean {
	private Integer idUser;
	private Integer total;
	private Integer withResult;
	private Integer withoutResult;
	private Integer falling;
	private Integer upward;
	private Integer dateStat;
	private Integer today;
	private Integer popularity;
	
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	public Integer getWithResult() {
		return withResult;
	}
	public void setWithResult(Integer withResult) {
		this.withResult = withResult;
	}
	public Integer getWithoutResult() {
		return withoutResult;
	}
	public void setWithoutResult(Integer withoutResult) {
		this.withoutResult = withoutResult;
	}
	public Integer getFalling() {
		return falling;
	}
	public void setFalling(Integer falling) {
		this.falling = falling;
	}
	public Integer getUpward() {
		return upward;
	}
	public void setUpward(Integer upward) {
		this.upward = upward;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public Integer getToday() {
		return today;
	}
	public void setToday(Integer today) {
		this.today = today;
	}
	public Integer getPopularity() {
		return popularity;
	}
	public void setPopularity(Integer popularity) {
		this.popularity = popularity;
	}
	public Integer getDateStat() {
		return dateStat;
	}
	public void setDateStat(Integer dateStat) {
		this.dateStat = dateStat;
	}
}