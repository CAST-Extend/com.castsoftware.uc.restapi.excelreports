package com.castsoftware.uc.restapi.reports;

public class ApplicationCharacteristics {
	
	private String domain;
	private String name;
	private String id;
	private String href;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(domain).append("#").append(id).append("#").append(name).append("#").append(href);
		return sb.toString();
	}
	
}
