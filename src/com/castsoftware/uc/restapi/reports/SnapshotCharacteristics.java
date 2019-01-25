package com.castsoftware.uc.restapi.reports;

public class SnapshotCharacteristics {
	private String id;
	private String adglocalId;
	private String number;
	private String version; 
	private String isoDate;
	private Long time;
	private String href;	
	private String applicationId;
	private String domain;
	private String applicationName;
	
	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAdglocalId() {
		return adglocalId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIsoDate() {
		return isoDate;
	}

	public void setIsoDate(String isoDate) {
		this.isoDate = isoDate;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(version).append("#").append(isoDate).append("#").append(href);
		return sb.toString();
	}
	
}
