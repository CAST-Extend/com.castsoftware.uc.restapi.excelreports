package com.castsoftware.uc.restapi.reports;

public class CentralSnapshotMetrics {
	private String applicationName = null;
	private Integer applicationId = -1;
	private Integer snapshotId = -1;
	private Long snapshotTime = -1l;
	private String snapshotVersion = null;
	private String isoDate = null;

	private Integer addedArtifacts = -1;
	private Integer modifiedArtifacts = -1;
	private Integer deletedArtifacts = -1;
	private Integer loc = -1;
	
	public Integer getSnapshotId() {
		return snapshotId;
	}
	public void setSnapshotId(Integer snapshotId) {
		this.snapshotId = snapshotId;
	}
	public Integer getAddedArtifacts() {
		return addedArtifacts;
	}
	public void setAddedArtifacts(Integer addedArtifacts) {
		this.addedArtifacts = addedArtifacts;
	}
	public Integer getModifiedArtifacts() {
		return modifiedArtifacts;
	}
	public void setModifiedArtifacts(Integer modifiedArtifacts) {
		this.modifiedArtifacts = modifiedArtifacts;
	}
	public Integer getDeletedArtifacts() {
		return deletedArtifacts;
	}
	public void setDeletedArtifacts(Integer deletedArtifacts) {
		this.deletedArtifacts = deletedArtifacts;
	}
	public String getApplicationName() {
		return applicationName;
	}
	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}
	public Integer getLoc() {
		return loc;
	}
	public void setLoc(Integer loc) {
		this.loc = loc;
	}
	public Integer getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}
	public Long getSnapshotTime() {
		return snapshotTime;
	}
	public void setSnapshotTime(Long snapshotTime) {
		this.snapshotTime = snapshotTime;
	}
	public String getSnapshotVersion() {
		return snapshotVersion;
	}
	public void setSnapshotVersion(String snapshotVersion) {
		this.snapshotVersion = snapshotVersion;
	}
	
	public String getIsoDate() {
		return isoDate;
	}
	public void setIsoDate(String isoDate) {
		this.isoDate = isoDate;
	}
	
}
