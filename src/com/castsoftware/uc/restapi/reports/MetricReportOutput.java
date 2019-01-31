package com.castsoftware.uc.restapi.reports;

public class MetricReportOutput {
	
	private static final String OUTPUT_SEPARATOR = ";";
	

	
	private String reportType = null;
	
	private MetricReportOutput() {
		super();
	}
	

	public MetricReportOutput(String reportType) {
		super();
		this.reportType = reportType;
	}
	
	private String applicationId;
	private String applicationName;
	private String snapshotHref;
	private String snapshotVersion;
	private String snapshotId;
	private Long snapshotTime;
	private String isoDate;
	private String domain;

	////////////////////////////////////////////////////////////////////////////////////

	// Functional weight	
	private Integer nbAFPTotalFP = 0;
	private Integer nbAFPDatafunctionsFP = 0;
	private Integer nbAFPTransactionsFP  = 0;	
	
	private Integer nbAEPTotal  = 0;
	private Integer nbAEPAdded  = 0;
	private Integer nbAEPDeleted = 0;
	private Integer nbAEPModifed = 0;

	// Added Critical violations on new and modified code / AEP
	private Double addedCVOnNewCodePerAEP = 0.0;
	// Critical violations / AFP
	private Double CVPerAFP = 0.0;	
	
	private Double nbImplementationPointsAEFP = 0.0;      
	private Double nbImplementationPointsAETP = 0.0;                	
	private Integer nbAEFPAdded  = 0;                	
	private Integer nbAEFPAddedDataFunctions = 0;                	
	private Integer nbAEFPAddedTransactionalFunctions = 0;                	
	private Integer nbAEFPDeleted  = 0;        
	private Integer nbAEFPDeletedDataFunctions = 0;             	
	private Integer nbAEFPDeletedTransactionalFunctions = 0;             	
	private Integer nbAEFPModified = 0; 
	private Integer nbAEFPModifiedDataFunctions = 0;             	
	private Integer nbAEFPModifiedTransactionalFunctions = 0;             	
	private Integer nbAEFP = 0;                  	
	private Integer nbAEFPDataFunctions = 0;             	
	private Integer nbAEFPTransactionalFunctions = 0;             	
	
	private Integer nbAETP = 0;             	
	private Integer nbAETPAdded = 0;             	
	private Integer nbAETPDeleted = 0;
	private Integer nbAETPModified = 0;
	
	private Integer nbEvovedTransactions = 0;
	private Integer nbTransactions = 0;  
	private Integer nbEnhancementSharedArtifacts = 0;  
	private Integer nbEnhancementSpecificArtifacts = 0;
	private Integer effortComplexity = 0;
	private Integer equivalentRatio = 0;             	

	private Integer nbDecisionsPoints = 0;                	
	private Double nbBFP = 0.0;  	
	
	// EFP metrics
	private Integer nbEFPTotalAdded = 0;
	private Integer nbEFPDataFPAdded = 0;
	private Integer nbEFPTransactionalFPAdded = 0;
	private Integer nbEFPTotalModified = 0;
	private Integer nbEFPDataFPModified = 0;
	private Integer nbEFPTransactionalFPModified = 0;
	private Integer nbEFPTotalDeleted = 0;
	private Integer nbEFPDataFPDeleted = 0;
	private Integer nbEFPTransactionalFPDeleted = 0;	
	private Integer nbEFPTotalUnchanged = 0;
	private Integer nbEFPDataFPUnchanged = 0;
	private Integer nbEFPTransactionalFPUnchanged = 0;	
	private Integer nbEFPTotal = 0;	
	private Integer nbEFPDataFP = 0;	
	private Integer nbEFPTransactionalFP = 0;		

	
	////////////////////////////////////////////////////////////////////////////////////
	
	// Business criterias	
	private Double bcTQI = 0.0;
	private Double bcEffi= 0.0;
	private Double bcRobus= 0.0;
	private Double bcSecu= 0.0;
	private Double bcChang= 0.0;
	private Double bcTrans= 0.0;
	private Double bcCodinBP= 0.0; // programming practices

	private Double bcApplicationOp= 0.0; // average Effic / Secu / Robus
	private Double bcCodeMaintain= 0.0; // average Chang / Trans
	
