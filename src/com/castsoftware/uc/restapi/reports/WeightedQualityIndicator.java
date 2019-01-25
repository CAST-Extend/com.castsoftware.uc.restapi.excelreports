package com.castsoftware.uc.restapi.reports;

public class WeightedQualityIndicator {
	
	private QualityIndicator subQualityIndicator;
	private Integer weight;
	private Boolean critical;

	public QualityIndicator getSubQualityIndicator() {
		return subQualityIndicator;
	}

	public void setSubQualityIndicator(QualityIndicator subQualityIndicator) {
		this.subQualityIndicator = subQualityIndicator;
	}
	
	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Boolean getCritical() {
		return critical;
	}

	public void setCritical(Boolean critical) {
		this.critical = critical;
	}
}
