package ar.edu.ubp.das.beans;

import java.util.List;

public class MetadataBean {
	private String id;
	private Integer userId;
	private Integer websiteId;
	private String url;
	private String type;
	private String title;
	private String text;
	private Integer textLength;
	private Boolean approved;
	private Integer visitors;
	private List<String> topWords;
	private String[] tags;
	private String[] filters;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getWebsiteId() {
		return websiteId;
	}
	public void setWebsiteId(Integer websiteId) {
		this.websiteId = websiteId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Integer getTextLength() {
		return textLength;
	}
	public void setTextLength(Integer textLength) {
		this.textLength = textLength;
	}
	public Boolean getApproved() {
		return approved;
	}
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	public Integer getVisitors() {
		return visitors;
	}
	public void setVisitors(Integer visitors) {
		this.visitors = visitors;
	}
	public List<String> getTopWords() {
		return topWords;
	}
	public void setTopWords(List<String> topWords) {
		this.topWords = topWords;
	}
	public String[] getTags() {
		return tags;
	}
	public void setTags(String[] tags) {
		this.tags = tags;
	}
	public String[] getFilters() {
		return filters;
	}
	public void setFilters(String[] filters) {
		this.filters = filters;
	}
}