	//////////////////////////////////////////////////////////////////////////////
	
	// Technical size
	private Integer loc = 0;
	
	// Comment lines
	private Integer nbCommentLines = 0;                	
	private Integer nbCommentedOutLines = 0;
	private Integer nbArtifacts = 0;                	
	private Integer nbFiles = 0;
	private Integer nbPrograms = 0;                	
	private Integer nbClasses = 0;
	private Integer nbMethods = 0;
	private Integer nbPackages = 0;
	private Integer nbTables = 0;
	private Integer nbFuncProc = 0;    
	private Integer nbSQLArtifacts = 0;
	
	private Integer nbInterfaces = 0;
	private Integer nbIncludes = 0;
	private Integer nbFunctionPools = 0;
	private Integer nbABAPUserExits = 0;
	private Integer nbABAPTransactions = 0;
	
	// metrics coming from the central schema
	private Integer nbArtifactsAdded = -1;
	private Integer nbArtifactsDeleted = -1;
	private Integer nbArtifactsModified = -1;	
	private Integer locCentral = -1;	
	
	////////////////////////////////////////////////////////////////////////
	
	private Integer totalCritViolations = 0;   
	private Integer addedCritViolations = 0;
	private Integer removedCritViolations = 0;
	private Integer critViolationsInNewAndModifiedCode = 0;
	
	private Integer totalViolations = 0;   
	private Integer addedViolations = 0;
	private Integer removedViolations = 0;
	private Integer violationsInNewAndModifiedCode = 0;
	
	///////////////////////////////////////////////////////////////////////
	// Action plan 
	private Integer addedActionPlanItems = -1;   
	private Integer pendingActionPlanItems = -1;
	private Integer solvedActionPlanItems = -1;
	private Integer addedActionPlanLowComItems = -1;
	private Integer addedActionPlanAvgComItems = -1;
	private Integer addedActionPlanHigComItems = -1;	
	private Integer addedActionPlanExtComItems = -1;
	private Integer pendingActionPlanLowComItems = -1;
	private Integer pendingActionPlanAvgComItems = -1;
	private Integer pendingActionPlanHigComItems = -1;	
	private Integer pendingActionPlanExtComItems = -1;	
	private Integer solvedActionPlanLowComItems = -1;
	private Integer solvedActionPlanAvgComItems = -1;
	private Integer solvedActionPlanHigComItems = -1;	
	private Integer solvedActionPlanExtComItems = -1;	
	///////////////////////////////////////////////////////////////////////
	
	public String getApplicationName() {
		return applicationName;
	}


	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getSnapshotVersion() {
		return snapshotVersion;
	}


	public void setSnapshotVersion(String snapshotVersion) {
		this.snapshotVersion = snapshotVersion;
	}

	public String getSnapshotHref() {
		return snapshotHref;
	}



	public void setSnapshotHref(String snapshotHref) {
		this.snapshotHref = snapshotHref;
	}



	public int getNbAEPTotal() {
		return nbAEPTotal;
	}



	public void setNbAEPTotal(int nbAEPTotal) {
		this.nbAEPTotal = nbAEPTotal;
	}



	public int getNbAEPAdded() {
		return nbAEPAdded;
	}



	public void setNbAEPAdded(int nbAEPAdded) {
		this.nbAEPAdded = nbAEPAdded;
	}



	public int getNbAEPDeleted() {
		return nbAEPDeleted;
	}



	public void setNbAEPDeleted(int nbAEPDeleted) {
		this.nbAEPDeleted = nbAEPDeleted;
	}



	public int getNbAEPModifed() {
		return nbAEPModifed;
	}



	public void setNbAEPModifed(int nbAEPModifed) {
		this.nbAEPModifed = nbAEPModifed;
	}



	public Double getBcTQI() {
		return bcTQI;
	}



	public void setBcTQI(Double bcTQI) {
		this.bcTQI = bcTQI;
	}



	public Double getBcEffi() {
		return bcEffi;
	}



	public void setBcEffi(Double bcEffi) {
		this.bcEffi = bcEffi;
	}



