package com.castsoftware.uc.restapi.reports;

public class QualityRulesReportOutput implements Cloneable {
	private static final String OUTPUT_SEPARATOR = ";";
	
	private int metricId;
	private String metricName;
	// quality-rules / technical-criteria / quality-distributions
	private String type;
	private boolean critical;
	private Double grade = -1d;
	private int failedChecks = -1;
	private int successfulChecks = -1;
	private int totalChecks = -1;
	private Double complianceRatio = -1d;
	
	private Double threshold1 = -1d;
	private Double threshold2 = -1d;
	private Double threshold3 = -1d;
	private Double threshold4 = -1d;
	private SnapshotCharacteristics SnapshotCharacteristics;
	private String href;
	

	private int technicalCriterionId;
	private String technicalCriterionName;
	private int weight;
	private int businessCriterionId;
	private String businessCriterionName;
	private int weightTechnicalCriterion;
	
	private int addedViolations;
	private int removedViolations;
	private int addedCriticalViolations;
	private int removedCriticalViolations;
	private int totalCriticalViolations;
	private int totalViolations;	
	
	public int getMetricId() {
		return metricId;
	}
	public void setMetricId(int metricId) {
		this.metricId = metricId;
	}
	public String getMetricName() {
		return metricName;
	}
	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}
	public boolean isCritical() {
		return critical;
	}
	public void setCritical(boolean critical) {
		this.critical = critical;
	}
	public Double getGrade() {
		return grade;
	}
	public void setGrade(Double grade) {
		this.grade = grade;
	}
	public int getFailedChecks() {
		return failedChecks;
	}
	public void setFailedChecks(int failedChecks) {
		this.failedChecks = failedChecks;
	}
	public int getSuccessfulChecks() {
		return successfulChecks;
	}
	public void setSuccessfulChecks(int successfulChecks) {
		this.successfulChecks = successfulChecks;
	}
	public int getTotalChecks() {
		return totalChecks;
	}
	public void setTotalChecks(int totalChecks) {
		this.totalChecks = totalChecks;
	}
	public Double getComplianceRatio() {
		return complianceRatio;
	}
	public void setComplianceRatio(Double complianceRatio) {
		this.complianceRatio = complianceRatio;
	}
	public Double getThreshold1() {
		return threshold1;
	}
	public void setThreshold1(Double threshold1) {
		this.threshold1 = threshold1;
	}
	public Double getThreshold2() {
		return threshold2;
	}
	public void setThreshold2(Double threshold2) {
		this.threshold2 = threshold2;
	}
	public Double getThreshold3() {
		return threshold3;
	}
	public void setThreshold3(Double threshold3) {
		this.threshold3 = threshold3;
	}
	public Double getThreshold4() {
		return threshold4;
	}
	public void setThreshold4(Double threshold4) {
		this.threshold4 = threshold4;
	}
	public SnapshotCharacteristics getSnapshotCharacteristics() {
		return SnapshotCharacteristics;
	}
	public void setSnapshotCharacteristics(SnapshotCharacteristics snapshotCharacteristics) {
		SnapshotCharacteristics = snapshotCharacteristics;
	}
	public int getTechnicalCriterionId() {
		return technicalCriterionId;
	}
	public void setTechnicalCriterionId(int technicalCriterionId) {
		this.technicalCriterionId = technicalCriterionId;
	}
	public String getTechnicalCriterionName() {
		return technicalCriterionName;
	}
	public void setTechnicalCriterionName(String technicalCriterionName) {
		this.technicalCriterionName = technicalCriterionName;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getBusinessCriterionId() {
		return businessCriterionId;
	}
	public void setBusinessCriterionId(int businessCriterionId) {
		this.businessCriterionId = businessCriterionId;
	}
	public String getBusinessCriterionName() {
		return businessCriterionName;
	}
	public void setBusinessCriterionName(String businessCriterionName) {
		this.businessCriterionName = businessCriterionName;
	}
	public int getWeightTechnicalCriterion() {
		return weightTechnicalCriterion;
	}
	public void setWeightTechnicalCriterion(int weightTechnicalCriterion) {
		this.weightTechnicalCriterion = weightTechnicalCriterion;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getAddedViolations() {
		return addedViolations;
	}
	public void setAddedViolations(int addedViolations) {
		this.addedViolations = addedViolations;
	}
	public int getRemovedViolations() {
		return removedViolations;
	}
	public void setRemovedViolations(int removedViolations) {
		this.removedViolations = removedViolations;
	}
	public int getAddedCriticalViolations() {
		return addedCriticalViolations;
	}
	public void setAddedCriticalViolations(int addedCriticalViolations) {
		this.addedCriticalViolations = addedCriticalViolations;
	}
	public int getRemovedCriticalViolations() {
		return removedCriticalViolations;
	}
	public void setRemovedCriticalViolations(int removedCriticalViolations) {
		this.removedCriticalViolations = removedCriticalViolations;
	}
	public int getTotalCriticalViolations() {
		return totalCriticalViolations;
	}
	public void setTotalCriticalViolations(int totalCriticalViolations) {
		this.totalCriticalViolations = totalCriticalViolations;
	}
	public int getTotalViolations() {
		return totalViolations;
	}
	public void setTotalViolations(int totalViolations) {
		this.totalViolations = totalViolations;
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(SnapshotCharacteristics.getApplicationId());
		sb.append(OUTPUT_SEPARATOR);		
		sb.append(SnapshotCharacteristics.getApplicationName());
		sb.append(OUTPUT_SEPARATOR);
		sb.append(SnapshotCharacteristics.getId());		
		sb.append(OUTPUT_SEPARATOR);
		sb.append(SnapshotCharacteristics.getVersion());
		sb.append(OUTPUT_SEPARATOR);
		sb.append(type);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(metricId);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(metricName);
		return sb.toString();
	}	
	
	public Object clone() throws CloneNotSupportedException{  
		return super.clone();  
	}  
	
}

