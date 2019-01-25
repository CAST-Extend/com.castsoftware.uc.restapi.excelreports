package com.castsoftware.uc.restapi.reports;

import java.util.HashMap;
import java.util.Map;


/**
 * Class representing the quality indicators the Rest API 
 * @author MMR
 *
 */
public class QualityIndicator {

	    /**
	     * Type
	     * possible values are business-criteria,technical-criteria,quality-rules,quality-measures,quality-distributions
	     */
		private int id;
		private String type;
		private String name;
		private String href;
		
		private int addedViolations;
		private int removedViolations;
		private int addedCriticalViolations;
		private int removedCriticalViolations;
		private int totalViolations;		
		private int totalCriticalViolations;
		
		private Map<Integer,WeightedQualityIndicator> mapSubQualityIndicator = new HashMap<Integer,WeightedQualityIndicator>();

		/////////////////////////////////////////////////////////////////////////////////////
		
		protected QualityIndicator() {
		}

		/////////////////////////////////////////////////////////////////////////////////////
		
		public void addSubQualityIndicator (int id, WeightedQualityIndicator subQualityIndicator) {
			this.mapSubQualityIndicator.put(id, subQualityIndicator);
		}
		
		public WeightedQualityIndicator getSubQualityIndicator (Integer id) {
			if (mapSubQualityIndicator == null)
				return null;
			return (WeightedQualityIndicator) this.mapSubQualityIndicator.get(id);
		}
		
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getHref() {
			return href;
		}

		public void setHref(String href) {
			this.href = href;
		}

		public Map<Integer,WeightedQualityIndicator> getMapSubQualityIndicator() {
			return mapSubQualityIndicator;
		}

		public void setMapSubQualityIndicator(Map<Integer,WeightedQualityIndicator> mapSubQualityIndicator) {
			this.mapSubQualityIndicator = mapSubQualityIndicator;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
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

		public int getTotalViolations() {
			return totalViolations;
		}

		public void setTotalViolations(int totalViolations) {
			this.totalViolations = totalViolations;
		}

		public int getTotalCriticalViolations() {
			return totalCriticalViolations;
		}

		public void setTotalCriticalViolations(int totalCriticalViolations) {
			this.totalCriticalViolations = totalCriticalViolations;
		}
	    
}