	public Double getBcRobus() {
		return bcRobus;
	}



	public void setBcRobus(Double bcRobus) {
		this.bcRobus = bcRobus;
	}



	public Double getBcSecu() {
		return bcSecu;
	}



	public void setBcSecu(Double bcSecu) {
		this.bcSecu = bcSecu;
	}



	public Double getBcChang() {
		return bcChang;
	}



	public void setBcChang(Double bcChang) {
		this.bcChang = bcChang;
	}



	public Double getBcCodinBP() {
		return bcCodinBP;
	}



	public void setBcCodinBP(Double bcCodinBP) {
		this.bcCodinBP = bcCodinBP;
	}



	public Double getBcApplicationOp() {
		return bcApplicationOp;
	}



	public void setBcApplicationOp(Double bcApplicationOp) {
		this.bcApplicationOp = bcApplicationOp;
	}



	public Double getBcCodeMaintain() {
		return bcCodeMaintain;
	}



	public void setBcCodeMaintain(Double bcCodeMaintain) {
		this.bcCodeMaintain = bcCodeMaintain;
	}


	public Double getBcTrans() {
		return bcTrans;
	}


	public void setBcTrans(Double bcTrans) {
		this.bcTrans = bcTrans;
	}
	

	public int getNbAFPTotalFP() {
		return nbAFPTotalFP;
	}

	public void setNbAFPTotalFP(int nbAFPTotalFP) {
		this.nbAFPTotalFP = nbAFPTotalFP;
	}

	public int getNbAFPDatafunctionsFP() {
		return nbAFPDatafunctionsFP;
	}

	public void setNbAFPDatafunctionsFP(int nbAFPDatafunctionsFP) {
		this.nbAFPDatafunctionsFP = nbAFPDatafunctionsFP;
	}

	public int getNbAFPTransactionsFP() {
		return nbAFPTransactionsFP;
	}

	public void setNbAFPTransactionsFP(int nbAFPTransactionsFP) {
		this.nbAFPTransactionsFP = nbAFPTransactionsFP;
	}
	
	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	
	public int getNbCommentLines() {
		return nbCommentLines;
	}



	public void setNbCommentLines(int nbCommentLines) {
		this.nbCommentLines = nbCommentLines;
	}



	public int getNbCommentedOutLines() {
		return nbCommentedOutLines;
	}



	public void setNbCommentedOutLines(int nbCommentedOutLines) {
		this.nbCommentedOutLines = nbCommentedOutLines;
	}



	public int getNbArtifacts() {
		return nbArtifacts;
	}



	public void setNbArtifacts(int nbArtifacts) {
		this.nbArtifacts = nbArtifacts;
	}



	public int getNbFiles() {
		return nbFiles;
	}



	public void setNbFiles(int nbFiles) {
		this.nbFiles = nbFiles;
	}



	public int getNbPrograms() {
		return nbPrograms;
	}



	public void setNbPrograms(int nbPrograms) {
		this.nbPrograms = nbPrograms;
	}



	public int getNbClasses() {
		return nbClasses;
	}



	public void setNbClasses(int nbClasses) {
		this.nbClasses = nbClasses;
	}



	public int getNbMethods() {
		return nbMethods;
	}



	public void setNbMethods(int nbMethods) {
		this.nbMethods = nbMethods;
	}



	public int getNbPackages() {
		return nbPackages;
	}



	public void setNbPackages(int nbPackages) {
		this.nbPackages = nbPackages;
	}



	public int getNbTables() {
		return nbTables;
	}



	public void setNbTables(int nbTables) {
		this.nbTables = nbTables;
	}



	public int getNbFuncProc() {
		return nbFuncProc;
	}



	public void setNbFuncProc(int nbFuncProc) {
		this.nbFuncProc = nbFuncProc;
	}



	public int getNbSQLArtifacts() {
		return nbSQLArtifacts;
	}



	public void setNbSQLArtifacts(int nbSQLArtifacts) {
		this.nbSQLArtifacts = nbSQLArtifacts;
	}



	public Double getNbImplementationPointsAEFP() {
		return nbImplementationPointsAEFP;
	}



