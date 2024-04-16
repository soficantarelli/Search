package ar.edu.ubp.das.beans;

public class ServicesBean {
	private Integer idService;
	private Integer idUser;
	private String url;
	private String protocol;
	private Boolean indexed;
	private Boolean reindex;
	private Boolean up;
	private String token;
	
	public Integer getIdService() {
		return idService;
	}
	public void setIdService(Integer idService) {
		this.idService = idService;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
