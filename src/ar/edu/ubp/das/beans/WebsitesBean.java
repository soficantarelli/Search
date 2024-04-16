package ar.edu.ubp.das.beans;

public class WebsitesBean {
	private Integer idWebSite;
	private Integer idUser;
	private String url;
	private Integer idService;
	private Boolean indexed;
	private Boolean reindex;
	private Boolean up;
	
	public Integer getIdWebSite() {
		return idWebSite;
	}
	public void setIdWebSite(Integer idWebSite) {
		this.idWebSite = idWebSite;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getIdService() {
		return idService;
	}
	public void setIdService(Integer idService) {
		this.idService = idService;
	}
	public Boolean getIndexed() {
		return indexed;
	}
	public void setIndexed(Boolean indexed) {
		this.indexed = indexed;
	}
	public Boolean getReindex() {
		return reindex;
	}
	public void setReindex(Boolean reindex) {
		this.reindex = reindex;
	}
	public Boolean getUp() {
		return up;
	}
	public void setUp(Boolean up) {
		this.up = up;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

}