	public void setNbImplementationPointsAEFP(Double nbImplementationPointsAEFP) {
		this.nbImplementationPointsAEFP = nbImplementationPointsAEFP;
	}



	public Double getNbImplementationPointsAETP() {
		return nbImplementationPointsAETP;
	}



	public void setNbImplementationPointsAETP(Double nbImplementationPointsAETP) {
		this.nbImplementationPointsAETP = nbImplementationPointsAETP;
	}



	public int getNbAEFPAdded() {
		return nbAEFPAdded;
	}



	public Integer getAddedActionPlanItems() {
		return addedActionPlanItems;
	}



	public void setAddedActionPlanItems(Integer addedActionPlanItems) {
		this.addedActionPlanItems = addedActionPlanItems;
	}



	public Integer getPendingActionPlanItems() {
		return pendingActionPlanItems;
	}



	public void setPendingActionPlanItems(Integer pendingActionPlanItems) {
		this.pendingActionPlanItems = pendingActionPlanItems;
	}



	public void setNbAEFPAdded(int nbAEFPAdded) {
		this.nbAEFPAdded = nbAEFPAdded;
	}



	public int getNbAEFPAddedDataFunctions() {
		return nbAEFPAddedDataFunctions;
	}



	public void setNbAEFPAddedDataFunctions(int nbAEFPAddedDataFunctions) {
		this.nbAEFPAddedDataFunctions = nbAEFPAddedDataFunctions;
	}



	public int getNbAEFPAddedTransactionalFunctions() {
		return nbAEFPAddedTransactionalFunctions;
	}



	public void setNbAEFPAddedTransactionalFunctions(int nbAEFPAddedTransactionalFunctions) {
		this.nbAEFPAddedTransactionalFunctions = nbAEFPAddedTransactionalFunctions;
	}



	public int getNbAEFPDeleted() {
		return nbAEFPDeleted;
	}



	public void setNbAEFPDeleted(int nbAEFPDeleted) {
		this.nbAEFPDeleted = nbAEFPDeleted;
	}



	public int getNbAEFPDeletedDataFunctions() {
		return nbAEFPDeletedDataFunctions;
	}



	public void setNbAEFPDeletedDataFunctions(int nbAEFPDeletedDataFunctions) {
		this.nbAEFPDeletedDataFunctions = nbAEFPDeletedDataFunctions;
	}



	public int getNbAEFPDeletedTransactionalFunctions() {
		return nbAEFPDeletedTransactionalFunctions;
	}



	public void setNbAEFPDeletedTransactionalFunctions(int nbAEFPDeletedTransactionalFunctions) {
		this.nbAEFPDeletedTransactionalFunctions = nbAEFPDeletedTransactionalFunctions;
	}



	public int getNbAEFPModified() {
		return nbAEFPModified;
	}



	public void setNbAEFPModified(int nbAEFPModified) {
		this.nbAEFPModified = nbAEFPModified;
	}



	public int getNbAEFPModifiedDataFunctions() {
		return nbAEFPModifiedDataFunctions;
	}



	public void setNbAEFPModifiedDataFunctions(int nbAEFPModifiedDataFunctions) {
		this.nbAEFPModifiedDataFunctions = nbAEFPModifiedDataFunctions;
	}



	public int getNbAEFPModifiedTransactionalFunctions() {
		return nbAEFPModifiedTransactionalFunctions;
	}



	public void setNbAEFPModifiedTransactionalFunctions(int nbAEFPModifiedTransactionalFunctions) {
		this.nbAEFPModifiedTransactionalFunctions = nbAEFPModifiedTransactionalFunctions;
	}



	public int getNbAEFP() {
		return nbAEFP;
	}



	public void setNbAEFP(int nbAEFP) {
		this.nbAEFP = nbAEFP;
	}



	public int getNbAEFPDataFunctions() {
		return nbAEFPDataFunctions;
	}



	public void setNbAEFPDataFunctions(int nbAEFPDataFunctions) {
		this.nbAEFPDataFunctions = nbAEFPDataFunctions;
	}



	public int getNbAEFPTransactionalFunctions() {
		return nbAEFPTransactionalFunctions;
	}



