package ar.edu.ubp.das.beans;

public class PreferenceBean {

	private Integer idPreference;
	private Integer idUser;
	private String color;
	private String iconUrl;
	private String search;
	private Double borderRadius;
	private Double borderWith;
	private Integer timeMetadata;
	private Integer iconSize;
	
	public Integer getIconSize() {
		return iconSize;
	}
	public void setIconSize(Integer iconSize) {
		this.iconSize = iconSize;
	}
	public Integer getIdPreference() {
		return idPreference;
	}
	public void setIdPreference(Integer idPreference) {
		this.idPreference = idPreference;
	}
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	public Double getBorderRadius() {
		return borderRadius;
	}
	public void setBorderRadius(Double borderRadius) {
		this.borderRadius = borderRadius;
	}
	public Double getBorderWith() {
		return borderWith;
	}
	public void setBorderWith(Double borderWith) {
		this.borderWith = borderWith;
	}
	public Integer getTimeMetadata() {
		return timeMetadata;
	}
	public void setTimeMetadata(Integer timeMetadata) {
		this.timeMetadata = timeMetadata;
	}
	
	
}