	public void setNbAEFPTransactionalFunctions(int nbAEFPTransactionalFunctions) {
		this.nbAEFPTransactionalFunctions = nbAEFPTransactionalFunctions;
	}



	public int getNbAETP() {
		return nbAETP;
	}



	public void setNbAETP(int nbAETP) {
		this.nbAETP = nbAETP;
	}



	public int getNbAETPAdded() {
		return nbAETPAdded;
	}



	public void setNbAETPAdded(int nbAETPAdded) {
		this.nbAETPAdded = nbAETPAdded;
	}



	public int getNbAETPDeleted() {
		return nbAETPDeleted;
	}



	public void setNbAETPDeleted(int nbAETPDeleted) {
		this.nbAETPDeleted = nbAETPDeleted;
	}



	public int getNbAETPModified() {
		return nbAETPModified;
	}



	public void setNbAETPModified(int nbAETPModified) {
		this.nbAETPModified = nbAETPModified;
	}



	public int getNbEvovedTransactions() {
		return nbEvovedTransactions;
	}



	public void setNbEvovedTransactions(int nbEvovedTransactions) {
		this.nbEvovedTransactions = nbEvovedTransactions;
	}



	public int getNbTransactions() {
		return nbTransactions;
	}



	public void setNbTransactions(int nbTransactions) {
		this.nbTransactions = nbTransactions;
	}



	public int getNbEnhancementSharedArtifacts() {
		return nbEnhancementSharedArtifacts;
	}



	public void setNbEnhancementSharedArtifacts(int nbEnhancementSharedArtifacts) {
		this.nbEnhancementSharedArtifacts = nbEnhancementSharedArtifacts;
	}



	public int getNbEnhancementSpecificArtifacts() {
		return nbEnhancementSpecificArtifacts;
	}



	public void setNbEnhancementSpecificArtifacts(int nbEnhancementSpecificArtifacts) {
		this.nbEnhancementSpecificArtifacts = nbEnhancementSpecificArtifacts;
	}



	public int getEffortComplexity() {
		return effortComplexity;
	}



	public void setEffortComplexity(int effortComplexity) {
		this.effortComplexity = effortComplexity;
	}



	public int getEquivalentRatio() {
		return equivalentRatio;
	}



	public void setEquivalentRatio(int equivalentRatio) {
		this.equivalentRatio = equivalentRatio;
	}



	public int getNbDecisionsPoints() {
		return nbDecisionsPoints;
	}



	public void setNbDecisionsPoints(int nbDecisionsPoints) {
		this.nbDecisionsPoints = nbDecisionsPoints;
	}



	public Double getNbBFP() {
		return nbBFP;
	}



	public void setNbBFP(Double nbBFP) {
		this.nbBFP = nbBFP;
	}





	public Integer getTotalCritViolations() {
		return totalCritViolations;
	}



	public void setTotalCritViolations(Integer totalCritViolations) {
		this.totalCritViolations = totalCritViolations;
	}



	public Integer getAddedCritViolations() {
		return addedCritViolations;
	}



	public void setAddedCritViolations(Integer addedCritViolations) {
		this.addedCritViolations = addedCritViolations;
	}



	public Integer getRemovedCritViolations() {
		return removedCritViolations;
	}



	public void setRemovedCritViolations(Integer removedCritViolations) {
		this.removedCritViolations = removedCritViolations;
	}



	public Integer getCritViolationsInNewAndModifiedCode() {
		return critViolationsInNewAndModifiedCode;
	}



	public void setCritViolationsInNewAndModifiedCode(Integer critViolationsInNewAndModifiedCode) {
		this.critViolationsInNewAndModifiedCode = critViolationsInNewAndModifiedCode;
	}



	public Integer getTotalViolations() {
		return totalViolations;
	}



	public void setTotalViolations(Integer totalViolations) {
		this.totalViolations = totalViolations;
	}



	public Integer getAddedViolations() {
		return addedViolations;
	}



	public void setAddedViolations(Integer addedViolations) {
		this.addedViolations = addedViolations;
	}



	public Integer getRemovedViolations() {
		return removedViolations;
	}



	public void setRemovedViolations(Integer removedViolations) {
		this.removedViolations = removedViolations;
	}



	public Integer getViolationsInNewAndModifiedCode() {
		return violationsInNewAndModifiedCode;
	}



	public void setViolationsInNewAndModifiedCode(Integer violationsInNewAndModifiedCode) {
		this.violationsInNewAndModifiedCode = violationsInNewAndModifiedCode;
	}



	public String getSnapshotId() {
		return snapshotId;
	}



	public void setSnapshotId(String snapshotId) {
		this.snapshotId = snapshotId;
	}



	public Integer getNbArtifactsAdded() {
		return nbArtifactsAdded;
	}



	public void setNbArtifactsAdded(Integer nbArtifactsAdded) {
		this.nbArtifactsAdded = nbArtifactsAdded;
	}



	public Integer getNbArtifactsDeleted() {
		return nbArtifactsDeleted;
	}



	public void setNbArtifactsDeleted(Integer nbArtifactsDeleted) {
		this.nbArtifactsDeleted = nbArtifactsDeleted;
	}



	public Integer getNbArtifactsModified() {
		return nbArtifactsModified;
	}



	public void setNbArtifactsModified(Integer nbArtifactsModified) {
		this.nbArtifactsModified = nbArtifactsModified;
	}



	public Integer getLocCentral() {
		return locCentral;
	}



	public void setLocCentral(Integer locCentral) {
		this.locCentral = locCentral;
	}



	public String getApplicationId() {
		return applicationId;
	}



	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}



	public Long getSnapshotTime() {
		return snapshotTime;
	}



	public void setSnapshotTime(Long snapshotTime) {
		this.snapshotTime = snapshotTime;
	}



	public Integer getSolvedActionPlanItems() {
		return solvedActionPlanItems;
	}



	public void setSolvedActionPlanItems(Integer solvedActionPlanItems) {
		this.solvedActionPlanItems = solvedActionPlanItems;
	}



	public Integer getAddedActionPlanLowComItems() {
		return addedActionPlanLowComItems;
	}



	public void setAddedActionPlanLowComItems(Integer addedActionPlanLowComItems) {
		this.addedActionPlanLowComItems = addedActionPlanLowComItems;
	}



	public Integer getAddedActionPlanAvgComItems() {
		return addedActionPlanAvgComItems;
	}



	public void setAddedActionPlanAvgComItems(Integer addedActionPlanAvgComItems) {
		this.addedActionPlanAvgComItems = addedActionPlanAvgComItems;
	}



	public Integer getAddedActionPlanHigComItems() {
		return addedActionPlanHigComItems;
	}



	public void setAddedActionPlanHigComItems(Integer addedActionPlanHigComItems) {
		this.addedActionPlanHigComItems = addedActionPlanHigComItems;
	}



	public Integer getAddedActionPlanExtComItems() {
		return addedActionPlanExtComItems;
	}



	public void setAddedActionPlanExtComItems(Integer addedActionPlanExtComItems) {
		this.addedActionPlanExtComItems = addedActionPlanExtComItems;
	}



	public Integer getPendingActionPlanLowComItems() {
		return pendingActionPlanLowComItems;
	}



	public void setPendingActionPlanLowComItems(Integer pendingActionPlanLowComItems) {
		this.pendingActionPlanLowComItems = pendingActionPlanLowComItems;
	}



	public Integer getPendingActionPlanAvgComItems() {
		return pendingActionPlanAvgComItems;
	}



	public void setPendingActionPlanAvgComItems(Integer pendingActionPlanAvgComItems) {
		this.pendingActionPlanAvgComItems = pendingActionPlanAvgComItems;
	}



	public Integer getPendingActionPlanHigComItems() {
		return pendingActionPlanHigComItems;
	}



	public void setPendingActionPlanHigComItems(Integer pendingActionPlanHigComItems) {
		this.pendingActionPlanHigComItems = pendingActionPlanHigComItems;
	}



	public Integer getPendingActionPlanExtComItems() {
		return pendingActionPlanExtComItems;
	}



	public void setPendingActionPlanExtComItems(Integer pendingActionPlanExtComItems) {
		this.pendingActionPlanExtComItems = pendingActionPlanExtComItems;
	}



	public Integer getSolvedActionPlanLowComItems() {
		return solvedActionPlanLowComItems;
	}



	public void setSolvedActionPlanLowComItems(Integer solvedActionPlanLowComItems) {
		this.solvedActionPlanLowComItems = solvedActionPlanLowComItems;
	}



	public Integer getSolvedActionPlanAvgComItems() {
		return solvedActionPlanAvgComItems;
	}



	public void setSolvedActionPlanAvgComItems(Integer solvedActionPlanAvgComItems) {
		this.solvedActionPlanAvgComItems = solvedActionPlanAvgComItems;
	}



	public Integer getSolvedActionPlanHigComItems() {
		return solvedActionPlanHigComItems;
	}



	public void setSolvedActionPlanHigComItems(Integer solvedActionPlanHigComItems) {
		this.solvedActionPlanHigComItems = solvedActionPlanHigComItems;
	}



	public Integer getSolvedActionPlanExtComItems() {
		return solvedActionPlanExtComItems;
	}



	public void setSolvedActionPlanExtComItems(Integer solvedActionPlanExtComItems) {
		this.solvedActionPlanExtComItems = solvedActionPlanExtComItems;
	}



	public Double getAddedCVOnNewCodePerAEP() {
		return addedCVOnNewCodePerAEP;
	}


	public void setAddedCVOnNewCodePerAEP(Double addedCVOnNewCodePerAEP) {
		this.addedCVOnNewCodePerAEP = addedCVOnNewCodePerAEP;
	}


	public String getDomain() {
		return domain;
	}



	public void setDomain(String domain) {
		this.domain = domain;
	}


	public Double getCVPerAFP() {
		return CVPerAFP;
	}


	public void setCVPerAFP(Double cVPerAFP) {
		CVPerAFP = cVPerAFP;
	}

	
	
	public Integer getNbEFPTotalAdded() {
		return nbEFPTotalAdded;
	}


	public void setNbEFPTotalAdded(Integer nbEFPTotalAdded) {
		this.nbEFPTotalAdded = nbEFPTotalAdded;
	}


	public Integer getNbEFPDataFPAdded() {
		return nbEFPDataFPAdded;
	}


	public void setNbEFPDataFPAdded(Integer nbEFPDataFPAdded) {
		this.nbEFPDataFPAdded = nbEFPDataFPAdded;
	}


	public Integer getNbEFPTransactionalFPAdded() {
		return nbEFPTransactionalFPAdded;
	}


	public void setNbEFPTransactionalFPAdded(Integer nbEFPTransactionalFPAdded) {
		this.nbEFPTransactionalFPAdded = nbEFPTransactionalFPAdded;
	}


	public Integer getNbEFPTotalModified() {
		return nbEFPTotalModified;
	}


	public void setNbEFPTotalModified(Integer nbEFPTotalModified) {
		this.nbEFPTotalModified = nbEFPTotalModified;
	}


	public Integer getNbEFPDataFPModified() {
		return nbEFPDataFPModified;
	}


	public void setNbEFPDataFPModified(Integer nbEFPDataFPModified) {
		this.nbEFPDataFPModified = nbEFPDataFPModified;
	}


	public Integer getNbEFPTransactionalFPModified() {
		return nbEFPTransactionalFPModified;
	}


	public void setNbEFPTransactionalFPModified(Integer nbEFPTransactionalFPModified) {
		this.nbEFPTransactionalFPModified = nbEFPTransactionalFPModified;
	}


	public Integer getNbEFPTotalDeleted() {
		return nbEFPTotalDeleted;
	}


	public void setNbEFPTotalDeleted(Integer nbEFPTotalDeleted) {
		this.nbEFPTotalDeleted = nbEFPTotalDeleted;
	}


	public Integer getNbEFPDataFPDeleted() {
		return nbEFPDataFPDeleted;
	}


	public void setNbEFPDataFPDeleted(Integer nbEFPDataFPDeleted) {
		this.nbEFPDataFPDeleted = nbEFPDataFPDeleted;
	}


	public Integer getNbEFPTransactionalFPDeleted() {
		return nbEFPTransactionalFPDeleted;
	}


	public void setNbEFPTransactionalFPDeleted(Integer nbEFPTransactionalFPDeleted) {
		this.nbEFPTransactionalFPDeleted = nbEFPTransactionalFPDeleted;
	}


	public Integer getNbEFPTotalUnchanged() {
		return nbEFPTotalUnchanged;
	}


	public void setNbEFPTotalUnchanged(Integer nbEFPTotalUnchanged) {
		this.nbEFPTotalUnchanged = nbEFPTotalUnchanged;
	}


	public Integer getNbEFPDataFPUnchanged() {
		return nbEFPDataFPUnchanged;
	}


	public void setNbEFPDataFPUnchanged(Integer nbEFPDataFPUnchanged) {
		this.nbEFPDataFPUnchanged = nbEFPDataFPUnchanged;
	}


	public Integer getNbEFPTransactionalFPUnchanged() {
		return nbEFPTransactionalFPUnchanged;
	}


	public void setNbEFPTransactionalFPUnchanged(Integer nbEFPTransactionalFPUnchanged) {
		this.nbEFPTransactionalFPUnchanged = nbEFPTransactionalFPUnchanged;
	}


	public Integer getNbEFPTotal() {
		return nbEFPTotal;
	}


	public void setNbEFPTotal(Integer nbEFPTotal) {
		this.nbEFPTotal = nbEFPTotal;
	}


	public Integer getNbEFPDataFP() {
		return nbEFPDataFP;
	}


	public void setNbEFPDataFP(Integer nbEFPDataFP) {
		this.nbEFPDataFP = nbEFPDataFP;
	}


	public Integer getNbEFPTransactionalFP() {
		return nbEFPTransactionalFP;
	}


	public void setNbEFPTransactionalFP(Integer nbEFPTransactionalFP) {
		this.nbEFPTransactionalFP = nbEFPTransactionalFP;
	}


	public Integer getNbInterfaces() {
		return nbInterfaces;
	}


	public void setNbInterfaces(Integer nbInterfaces) {
		this.nbInterfaces = nbInterfaces;
	}


	public Integer getNbIncludes() {
		return nbIncludes;
	}


	public void setNbIncludes(Integer nbIncludes) {
		this.nbIncludes = nbIncludes;
	}


	public Integer getNbFunctionPools() {
		return nbFunctionPools;
	}


	public void setNbFunctionPools(Integer nbFunctionPools) {
		this.nbFunctionPools = nbFunctionPools;
	}


	public Integer getNbABAPUserExits() {
		return nbABAPUserExits;
	}


	public void setNbABAPUserExits(Integer nbABAPUserExits) {
		this.nbABAPUserExits = nbABAPUserExits;
	}


	public Integer getNbABAPTransactions() {
		return nbABAPTransactions;
	}


	public void setNbABAPTransactions(Integer nbABAPTransactions) {
		this.nbABAPTransactions = nbABAPTransactions;
	}


	public String getIsoDate() {
		return isoDate;
	}


	public void setIsoDate(String isoDate) {
		this.isoDate = isoDate;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
//		sb.append(snapshotHref);
//		sb.append(OUTPUT_SEPARATOR);
		sb.append(applicationName);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(snapshotVersion);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcTQI);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcEffi);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcRobus);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcSecu);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcChang);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcTrans);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcCodinBP);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcApplicationOp);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(bcCodeMaintain);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(loc);		
		if (isFullReport()) {
			sb.append(OUTPUT_SEPARATOR);
			sb.append(nbAFPTotalFP);
			sb.append(OUTPUT_SEPARATOR);
			sb.append(nbAFPDatafunctionsFP);			
			sb.append(OUTPUT_SEPARATOR);
			sb.append(nbAFPTransactionsFP);			
		}
		sb.append(OUTPUT_SEPARATOR);
		sb.append(nbAEPTotal);		
		sb.append(OUTPUT_SEPARATOR);
		sb.append(nbAEPAdded);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(nbAEPDeleted);
		sb.append(OUTPUT_SEPARATOR);
		sb.append(nbAEPModifed);
		return sb.toString();
	}
	
	protected boolean isFullReport() {
		return  RestAPIReports.REPORTTYPE_METRICS_FULLREPORT.equals(reportType);
	}
}

