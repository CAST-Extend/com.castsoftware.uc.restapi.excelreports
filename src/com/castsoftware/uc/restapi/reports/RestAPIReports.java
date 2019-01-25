package com.castsoftware.uc.restapi.reports;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Generation of reports that consolidate data from the Rest API (heath or
 * engineering) in Excel
 * 
 * @author MMR
 *
 */
public class RestAPIReports {

	//////////////////////////////////////////////////////////////////////////////////////////////////

	// Please also change the version in version.properties file
	private static final String VERSION = "1.4.5";

	//////////////////////////////////////////////////////////////////////////////////////////////////

	private static final String DEFAULT_ENCODING = "iso-8859-1";
	static String sdfr = (new SimpleDateFormat("yyyyMMddHHmm")).format(new Date());

	// businessCriteria
	private static String BUSINESS_CRITERIA_ID_TQI = "60017";
	private static String BUSINESS_CRITERIA_ID_ROBUSTNESS = "60013";
	private static String BUSINESS_CRITERIA_ID_EFFICIENCY = "60014";
	private static String BUSINESS_CRITERIA_ID_SECURITY = "60016";
	private static String BUSINESS_CRITERIA_ID_TRANSFERABILITY = "60011";
	private static String BUSINESS_CRITERIA_ID_CHANGEABILITY = "60012";
	private static String BUSINESS_CRITERIA_ID_ARCH_DESIGN = "66032";
	private static String BUSINESS_CRITERIA_ID_CODING_PROGRAMMING_BEST_PRACTICES = "66031";
	private static String BUSINESS_CRITERIA_ID_DOCUMENTATION = "66033";

	private final static String[] LIST_BUSINESS_CRITERIA_IDS = new String[] { BUSINESS_CRITERIA_ID_TQI,
			BUSINESS_CRITERIA_ID_ROBUSTNESS, BUSINESS_CRITERIA_ID_EFFICIENCY, BUSINESS_CRITERIA_ID_SECURITY,
			BUSINESS_CRITERIA_ID_TRANSFERABILITY, BUSINESS_CRITERIA_ID_CHANGEABILITY, BUSINESS_CRITERIA_ID_ARCH_DESIGN,
			BUSINESS_CRITERIA_ID_CODING_PROGRAMMING_BEST_PRACTICES, BUSINESS_CRITERIA_ID_DOCUMENTATION };

	// Report type possible options
	public static final String REPORTTYPE_METRICS_KPIREPORT = "Metrics_KPIReport";
	public static final String REPORTTYPE_METRICS_FULLREPORT = "Metrics_FullReport";
	public static final String REPORTTYPE_ENV_DELTAREPORT = "Env_DeltaReport";
	public static final String REPORTTYPE_QR_SIMPLEREPORT = "QR_SimpleReport";
	public static final String REPORTTYPE_QR_FULLREPORT = "QR_FullReport";

	// Filter version possible options
	private static final String FILTER_VERSIONS_LASTONE = "VERSIONS_LASTONE";
	private static final String FILTER_VERSIONS_LASTTWO = "VERSIONS_LASTTWO";
	private static final String FILTER_VERSIONS_ALL = "VERSIONS_ALL";

	//////////////////////////////////////////////////////////////////////////////////////////////////
	// command line short and long keys

	// short key params
	private final static String OPTION_SK_URL = "u";
	private final static String OPTION_SK_HDOMAIN = "hDomain";
	private final static String OPTION_SK_EDOMAINS = "eDomains";
	private final static String OPTION_SK_USR = "usr";
	private final static String OPTION_SK_PWD = "pwd";
	private final static String OPTION_SK_ENV = "env";
	private final static String OPTION_SK_RTYPE = "rt";
	private final static String OPTION_SK_APPFILTER = "pra";
	private final static String OPTION_SK_VERSIONFILTER = "pra";
	// DB
	private final static String OPTION_SK_DB_RUNSQL = "dbr";
	private final static String OPTION_SK_DB_HOST = "dbh";
	private final static String OPTION_SK_DB_PORT = "dbpo";
	private final static String OPTION_SK_DB_DBNAME = "dbn";
	private final static String OPTION_SK_DB_SCHEMAS = "dbls";
	private final static String OPTION_SK_DB_USER = "dbu";
	private final static String OPTION_SK_DB_PWD = "dbpw";

	// Lonk key params
	private final static String OPTION_LK_URL = "url";
	private final static String OPTION_LK_HDOMAIN = "healthDomain";
	private final static String OPTION_LK_EDOMAINS = "engDomains";
	private final static String OPTION_LK_USR = "user";
	private final static String OPTION_LK_PWD = "password";
	private final static String OPTION_LK_ENV = "environment";
	private final static String OPTION_LK_RTYPE = "reportType";
	private final static String OPTION_LK_APPFILTER = "processApplicationFilter";
	private final static String OPTION_LK_VERSIONFILTER = "versionFilter";
	// DB
	private final static String OPTION_LK_DB_RUNSQL = "dbRunSql";
	private final static String OPTION_LK_DB_HOST = "dbHost";
	private final static String OPTION_LK_DB_PORT = "dbPort";
	private final static String OPTION_LK_DB_DBNAME = "dbDatabaseName";
	private final static String OPTION_LK_DB_SCHEMAS = "dbSchemas";
	private final static String OPTION_LK_DB_USER = "dbUser";
	private final static String OPTION_LK_DB_PWD = "dbPassword";

	/**
	 * Specific for Telefonica customer Hardcoded list of application with NO
	 * FP, separated by a comma Used to set a N/A value for FP metrics because
	 * we have used a FP licence key and want to hide those metrics
	 */
	private static final String[] TELEFONICASPECIFIC_APPLICATIONS_WITH_NOFP = new String[] {
			"U-057 - Unified-FSS-Billing" };
	/*
	 * CAST Demo parameters
	 * 
	 * private static final String ROOT_URL =
	 * "http://demo-us.castsoftware.com/AAD2/rest"; private static final String
	 * DOMAIN = "AAD"; private static final String USER = "CIO"; private static
	 * final String PASSWORD = "cast";
	 */

	/**
	 * Rest API type
	 */
	private String reportType = REPORTTYPE_METRICS_KPIREPORT;

	/**
	 * Css DB Host Name (use temporarly to query central schema to collect
	 * additional metrics)
	 */
	private String cssDbHostname = null;

	/**
	 * Css DB Port
	 */
	private String cssDbPort = null;

	/**
	 * Css DB Database
	 */
	private String cssDbDatabase = null;

	/**
	 * Css DB user
	 */
	private String cssDbUser = null;

	/**
	 * Css DB password
	 */
	private String cssDbPassword = null;

	/**
	 * Css DB list of central schemas, separated by a comma
	 */
	private String cssDbListCentralSchemas = null;

	/**
	 * Encoding used for the HTTP user & password authentication
	 */
	private String encoding = null;

	/**
	 * Rest API root URL
	 */
	private String rootURL = null;

	/**
	 * Rest API AAD domain
	 */
	private String AADDomain = null;

	/**
	 * Rest API AED domains
	 */
	private String AEDDomains = null;

	/**
	 * Rest API user
	 */
	private String user = null;

	/**
	 * Rest API password
	 */
	private String password = null;

	/**
	 * environment (PROD/DEV)
	 */
	private String environment = null;

	/**
	 * Retrieve or not the action plan items
	 */
	// TODO : param to be externalized
	private boolean bRetrieveActionPlanItems = false;

	/**
	 * Retrieve or not the central schemas metrics
	 */
	private boolean bRetrieveDBCentralSchemaMetrics = false;

	/**
	 * Used (optional) to filter application names
	 */
	private String filterApplicationNames = "";

	/**
	 * Used (optional) to filter versions
	 */
	private String filterVersions = FILTER_VERSIONS_LASTONE;

	/**
	 * Class logger
	 */
	private Logger logger = null;
	List<ApplicationCharacteristics> listApplications = new ArrayList<ApplicationCharacteristics>();

	/**
	 * List of Metrics report outputs
	 */
	List<MetricReportOutput> listMetricsReportOutputs = new ArrayList<MetricReportOutput>();

	/**
	 * List of Quality rules report outputs (simple without technical criterion
	 * and business criterion)
	 */
	List<QualityRulesReportOutput> listQRReportOutputs = new ArrayList<QualityRulesReportOutput>();

	/**
	 * List of Quality rules report outputs (with technical criterion and
	 * business criterion)
	 */
	List<QualityRulesReportOutput> listQRReportOutputsWithTCAndBC = new ArrayList<QualityRulesReportOutput>();

	/**
	 * Constructor
	 */
	private RestAPIReports() {
		super();
		listApplications = new ArrayList<ApplicationCharacteristics>();
		logger = Logger.getLogger("MainLogger");
	}

	/**
	 * get the list of business criteria as a String
	 * 
	 * @return
	 */
	private static String getBusinessCriteriaAsString() {
		StringBuffer sb = new StringBuffer();
		for (String s : LIST_BUSINESS_CRITERIA_IDS) {
			sb.append(s);
			sb.append(",");
		}
		String output = sb.toString();
		return output.substring(0, output.length() - 1);
	}

	/**
	 * Rest API URL to retrieve the list of violations
	 */
	// AED_UNIFIED_BILLING/applications/1081752/snapshots/18/violations?rule-pattern=(cc:60017,nc:60017)&nbRows=10000000

	/**
	 * Rest API URI used to retrieve the list of applications
	 */
	protected String getURIApplicationList(String domain) {
		return getRootURL() + "/" + domain + "/applications";
	}

	private String getSnapshotVersionFilterParam() {
		if (this.filterVersions == null)
			return "-1";
		if (FILTER_VERSIONS_ALL.equals(this.filterVersions)) {
			return "$all";
		} else if (FILTER_VERSIONS_LASTTWO.equals(this.filterVersions)) {
			return "-2";
		} else if (FILTER_VERSIONS_LASTONE.equals(this.filterVersions)) {
			return "-1";
		} else
			return "-1";
	}

	/**
	 * Rest API URI used to retrieve the list quality indicators results last
	 * version
	 */
	protected String getURIQualityIndicatorsResults(String domain) {
		return getRootURL() + "/" + domain + "/applications/%2/results?quality-indicators=(nc:"
				+ BUSINESS_CRITERIA_ID_TQI + ",cc:" + BUSINESS_CRITERIA_ID_TQI
				+ ")&select=(evolutionSummary,violationRatio)&snapshots=" + getSnapshotVersionFilterParam();
	}

	/**
	 * Rest API URI used to retrieve the list quality rules results, for the
	 * last version, for one application
	 */
	protected String getURIQualityIndicatorsResults(String domain, String applicationId) {
		return getRootURL() + "/" + domain + "/applications/" + applicationId + "/results?quality-indicators=(nc:"
				+ BUSINESS_CRITERIA_ID_TQI + ",cc:" + BUSINESS_CRITERIA_ID_TQI
				+ ")&select=(evolutionSummary,violationRatio)&snapshots=" + getSnapshotVersionFilterParam();
	}

	/**
	 * Rest API URI used to retrieve the list of Business criteria metrics
	 * 
	 * TQI = "60017"; EFFICIENCY = "60014"; ROBUS = "60013"; SECU = "60016";
	 * CHANG = "60012"; TRANS = "60011"; CODING BEST PRAC = "66031"
	 */
	protected String getURIBusinessCriteriasCMetrics(String domain) {
		String bclist = getBusinessCriteriaAsString();
		return getRootURL() + "/" + domain + "/results?quality-indicators=" + bclist + "&snapshots="
				+ getSnapshotVersionFilterParam() + "&applications=$all";
	}

	// TODO AAD/quality-indicators/60017/snapshots/671

	/**
	 * Rest API URI used to retrieve the list of Sizing measures metrics
	 * 
	 * AFP metrics 10202 Total AFP function points 10203 AFP Datafunctions
	 * function points 10204 AFP Transactions function points
	 * 
	 * AEP metrics 10450 Total AEP 10451 Added AEP 10452 Deleted AEP 10453
	 * Modified AEP etc
	 * 
	 */
	protected String getURISizingMeasuresFunctionalWeightMeasures(String domain) {
		return getRootURL() + "/" + domain + "/results?sizing-measures=(functional-weight-measures)&snapshots="
				+ getSnapshotVersionFilterParam() + "&applications=$all";
	}

	/**
	 * Rest API URI used to retrieve the list of Technical size measures
	 * 
	 * 
	 */
	protected String getURISizingMeasuresTechnicalSizeMeasures(String domain) {
		return getRootURL() + "/" + domain + "/results?sizing-measures=(technical-size-measures)&snapshots="
				+ getSnapshotVersionFilterParam() + "&applications=$all";
	}

	/**
	 * Rest API URI used to retrieve the list of Evolution summary metrics
	 * 
	 */
	protected String getURIEvolutionSummary(String domain) {
		return getRootURL() + "/" + domain + "/results?select=evolutionSummary&snapshots=("
				+ getSnapshotVersionFilterParam() + ")&applications=($all)";
	}

	/**
	 * Rest API URI used to retrieve all snapshots
	 */
	protected String getSnapshotsList(String domain) {
		return getRootURL() + "/" + domain + "/applications/%s/snapshots";
	}

	/**
	 * Rest AED API URI used to retrieve action plan summary
	 */
	protected String getURIActionPlanSummary(String domain) {
		return getRootURL() + "/" + domain + "/applications/%2/snapshots/%3/action-plan/summary?nbRows=100000";
	}

	/**
	 * Rest AED API URI used to retrieve excluded summary
	 */
	protected String getURIExclusionsSummary(String domain) {
		return getRootURL() + "/" + domain + "/applications/%2/snapshots/%3/excluded-violations-summary";
	}

	/**
	 * Rest AED API URI used to retrieve scheduled excluded summary
	 */
	protected String getURISScheduledExclusionsSummary(String domain) {
		return getRootURL() + "/" + domain + "/applications/%2/snapshots/%3/exclusions/scheduled-summary";
	}

	/**
	 * Rest AED API URI used to retrieve action plan summary
	 */
	protected String getURIActionPlanIssues(String domain) {
		return getRootURL() + "/" + domain + "/applications/%2/snapshots/%3/action-plan/issues?nbRows=100000";
	}

	protected String getEncodingToUse() {
		if (getEncoding() == null)
			return DEFAULT_ENCODING;
		return getEncoding();
	}

	private String getFileName() {
		StringBuffer sbFileName = new StringBuffer();
		sbFileName.append("Export Data_");
		if (getEnvironment() != null) {
			sbFileName.append(getEnvironment());
			sbFileName.append("_");
		}
		if (getReportType() != null) {
			sbFileName.append(getReportType());
			sbFileName.append("_");
		}
		sbFileName.append(sdfr);
		sbFileName.append(".xlsx");
		// return "Export Data_"+sdfr+".xlsx";
		return sbFileName.toString();
	}

	/**
	 * This application needs to be filtered or be processed ?
	 * 
	 * @param appName
	 */
	private boolean applicationtoBeProcessed(String appName) {
		if (appName == null)
			return false;
		if (this.filterApplicationNames == null || "".equals(this.filterApplicationNames))
			return true;
		String[] appToFilter = this.filterApplicationNames.split(",");
		for (int i = 0; i < appToFilter.length; i++) {
			if (appName.equals(appToFilter[i])) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check the JSON errors
	 * 
	 * @param responseBody
	 * @return
	 */
	private JSONArray checkJSONArrayResponseBodyErrors(String responseBody) {
		JSONArray jsonArray = null;
		if (responseBody == null)
			return null;
		try {
			jsonArray = new JSONArray(responseBody);
		} catch (Exception e) {
			if ("A JSONArray text must start with '[' at character 1".equals(e.getMessage())) {
				logger.error("Error " + responseBody.toString());
			}
			logger.error("Error " + e.getMessage());
			jsonArray = new JSONArray();
		}
		return jsonArray;
	}

	/**
	 * Check the JSON errors
	 * 
	 * @param responseBody
	 * @return
	 */
	private JSONObject checkJSONObjectResponseBodyErrors(String responseBody) {
		JSONObject jsonObject = null;
		if (responseBody == null)
			return null;
		try {
			jsonObject = new JSONObject(responseBody);
		} catch (Exception e) {
			if ("A JSONArray text must start with '[' at character 1".equals(e.getMessage())) {
				logger.error("Error " + responseBody.toString());
			}
			logger.error("Error " + e.getMessage());
			jsonObject = new JSONObject();
		}
		return jsonObject;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Retrieve the application list
	 * 
	 * @throws Exception
	 */
	private List<ApplicationCharacteristics> retrieveApplicationList(String domain) throws Exception {
		List<ApplicationCharacteristics> locallistApplication = new ArrayList<ApplicationCharacteristics>();
		try {
			String responseBody = executeRestAPIRequest(getURIApplicationList(domain));
			// Parsing the JSON structure
			JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
			for (int i = 0; i < array.length(); i++) {
				JSONObject object = array.getJSONObject(i);
				ApplicationCharacteristics app = new ApplicationCharacteristics();
				String applicationName = object.getString("name");

				// Filter mecanism based on application name (optional input
				// parameter)
				if (!applicationtoBeProcessed(applicationName)) {
					continue;
				}
				app.setName(applicationName);
				String applicationHRef = object.getString("href");
				app.setHref(applicationHRef);
				String applicationId = applicationHRef.substring(applicationHRef.lastIndexOf("/") + 1);
				app.setId(applicationId);
				app.setDomain(domain);

				locallistApplication.add(app);
				// strApplicationList.append(applicationName);
				// if (i < array.length() - 1)
				// strApplicationList.append(",");
			}

		} catch (Exception l_exception) {
			logger.error(l_exception.getMessage(), l_exception);
			throw l_exception;
		} finally {
		}
		return locallistApplication;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve action plan summary + issues
	 * 
	 * @throws Exception
	 */
	private void retrieveActionPlans() throws Exception {

		for (MetricReportOutput reportOutput : this.listMetricsReportOutputs) {
			try {
				// Retrieve the list of action plan items for this snapshot
				String URIActionsPlanSummary = getURIActionPlanSummary(reportOutput.getDomain())
						// application id is different in AAD/AED
						.replaceAll("%2", reportOutput.getApplicationId())
						// snapshot id is different in AAD/AED
						.replaceAll("%3", reportOutput.getSnapshotId());
				String responseBody = executeRestAPIRequest(URIActionsPlanSummary);
				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				int lAdded = 0;
				int lPending = 0;
				int lSolved = 0;
				logger.debug(
						"Action plan : " + reportOutput.getApplicationName() + " " + reportOutput.getSnapshotVersion());
				for (int j = 0; j < array.length(); j++) {
					JSONObject object = array.getJSONObject(j);
					String ruleName = object.getJSONObject("rulePattern").getString("name");
					int kadd = object.getInt("addedIssues");
					lAdded += kadd;
					int kpen = object.getInt("pendingIssues");
					lPending += kpen;
					int ksol = object.getInt("solvedIssues");
					lSolved += ksol;
					logger.debug(ruleName + ";" + kadd + ";" + kpen + ";" + ksol);
				}
				reportOutput.setAddedActionPlanItems(lAdded);
				reportOutput.setPendingActionPlanItems(lPending);
				reportOutput.setSolvedActionPlanItems(lSolved);
				logger.debug("Total;" + lAdded + ";" + lPending + ";" + lSolved);

				// Action plan issues
				String URIActionsPlanIssues = getURIActionPlanIssues(reportOutput.getDomain())
						// application id is different in AAD/AED
						.replaceAll("%2", reportOutput.getApplicationId())
						// snapshot id is different in AAD/AED
						.replaceAll("%3", reportOutput.getSnapshotId());
				responseBody = executeRestAPIRequest(URIActionsPlanIssues);
				// Parsing the JSON structure
				array = checkJSONArrayResponseBodyErrors(responseBody);
				int kadd_low = 0;
				int kadd_moderate = 0;
				int kadd_high = 0;
				int kadd_extreme = 0;
				int kpen_low = 0;
				int kpen_moderate = 0;
				int kpen_high = 0;
				int kpen_extreme = 0;
				int ksol_low = 0;
				int ksol_moderate = 0;
				int ksol_high = 0;
				int ksol_extreme = 0;
				for (int j = 0; j < array.length(); j++) {
					JSONObject object = array.getJSONObject(j);
					object.keys();

					String status = object.getJSONObject("remedialAction").getString("status");
					String priority = object.getJSONObject("remedialAction").getString("priority");
					String exclusionRequest = object.getString("exclusionRequest");
					String ruleName = object.getJSONObject("rulePattern").getString("name");
					String componentName = object.getJSONObject("component").getString("shortName");
					if ("added".equals(status)) {
						if ("low".equals(priority))
							kadd_low++;
						if ("moderate".equals(priority))
							kadd_moderate++;
						if ("high".equals(priority))
							kadd_high++;
						if ("extreme".equals(priority))
							kadd_extreme++;
					}
					if ("pending".equals(status)) {
						if ("low".equals(priority))
							kpen_low++;
						if ("moderate".equals(priority))
							kpen_moderate++;
						if ("high".equals(priority))
							kpen_high++;
						if ("extreme".equals(priority))
							kpen_extreme++;
					}
					if ("solved".equals(status)) {
						if ("low".equals(priority))
							ksol_low++;
						if ("moderate".equals(priority))
							ksol_moderate++;
						if ("high".equals(priority))
							ksol_high++;
						if ("extreme".equals(priority))
							ksol_extreme++;
					}
					// logger.debug(componentName + ";added;" + kadd_low + ";" +
					// kadd_moderate + ";" + kadd_high + ";" + kadd_extreme);
					// logger.debug(componentName + ";added;" + kadd_low + ";" +
					// kadd_moderate + ";" + kadd_high + ";" + kadd_extreme);
				}

				logger.debug("added;" + kadd_low + ";" + kadd_moderate + ";" + kadd_high + ";" + kadd_extreme);
				logger.debug("pending;" + kpen_low + ";" + kpen_moderate + ";" + kpen_high + ";" + kpen_extreme);
				logger.debug("solved;" + ksol_low + ";" + ksol_moderate + ";" + ksol_high + ";" + ksol_extreme);
				reportOutput.setAddedActionPlanLowComItems(kadd_low);
				reportOutput.setAddedActionPlanAvgComItems(kadd_moderate);
				reportOutput.setAddedActionPlanHigComItems(kadd_high);
				reportOutput.setAddedActionPlanExtComItems(kadd_extreme);
				reportOutput.setPendingActionPlanLowComItems(kpen_low);
				reportOutput.setPendingActionPlanAvgComItems(kpen_moderate);
				reportOutput.setPendingActionPlanHigComItems(kpen_high);
				reportOutput.setPendingActionPlanExtComItems(kpen_extreme);
				reportOutput.setSolvedActionPlanLowComItems(ksol_low);
				reportOutput.setSolvedActionPlanAvgComItems(ksol_moderate);
				reportOutput.setSolvedActionPlanHigComItems(ksol_high);
				reportOutput.setSolvedActionPlanExtComItems(ksol_extreme);
			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
	}

	// private void retrieveActionPlans() throws Exception {
	// // Skip if the AED domains are not defined
	// if (AEDDomains == null || AEDDomains.equals("N/A"))
	// return;
	//
	// String[] arrAEDDomains = AEDDomains.split(",");
	// for (int i = 0; i < arrAEDDomains.length; i++) {
	// String AEDDomain = arrAEDDomains[i];
	// List<ApplicationCharacteristics> listAEDApplications =
	// retrieveApplicationList(AEDDomain);
	// List<ReportOutput> listSnapshotsAED = null;
	//
	// for (ApplicationCharacteristics appChar : listAEDApplications) {
	// logger.debug("AEDDomain=" + AEDDomain);
	// // collect the list of snapshots in AED
	// listSnapshotsAED = null;//retrieveSnapshotList(AEDDomain,
	// appChar.getId());
	//
	// for (ReportOutput outputAAD : listReportOuputs) {
	// logger.debug("AADsnapshot=" + outputAAD.getSnapshotVersion() + "/" +
	// outputAAD.getSnapshotId() + "/" + outputAAD.getSnapshotTime());
	// if (outputAAD.getApplicationName().equals(appChar.getName())) {
	// // Look for the snapshot id in AED
	// for (ReportOutput outputAED : listSnapshotsAED ) {
	// logger.debug(" AEDsnapshot=" + outputAED.getSnapshotVersion() + "/" +
	// outputAED.getSnapshotId() + "/" + outputAED.getSnapshotTime());
	// if (outputAED.getApplicationName().equals(outputAAD.getApplicationName())
	// && outputAED.getSnapshotTime().equals(outputAAD.getSnapshotTime())) {
	// try {
	// // Retrieve the list of action plan items for this snapshot
	// String URIActionsPlanSummary = getURIActionPlanSummary(AEDDomain)
	// // application id is different in AAD/AED
	// .replaceAll("%2", appChar.getId())
	// // snapshot id is different in AAD/AED
	// .replaceAll("%3", outputAED.getSnapshotId());
	// String responseBody = executeRestAPIRequest(URIActionsPlanSummary);
	// // Parsing the JSON structure
	// JSONArray array = checkJSONResponseBodyErrors(responseBody);
	//
	// int lAdded = 0;
	// int lPending = 0;
	// int lSolved = 0;
	// logger.debug("Action plan : " + outputAED.getApplicationName() + " " +
	// outputAED.getSnapshotVersion());
	// for (int j = 0; j < array.length(); j++) {
	// JSONObject object = array.getJSONObject(j);
	// String ruleName = object.getJSONObject("rulePattern").getString("name");
	// int kadd = object.getInt("addedIssues");
	// lAdded += kadd;
	// int kpen = object.getInt("pendingIssues");
	// lPending += kpen;
	// int ksol = object.getInt("solvedIssues");
	// lSolved += ksol;
	// logger.debug(ruleName + ";" + kadd + ";" + kpen + ";" + ksol);
	// }
	// outputAAD.setAddedActionPlanItems(lAdded);
	// outputAAD.setPendingActionPlanItems(lPending);
	// outputAAD.setSolvedActionPlanItems(lSolved);
	// logger.debug("Total;" + lAdded + ";" + lPending + ";" + lSolved);
	//
	//
	// // Action plan issues
	// String URIActionsPlanIssues = getURIActionPlanIssues()
	// .replaceAll("%1", AEDDomain)
	// // application id is different in AAD/AED
	// .replaceAll("%2", appChar.getId())
	// // snapshot id is different in AAD/AED
	// .replaceAll("%3", outputAED.getSnapshotId());
	// responseBody = executeRestAPIRequest(URIActionsPlanIssues);
	// // Parsing the JSON structure
	// array = checkJSONResponseBodyErrors(responseBody);
	// int kadd_low = 0;
	// int kadd_moderate = 0;
	// int kadd_high = 0;
	// int kadd_extreme = 0;
	// int kpen_low = 0;
	// int kpen_moderate = 0;
	// int kpen_high = 0;
	// int kpen_extreme = 0;
	// int ksol_low = 0;
	// int ksol_moderate = 0;
	// int ksol_high = 0;
	// int ksol_extreme = 0;
	// for (int j = 0; j < array.length(); j++) {
	// JSONObject object = array.getJSONObject(j);
	// object.keys();
	//
	// String status =
	// object.getJSONObject("remedialAction").getString("status");
	// String priority =
	// object.getJSONObject("remedialAction").getString("priority");
	// String exclusionRequest = object.getString("exclusionRequest");
	// String ruleName = object.getJSONObject("rulePattern").getString("name");
	// String componentName =
	// object.getJSONObject("component").getString("shortName");
	// if ("added".equals(status)) {
	// if ("low".equals(priority))
	// kadd_low++;
	// if ("moderate".equals(priority))
	// kadd_moderate++;
	// if ("high".equals(priority))
	// kadd_high++;
	// if ("extreme".equals(priority))
	// kadd_extreme++;
	// }
	// if ("pending".equals(status)) {
	// if ("low".equals(priority))
	// kpen_low++;
	// if ("moderate".equals(priority))
	// kpen_moderate++;
	// if ("high".equals(priority))
	// kpen_high++;
	// if ("extreme".equals(priority))
	// kpen_extreme++;
	// }
	// if ("solved".equals(status)) {
	// if ("low".equals(priority))
	// ksol_low++;
	// if ("moderate".equals(priority))
	// ksol_moderate++;
	// if ("high".equals(priority))
	// ksol_high++;
	// if ("extreme".equals(priority))
	// ksol_extreme++;
	// }
	// //logger.debug(componentName + ";added;" + kadd_low + ";" + kadd_moderate
	// + ";" + kadd_high + ";" + kadd_extreme);
	// //logger.debug(componentName + ";added;" + kadd_low + ";" + kadd_moderate
	// + ";" + kadd_high + ";" + kadd_extreme);
	// }
	//
	// logger.debug("added;" + kadd_low + ";" + kadd_moderate + ";" + kadd_high
	// + ";" + kadd_extreme);
	// logger.debug("pending;" + kpen_low + ";" + kpen_moderate + ";" +
	// kpen_high + ";" + kpen_extreme);
	// logger.debug("solved;" + ksol_low + ";" + ksol_moderate + ";" + ksol_high
	// + ";" + ksol_extreme);
	// outputAAD.setAddedActionPlanLowComItems(kadd_low);
	// outputAAD.setAddedActionPlanAvgComItems(kadd_moderate);
	// outputAAD.setAddedActionPlanHigComItems(kadd_high);
	// outputAAD.setAddedActionPlanExtComItems(kadd_extreme);
	// outputAAD.setPendingActionPlanLowComItems(kpen_low);
	// outputAAD.setPendingActionPlanAvgComItems(kpen_moderate);
	// outputAAD.setPendingActionPlanHigComItems(kpen_high);
	// outputAAD.setPendingActionPlanExtComItems(kpen_extreme);
	// outputAAD.setSolvedActionPlanLowComItems(ksol_low);
	// outputAAD.setSolvedActionPlanAvgComItems(ksol_moderate);
	// outputAAD.setSolvedActionPlanHigComItems(ksol_high);
	// outputAAD.setSolvedActionPlanExtComItems(ksol_extreme);
	//
	// // we have processed the snapshot, break the current loops to move to
	// next AAD snapshot
	// break;
	//
	// } catch (Exception l_exception) {
	// logger.error(l_exception.getMessage(), l_exception);
	// throw l_exception;
	// } finally {
	// }
	// }
	// }
	// }
	//
	// }
	// }
	// }
	//
	// }

	/**
	 * Retrieve the snapshots list of the applications already loaded
	 * 
	 * @throws Exception
	 */
	private List<SnapshotCharacteristics> retrieveSnapshotList() throws Exception {
		List<SnapshotCharacteristics> listSnapshots = new ArrayList<SnapshotCharacteristics>();
		for (ApplicationCharacteristics app : this.listApplications) {
			try {
				// Retrieve the list of applications
				String responseBody = executeRestAPIRequest(
						String.format(getSnapshotsList(app.getDomain()), app.getId()));

				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				for (int i = 0; i < array.length(); i++) {
					// Keep in the list only the snapshots that are expected
					// according to the version filter
					if (filterVersions == null || FILTER_VERSIONS_LASTONE.equals(filterVersions) && i > 0)
						break;
					if (filterVersions != null && FILTER_VERSIONS_LASTTWO.equals(filterVersions) && i > 1)
						break;
					JSONObject object = array.getJSONObject(i);
					SnapshotCharacteristics snap = new SnapshotCharacteristics();
					String appname = object.getJSONObject("application").getString("name");
					snap.setApplicationName(appname);
					snap.setApplicationId(app.getId());
					String href = object.getString("href");
					snap.setHref(href);
					String version = object.getJSONObject("annotation").getString("version");
					snap.setVersion(version);
					String isoDate = object.getJSONObject("annotation").getJSONObject("date").getString("isoDate");
					snap.setIsoDate(isoDate);
					Long time = object.getJSONObject("annotation").getJSONObject("date").getLong("time");
					snap.setTime(time);
					String snapshotId = href.substring(href.lastIndexOf("/") + 1);
					snap.setId(snapshotId);
					snap.setDomain(app.getDomain());

					logger.debug("          " + app.getName() + " ===> snapshot version " + version + " isoDate "
							+ isoDate + " href " + href + " snapshotid " + snapshotId);
					listSnapshots.add(snap);
				}
			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
		return listSnapshots;
	}

	/**
	 * Retrieve the snapshots list of the applications already loaded
	 * 
	 * @throws Exception
	 */
	private List<MetricReportOutput> retrieveSnapshotListAsMetricOutput() throws Exception {
		List<MetricReportOutput> localListReportOuputs = new ArrayList<MetricReportOutput>();
		List<SnapshotCharacteristics> listSnapshots = retrieveSnapshotList();

		for (SnapshotCharacteristics snapshot : listSnapshots) {
			try {
				MetricReportOutput output = new MetricReportOutput(this.reportType);
				output.setApplicationName(snapshot.getApplicationName());
				output.setApplicationId(snapshot.getApplicationId());
				output.setSnapshotHref(snapshot.getHref());
				output.setSnapshotVersion(snapshot.getVersion());
				output.setSnapshotId(snapshot.getId());
				output.setSnapshotTime(snapshot.getTime());
				output.setDomain(snapshot.getDomain());
				localListReportOuputs.add(output);
			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
		return localListReportOuputs;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param SnapshotHref
	 * @param listSnapshots
	 * @return
	 */
	private SnapshotCharacteristics getSnapshotCharacteristics(String SnapshotHref,
			List<SnapshotCharacteristics> listSnapshots) {
		for (SnapshotCharacteristics snap : listSnapshots) {
			if (snap.getHref().equals(SnapshotHref))
				return snap;
		}
		return null;
	}

	private SnapshotCharacteristics getSnapshotCharacteristics(String applicationName, Long time,
			List<SnapshotCharacteristics> listSnapshots) {
		for (SnapshotCharacteristics snap : listSnapshots) {
			if (snap.getApplicationName().equals(applicationName) && snap.getTime().equals(time))
				return snap;
		}
		return null;
	}

	private boolean isQualityRuleDataValidInRestAPI(JSONObject parentContainer, String key) throws JSONException {
		return parentContainer.has(key) && parentContainer.getString(key) != null
				&& !"null".equals(parentContainer.getString(key));
	}

	/**
	 * Get the Weighted TR for a given business criterion and for a given snapshot
	 * indicator parent and child
	 * 
	 * @param snapshotHRef
	 * @param qualityModelWeightingPerSnapshot
	 * @param bcId
	 * @param qualityIndicatorId
	 * @return
	 */
	private static WeightedQualityIndicator getWeightedQualityIndicatorBCAndTC(String snapshotHRef,
			Map<String, Set<QualityIndicator>> qualityModelWeightingPerSnapshot, Integer bcId, Integer tcId) {
		Set<QualityIndicator> setSnapshotBC = (Set<QualityIndicator>) qualityModelWeightingPerSnapshot
				.get(snapshotHRef);
		Iterator<QualityIndicator> itBC = setSnapshotBC.iterator();
		while (itBC.hasNext()) {
			QualityIndicator bc = itBC.next();
			if (bcId.equals(bc.getId())) {
				// we look at the technical criteria for the TQI
				if (tcId != null) {
					return bc.getSubQualityIndicator(tcId);
				}
			} else
				continue;
		}
		return null;
	}
	
	/**
	 * Get the Weighted QR for a given technical criterion and for a given snapshot
	 * indicator parent and child
	 * 
	 * @param snapshotHRef
	 * @param qualityModelWeightingPerSnapshot
	 * @param parentQualityIndicatorId
	 * @param qualityIndicatorId
	 * @return
	 */
	private static WeightedQualityIndicator getWeightedQualityIndicatorTCAndQR(String snapshotHRef,
			Map<String, Set<QualityIndicator>> qualityModelWeightingPerSnapshot, Integer tcId, Integer qrId) {
		Set<QualityIndicator> setSnapshotBC = (Set<QualityIndicator>) qualityModelWeightingPerSnapshot
				.get(snapshotHRef);
		Iterator<QualityIndicator> itBC = setSnapshotBC.iterator();
		while (itBC.hasNext()) {
			QualityIndicator bc = itBC.next();
			if (BUSINESS_CRITERIA_ID_TQI.equals("" + bc.getId())) {
				// we look at the technical criteria for the TQI
				if (tcId != null) {
					WeightedQualityIndicator wtc = bc.getSubQualityIndicator(tcId);
					if (wtc != null) {
						WeightedQualityIndicator wqr = wtc.getSubQualityIndicator().getMapSubQualityIndicator().get(qrId);
						return wqr;
					}
				}
			} else
				continue;
		}
		return null;
	}
	
//	private static WeightedQualityIndicator getWeightedTCById(String snapshotHRef,
//			Map<String, Set<QualityIndicator>> qualityModelWeightingPerSnapshot, Integer tcId) {
//		Set<QualityIndicator> setSnapshotBC = (Set<QualityIndicator>) qualityModelWeightingPerSnapshot
//				.get(snapshotHRef);
//		Iterator<QualityIndicator> itBC = setSnapshotBC.iterator();
//		while (itBC.hasNext()) {
//			QualityIndicator bc = itBC.next();
//			if (BUSINESS_CRITERIA_ID_TQI.equals("" + bc.getId())) {
//				// we look at the technical criteria for the TQI
//				if (tcId != null) {
//					return bc.getSubQualityIndicator(tcId);
//				}
//			} else
//				continue;
//		}
//		return null;
//	}

	/**
	 * Retrieve the Quality indicators metrics for the last snapshot
	 * 
	 * @throws Exception
	 */
	private void retrieveQualityIndicatorsMetrics() throws Exception {
		listQRReportOutputs = new ArrayList<QualityRulesReportOutput>();
		// Retrieve the snapshots characteristics
		List<SnapshotCharacteristics> listSnapshots = retrieveSnapshotList();

		Map<String, Set<QualityIndicator>> qualityModelWeightingPerSnapshot = new HashMap<String, Set<QualityIndicator>>();
		for (ApplicationCharacteristics app : this.listApplications) {
			try {
				String URI = getURIQualityIndicatorsResults(AADDomain).replaceAll("%2", app.getId());
				String responseBody = executeRestAPIRequest(URI);
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				// Snapshot loop
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					SnapshotCharacteristics snapshot = null;
					if (object.has("date") && object.getJSONObject("date") != null) {
						Long snapTime = object.getJSONObject("date").getLong("time");
						snapshot = getSnapshotCharacteristics(app.getName(), snapTime, listSnapshots);
					}
					Set<QualityIndicator> setBCForGivenSnapshot = new HashSet<QualityIndicator>();
					if (isQRFullReport()) {
						// Retrieve the business criteria and associated
						// technical criteria quality model weighting for this
						// snapshot
						for (int ii = 0; ii < LIST_BUSINESS_CRITERIA_IDS.length; ii++) {
							logger.info(
									"Loading technical criteria and quality rules weighting for business criteria : "
											+ LIST_BUSINESS_CRITERIA_IDS[ii]);
							URI = getRootURL() + "/" + AADDomain + "/quality-indicators/"
									+ LIST_BUSINESS_CRITERIA_IDS[ii] + "/snapshots/" + snapshot.getId();
							QualityIndicator bc = new QualityIndicator();
							responseBody = executeRestAPIRequest(URI);
							JSONObject jsonBC = checkJSONObjectResponseBodyErrors(responseBody);
							bc.setId(jsonBC.getInt("key"));
							bc.setName(jsonBC.getString("name"));
							bc.setHref(jsonBC.getString("href"));
							JSONArray jsonArrayTC = jsonBC.getJSONArray("gradeContributors");
							for (int l = 0; l < jsonArrayTC.length(); l++) {
								QualityIndicator tc = new QualityIndicator();
								tc.setId(jsonArrayTC.getJSONObject(l).getInt("key"));
								tc.setName(jsonArrayTC.getJSONObject(l).getString("name"));
								tc.setHref(jsonArrayTC.getJSONObject(l).getString("href"));
								// collect all quality metrics under the
								// technical criterion
								URI = getRootURL() + "/" + AADDomain + "/quality-indicators/" + tc.getId()
										+ "/snapshots/" + snapshot.getId();
								responseBody = executeRestAPIRequest(URI);
								JSONObject jsonTC = checkJSONObjectResponseBodyErrors(responseBody);
								JSONArray jsonArrayQR = jsonTC.getJSONArray("gradeContributors");
								for (int tt = 0; tt < jsonArrayQR.length(); tt++) {
									QualityIndicator qr = new QualityIndicator();
									qr.setId(jsonArrayQR.getJSONObject(tt).getInt("key"));
									qr.setName(jsonArrayQR.getJSONObject(tt).getString("name"));
									qr.setHref(jsonArrayQR.getJSONObject(tt).getString("href"));
									WeightedQualityIndicator qrw = new WeightedQualityIndicator();
									qrw.setWeight(jsonArrayQR.getJSONObject(tt).getInt("weight"));
									qrw.setCritical(jsonArrayQR.getJSONObject(tt).getBoolean("critical"));
									qrw.setSubQualityIndicator(qr);
									tc.addSubQualityIndicator(qr.getId(), qrw);
								}

								WeightedQualityIndicator tcw = new WeightedQualityIndicator();
								tcw.setWeight(jsonArrayTC.getJSONObject(l).getInt("weight"));
								tcw.setCritical(jsonArrayTC.getJSONObject(l).getBoolean("critical"));
								tcw.setSubQualityIndicator(tc);
								bc.addSubQualityIndicator(tc.getId(), tcw);
							}
							setBCForGivenSnapshot.add(bc);
						}
						// base base weight indicators
						// AAD/quality-indicators/60017/snapshots/642/base-quality-indicators
					}

					// loop on all quality-rules, technical-criteria,
					// quality-measures and quality-distributions
					JSONArray appResultsArray = object.getJSONArray("applicationResults");
					for (int j = 0; j < appResultsArray.length(); j++) {
						QualityRulesReportOutput qr = new QualityRulesReportOutput();
						qr.setSnapshotCharacteristics(snapshot);
						JSONObject jsonObjectResult = appResultsArray.getJSONObject(j);
						String type = jsonObjectResult.getString("type");
						qr.setType(type);
						// keep only "quality-rules" for the simple report
						if (!"quality-rules".equals(type)) {
							if (isQRSimpleReport()) {
								continue;
							}
						}
						if (jsonObjectResult.has("reference") && jsonObjectResult.get("reference") != null) {
							JSONObject jsonReference = jsonObjectResult.getJSONObject("reference");
							qr.setMetricId(jsonReference.getInt("key"));
							qr.setMetricName(jsonReference.getString("name"));
							qr.setHref(jsonReference.getString("href"));
							qr.setCritical(jsonReference.getBoolean("critical"));
						}

						// JSON result block
						if (jsonObjectResult.has("result") && jsonObjectResult.get("result") != null) {
							if (isQualityRuleDataValidInRestAPI(jsonObjectResult.getJSONObject("result"), "grade")) {
								Double grade = jsonObjectResult.getJSONObject("result").getDouble("grade");
								qr.setGrade(grade);
							}
							logger.debug(qr.toString());

							// Quality rule, not Technical criteria
							if ("quality-rules".equals(type)) {
								if (jsonObjectResult.getJSONObject("result").has("violationRatio")) {
									JSONObject vratio = jsonObjectResult.getJSONObject("result")
											.getJSONObject("violationRatio");
									if (isQualityRuleDataValidInRestAPI(vratio, "totalChecks")) {
										Integer totalChecks = vratio.getInt("totalChecks");
										qr.setTotalChecks(totalChecks);
									}
									if (isQualityRuleDataValidInRestAPI(vratio, "failedChecks")) {
										Integer failedChecks = vratio.getInt("failedChecks");
										qr.setFailedChecks(failedChecks);
									}
									if (isQualityRuleDataValidInRestAPI(vratio, "successfulChecks")) {
										Integer sucessfullChecks = vratio.getInt("successfulChecks");
										qr.setSuccessfulChecks(sucessfullChecks);
									}
									if (isQualityRuleDataValidInRestAPI(vratio, "ratio")) {
										Double complianceRatio = vratio.getDouble("ratio");
										qr.setComplianceRatio(complianceRatio);
									}
								}
//								WeightedQualityIndicator wtc = null;
//								if ("technical-criteria".equals(type)) {
//									wtc = getWeightedTCById(qr.getSnapshotCharacteristics().getHref(),
//											qualityModelWeightingPerSnapshot, qr.getMetricId());
//								}
							}
							// Quality rule or Technical criteria
							if ("quality-rules".equals(type) || "technical-criteria".equals(type)) {
								Integer addedViolations;
								Integer removedViolations; 
								Integer addedCriticalViolations;
								Integer removedCriticalViolations;
								if (jsonObjectResult.getJSONObject("result").has("evolutionSummary")) {
									JSONObject vevolsum = jsonObjectResult.getJSONObject("result")
											.getJSONObject("evolutionSummary");
									if (isQualityRuleDataValidInRestAPI(vevolsum, "addedViolations")) {
										addedViolations = vevolsum.getInt("addedViolations");
										qr.setAddedViolations(addedViolations);
									}
									if (isQualityRuleDataValidInRestAPI(vevolsum, "removedViolations")) {
										removedViolations = vevolsum.getInt("removedViolations");
										qr.setRemovedViolations(removedViolations);
									}
									if (isQualityRuleDataValidInRestAPI(vevolsum, "addedCriticalViolations")) {
										addedCriticalViolations = vevolsum.getInt("addedCriticalViolations");
										qr.setAddedCriticalViolations(addedCriticalViolations);
									}
									if (isQualityRuleDataValidInRestAPI(vevolsum, "removedCriticalViolations")) {
										removedCriticalViolations = vevolsum
												.getInt("removedCriticalViolations");
										qr.setRemovedCriticalViolations(removedCriticalViolations);
									}
								}
							} else {
								// N/A
								qr.setAddedViolations(-1);
								qr.setRemovedViolations(-1);
								qr.setAddedCriticalViolations(-1);
								qr.setRemovedCriticalViolations(-1);
							}
							// Only Technical criteria
							if ("technical-criteria".equals(type)) {
								if (jsonObjectResult.getJSONObject("result").has("evolutionSummary")) {
									JSONObject vevolsum = jsonObjectResult.getJSONObject("result")
											.getJSONObject("evolutionSummary");
									if (isQualityRuleDataValidInRestAPI(vevolsum, "totalCriticalViolations")) {
										Integer totalCriticalViolations = vevolsum
												.getInt("totalCriticalViolations");
										qr.setTotalCriticalViolations(totalCriticalViolations);
									}
									if (isQualityRuleDataValidInRestAPI(vevolsum, "totalViolations")) {
										Integer totalViolations = vevolsum.getInt("totalViolations");
										qr.setTotalViolations(totalViolations);
									}
								}
							} else {
								// N/A
								qr.setTotalCriticalViolations(-1);
								qr.setTotalViolations(-1);
							}
						}
						// Append the list
						listQRReportOutputs.add(qr);
					} // loop on all quality-rules, technical-criteria,
						// quality-measures and quality-distributions
					if (isQRFullReport()) {
						qualityModelWeightingPerSnapshot.put(snapshot.getHref(), setBCForGivenSnapshot);
					}
				} // end loop snapshots
			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}

		}
		// collect the technical criteria and business criteria
		if (isQRFullReport()) {
			// Extract the complement information for the quality rules, ie
			// - Quality rules thresholds
			// - Parent Technical criterion or business criterion Id, Name,
			// Weight
			for (QualityRulesReportOutput qroutput : listQRReportOutputs) {
				String URI = getRootURL() + "/" + qroutput.getHref();
				String responseBody = executeRestAPIRequest(URI);
				JSONObject jsonObjectQR = checkJSONObjectResponseBodyErrors(responseBody);
				if ("quality-rules".equals(qroutput.getType()) && jsonObjectQR.has("thresholds")
						&& jsonObjectQR.get("thresholds") != null
						&& (jsonObjectQR.get("thresholds") instanceof JSONArray)) {
					JSONArray thresholds = (JSONArray) jsonObjectQR.get("thresholds");
					qroutput.setThreshold1((Double) thresholds.get(0));
					qroutput.setThreshold2((Double) thresholds.get(1));
					qroutput.setThreshold3((Double) thresholds.get(2));
					qroutput.setThreshold4((Double) thresholds.get(3));
				}
				JSONArray jsonFirstArray = (JSONArray) jsonObjectQR.get("gradeAggregators");
				for (int j = 0; j < jsonFirstArray.length(); j++) {

					JSONObject jsonObjectgradeAgg = jsonFirstArray.getJSONObject(j);
					// technical criteria for this rule
					String tcname = jsonObjectgradeAgg.getString("name");
					Integer tckey = jsonObjectgradeAgg.getInt("key");
					String tchref = jsonObjectgradeAgg.getString("href");
					if ("quality-rules".equals(qroutput.getType()) & j > 1) {
						logger.warn("More than one TC for this QR : " + qroutput.getMetricId());
					}
					if (!"technical-criteria".equals(qroutput.getType())) {
						// Get the weight (relation between the technical criterion and the quality rule)
						WeightedQualityIndicator wtcqr = getWeightedQualityIndicatorTCAndQR(qroutput.getSnapshotCharacteristics().getHref(),
								qualityModelWeightingPerSnapshot, tckey, qroutput.getMetricId());
						if (wtcqr == null)
							;//System.out.println("STOP1");
						else
							qroutput.setWeight(wtcqr.getWeight());
					} else {
						// technical-criteria
						QualityRulesReportOutput tcqr = (QualityRulesReportOutput) qroutput.clone();
						listQRReportOutputsWithTCAndBC.add(tcqr);
					}
					
					
					JSONArray arrayParentBC = jsonObjectgradeAgg.getJSONArray("gradeAggregators");
					for (int k = 0; k < arrayParentBC.length(); k++) {
						JSONObject parentBC = arrayParentBC.getJSONObject(k);
						String bcname = parentBC.getString("name");
						Integer bckey = parentBC.getInt("key");
						String bchref = parentBC.getString("href");
						
						// Get the weight (relation between the business criterion and the technical criterion
						WeightedQualityIndicator wbctc = getWeightedQualityIndicatorBCAndTC(qroutput.getSnapshotCharacteristics().getHref(),
								qualityModelWeightingPerSnapshot, bckey, tckey);
						if (wbctc == null)
							;//System.out.println("STOP2");
						else
							qroutput.setWeightTechnicalCriterion(wbctc.getWeight());						

						// This is applicable only for non technical-criteria
						QualityRulesReportOutput qrwithBC = (QualityRulesReportOutput) qroutput.clone();
						qrwithBC.setTechnicalCriterionId(tckey);
						qrwithBC.setTechnicalCriterionName(tcname);
						qrwithBC.setBusinessCriterionId(bckey);
						qrwithBC.setBusinessCriterionName(bcname);						
						
						listQRReportOutputsWithTCAndBC.add(qrwithBC);
					}
				}
			}
		}

	}

	/**
	 * Retrieve the Business criteria Metrics
	 * 
	 * @throws Exception
	 */
	private void retrieveBusinessCriteriasMetrics() throws Exception {
		String[] arrAEDDomains = AEDDomains.split(",");
		for (int it = 0; it < arrAEDDomains.length; it++) {
			String AEDDomain = arrAEDDomains[it];
			try {
				String responseBody = executeRestAPIRequest(getURIBusinessCriteriasCMetrics(AEDDomain));

				double value_60011 = 0;
				double value_60012 = 0;
				double value_60013 = 0;
				double value_60014 = 0;
				double value_60016 = 0;

				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String applicationName = object.getJSONObject("application").getString("name");
					String applicationHRef = object.getJSONObject("application").getString("href");
					String applicationSnapshotHref = object.getJSONObject("applicationSnapshot").getString("href");
					String isoDate = object.getJSONObject("date").getString("isoDate");
					Long time = object.getJSONObject("date").getLong("time");
					int number = object.getInt("number");
					MetricReportOutput output = getSnapshotOutput(applicationSnapshotHref);

					logger.debug("applicationSnapshotHref=" + applicationSnapshotHref);

					JSONArray appResultsArray = object.getJSONArray("applicationResults");

					for (int j = 0; j < appResultsArray.length(); j++) {
						JSONObject jsonObjectResult = appResultsArray.getJSONObject(j);
						/*
						 * * TQI = "60017"; EFFICIENCY = "60014"; ROBUS =
						 * "60013"; SECU = "60016"; CHANG = "60012"; TRANS =
						 * "60011"; CODING BEST PRAC = "66031"
						 */
						String strkey = jsonObjectResult.getJSONObject("reference").getString("key");
						Double value = jsonObjectResult.getJSONObject("result").getDouble("grade");

						if (BUSINESS_CRITERIA_ID_TQI.equals(strkey)) {
							logger.trace("    TQI " + value);
							output.setBcTQI(value);
						}
						if (BUSINESS_CRITERIA_ID_EFFICIENCY.equals(strkey)) {
							logger.trace("    Efficiency " + value);
							output.setBcEffi(value);
							value_60014 = value;
						}
						if (BUSINESS_CRITERIA_ID_ROBUSTNESS.equals(strkey)) {
							logger.trace("    Robustness " + value);
							output.setBcRobus(value);
							value_60013 = value;

						}
						if (BUSINESS_CRITERIA_ID_SECURITY.equals(strkey)) {
							logger.trace("    Security " + value);
							output.setBcSecu(value);
							value_60016 = value;
						}
						if (BUSINESS_CRITERIA_ID_CHANGEABILITY.equals(strkey)) {
							logger.trace("    Changeability " + value);
							output.setBcChang(value);
							value_60012 = value;

						}
						if (BUSINESS_CRITERIA_ID_TRANSFERABILITY.equals(strkey)) {
							logger.trace("    Transferability " + value);
							output.setBcTrans(value);
							value_60011 = value;

						}
						if (BUSINESS_CRITERIA_ID_CODING_PROGRAMMING_BEST_PRACTICES.equals(strkey)) {
							logger.trace("    Coding Best Practices " + value);
							output.setBcCodinBP(value);
						}

						output.setBcApplicationOp((value_60013 + value_60014 + value_60016) / 3);
						output.setBcCodeMaintain((value_60011 + value_60012) / 2);
					}

				}

			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve the Business criteria Metrics
	 * 
	 * @throws Exception
	 */
	private void retrieveTechnicalSizeMetrics() throws Exception {
		String[] arrAEDDomains = AEDDomains.split(",");
		for (int it = 0; it < arrAEDDomains.length; it++) {
			String AEDDomain = arrAEDDomains[it];
			try {
				String responseBody = executeRestAPIRequest(getURISizingMeasuresTechnicalSizeMeasures(AEDDomain));
				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String applicationSnapshotHref = object.getJSONObject("applicationSnapshot").getString("href");
					MetricReportOutput output = getSnapshotOutput(applicationSnapshotHref);

					logger.debug("applicationSnapshotHref=" + applicationSnapshotHref);

					JSONArray appResultsArray = object.getJSONArray("applicationResults");

					for (int j = 0; j < appResultsArray.length(); j++) {
						JSONObject jsonObjectResult = appResultsArray.getJSONObject(j);
						/*
						 * LOCs = "60017";
						 */
						String strkey = jsonObjectResult.getJSONObject("reference").getString("key");
						Integer value = jsonObjectResult.getJSONObject("result").getInt("value");

						if ("10151".equals(strkey)) {
							// logger.info(" LOC " + value);
							output.setLoc(value);
						}
						if ("10107".equals(strkey))
							output.setNbCommentLines(value);
						if ("10109".equals(strkey))
							output.setNbCommentedOutLines(value);
						if ("10152".equals(strkey))
							output.setNbArtifacts(value);
						if ("10154".equals(strkey))
							output.setNbFiles(value);
						if ("10156".equals(strkey))
							output.setNbPrograms(value);
						if ("10155".equals(strkey))
							output.setNbClasses(value);
						if ("10161".equals(strkey))
							output.setNbMethods(value);
						if ("10166".equals(strkey))
							output.setNbPackages(value);
						if ("10163".equals(strkey))
							output.setNbTables(value);
						if ("19175".equals(strkey))
							output.setNbFuncProc(value);
						if ("10158".equals(strkey))
							output.setNbSQLArtifacts(value);
						if ("10160".equals(strkey))
							output.setNbInterfaces(value);
						if ("19180".equals(strkey))
							output.setNbIncludes(value);
						if ("19181".equals(strkey))
							output.setNbFunctionPools(value);
						if ("19191".equals(strkey))
							output.setNbABAPUserExits(value);
						if ("19192".equals(strkey))
							output.setNbABAPTransactions(value);

					}

				}

			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve additional metrics from the central schemas, if available
	 * 
	 * @throws Exception
	 */
	private void retrieveDBCentralSchemaMetrics() throws Exception {
		if (getCssDbHostname() == null || getCssDbListCentralSchemas() == null) {
			return;
		}
		String[] listCentralSchemas = getCssDbListCentralSchemas().split(",");
		for (int i = 0; i < listCentralSchemas.length; i++) {
			List<CentralSnapshotMetrics> listCentralSnapshotMetrics = CSSDbTripletQueryUtil
					.runAddedDeletedModifiedArtifactQueries(getCssDbHostname(), getCssDbPort(), getCssDbDatabase(),
							getCssDbUser(), getCssDbPassword(), listCentralSchemas[i], logger);
			if (listCentralSnapshotMetrics != null)
				for (CentralSnapshotMetrics centralSnapshotMetrics : listCentralSnapshotMetrics) {
					MetricReportOutput output = getSnapshotOutput(centralSnapshotMetrics.getApplicationName(),
							centralSnapshotMetrics.getSnapshotId());
					if (output != null) {
						output.setNbArtifactsAdded(centralSnapshotMetrics.getAddedArtifacts());
						output.setNbArtifactsModified(centralSnapshotMetrics.getModifiedArtifacts());
						output.setNbArtifactsDeleted(centralSnapshotMetrics.getDeletedArtifacts());
						output.setLocCentral(centralSnapshotMetrics.getLoc());
					}
				}

		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve the evolution summary Metrics
	 * 
	 * @throws Exception
	 */
	private void retrieveEvolutionSummaryMetrics() throws Exception {
		String[] arrAEDDomains = AEDDomains.split(",");
		for (int it = 0; it < arrAEDDomains.length; it++) {
			String AEDDomain = arrAEDDomains[it];
			try {
				String responseBody = executeRestAPIRequest(getURIEvolutionSummary(AEDDomain));
				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String applicationSnapshotHref = object.getJSONObject("applicationSnapshot").getString("href");
					MetricReportOutput output = getSnapshotOutput(applicationSnapshotHref);
					logger.debug("applicationSnapshotHref=" + applicationSnapshotHref);
					JSONArray appResultsArray = object.getJSONArray("applicationResults");

					for (int j = 0; j < appResultsArray.length(); j++) {
						JSONObject jsonObjectResult = appResultsArray.getJSONObject(j);
						JSONObject o = jsonObjectResult.getJSONObject("result").getJSONObject("evolutionSummary");
						output.setTotalCritViolations(o.getInt("totalCriticalViolations"));
						output.setAddedCritViolations(o.getInt("addedCriticalViolations"));
						output.setRemovedCritViolations(o.getInt("removedCriticalViolations"));
						output.setCritViolationsInNewAndModifiedCode(
								o.getInt("criticalViolationsInNewAndModifiedCode"));
						output.setTotalViolations(o.getInt("totalViolations"));
						output.setAddedViolations(o.getInt("addedViolations"));
						output.setRemovedViolations(o.getInt("removedViolations"));
						output.setViolationsInNewAndModifiedCode(o.getInt("violationsInNewAndModifiedCode"));
					}
				}

			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve the Functional weigth Metrics
	 * 
	 * @throws Exception
	 */
	private void retrieveFunctionalWeightMeasures() throws Exception {
		String[] arrAEDDomains = AEDDomains.split(",");
		for (int it = 0; it < arrAEDDomains.length; it++) {
			String AEDDomain = arrAEDDomains[it];
			try {
				String responseBody = executeRestAPIRequest(getURISizingMeasuresFunctionalWeightMeasures(AEDDomain));
				// Parsing the JSON structure
				JSONArray array = checkJSONArrayResponseBodyErrors(responseBody);
				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String applicationName = object.getJSONObject("application").getString("name");
					String applicationHRef = object.getJSONObject("application").getString("href");
					String applicationSnapshotHref = object.getJSONObject("applicationSnapshot").getString("href");
					String isoDate = object.getJSONObject("date").getString("isoDate");
					Long time = object.getJSONObject("date").getLong("time");
					int number = object.getInt("number");
					// System.out.println("applicationSnapshotHref="+applicationSnapshotHref);

					JSONArray appResultsArray = object.getJSONArray("applicationResults");
					MetricReportOutput output = getSnapshotOutput(applicationSnapshotHref);
					for (int j = 0; j < appResultsArray.length(); j++) {
						JSONObject jsonObjectResult = appResultsArray.getJSONObject(j);
						/*
						 * 10450 Total AEP 10451 Added AEP 10452 Deleted AEP
						 * 10453 Modified AEP
						 */
						// Total AEP
						String strkey = jsonObjectResult.getJSONObject("reference").getString("key");
						double valueDouble = jsonObjectResult.getJSONObject("result").getDouble("value");
						int valueInt = jsonObjectResult.getJSONObject("result").getInt("value");

						if ("10450".equals(strkey)) {
							// System.out.println(" Total AEP " + value);
							output.setNbAEPTotal(valueInt);
						}
						if ("10451".equals(strkey)) {
							// System.out.println(" Added AEP " + value);
							output.setNbAEPAdded(valueInt);
						}
						if ("10452".equals(strkey)) {
							// System.out.println(" Deleted AEP " + value);
							output.setNbAEPDeleted(valueInt);
						}
						if ("10453".equals(strkey)) {
							// System.out.println(" Modified AEP " + value);
							output.setNbAEPModifed(valueInt);
						}
						if ("10202".equals(strkey))
							output.setNbAFPTotalFP(valueInt);
						if ("10203".equals(strkey))
							output.setNbAFPDatafunctionsFP(valueInt);
						if ("10204".equals(strkey))
							output.setNbAFPTransactionsFP(valueInt);

						if ("10360".equals(strkey))
							output.setNbImplementationPointsAEFP(valueDouble);
						if ("10362".equals(strkey))
							output.setNbImplementationPointsAETP(valueDouble);
						if ("10400".equals(strkey))
							output.setNbAEFPAdded(valueInt);
						if ("10401".equals(strkey))
							output.setNbAEFPAddedDataFunctions(valueInt);
						if ("10402".equals(strkey))
							output.setNbAEFPAddedTransactionalFunctions(valueInt);
						if ("10410".equals(strkey))
							output.setNbAEFPDeleted(valueInt);
						if ("10411".equals(strkey))
							output.setNbAEFPDeletedDataFunctions(valueInt);
						if ("10412".equals(strkey))
							output.setNbAEFPDeletedTransactionalFunctions(valueInt);
						if ("10420".equals(strkey))
							output.setNbAEFPModified(valueInt);
						if ("10421".equals(strkey))
							output.setNbAEFPModifiedDataFunctions(valueInt);
						if ("10422".equals(strkey))
							output.setNbAEFPModifiedTransactionalFunctions(valueInt);
						if ("10430".equals(strkey))
							output.setNbAEFP(valueInt);
						if ("10431".equals(strkey))
							output.setNbAEFPDataFunctions(valueInt);
						if ("10432".equals(strkey))
							output.setNbAEFPTransactionalFunctions(valueInt);

						if ("10440".equals(strkey))
							output.setNbAETP(valueInt);
						if ("10441".equals(strkey))
							output.setNbAETPAdded(valueInt);
						if ("10442".equals(strkey))
							output.setNbAETPDeleted(valueInt);
						if ("10443".equals(strkey))
							output.setNbAETPModified(valueInt);

						if ("10460".equals(strkey))
							output.setNbEvovedTransactions(valueInt);
						if ("10461".equals(strkey))
							output.setNbTransactions(valueInt);
						if ("10470".equals(strkey))
							output.setNbEnhancementSharedArtifacts(valueInt);
						if ("10471".equals(strkey))
							output.setNbEnhancementSpecificArtifacts(valueInt);
						if ("10350".equals(strkey))
							output.setEffortComplexity(valueInt);
						if ("10359".equals(strkey))
							output.setEquivalentRatio(valueInt);

						if ("10506".equals(strkey))
							output.setNbDecisionsPoints(valueInt);
						if ("10201".equals(strkey))
							output.setNbBFP(valueDouble);

						// EFP metrics
						if ("10300".equals(strkey))
							output.setNbEFPTotalAdded(valueInt);
						if ("10301".equals(strkey))
							output.setNbEFPDataFPAdded(valueInt);
						if ("10302".equals(strkey))
							output.setNbEFPTransactionalFPAdded(valueInt);
						if ("10310".equals(strkey))
							output.setNbEFPTotalModified(valueInt);
						if ("10311".equals(strkey))
							output.setNbEFPDataFPModified(valueInt);
						if ("10312".equals(strkey))
							output.setNbEFPTransactionalFPModified(valueInt);
						if ("10320".equals(strkey))
							output.setNbEFPTotalDeleted(valueInt);
						if ("10321".equals(strkey))
							output.setNbEFPDataFPDeleted(valueInt);
						if ("10322".equals(strkey))
							output.setNbEFPTransactionalFPDeleted(valueInt);
						if ("10330".equals(strkey))
							output.setNbEFPTotalUnchanged(valueInt);
						if ("10331".equals(strkey))
							output.setNbEFPDataFPUnchanged(valueInt);
						if ("10332".equals(strkey))
							output.setNbEFPTransactionalFPUnchanged(valueInt);
						if ("10340".equals(strkey))
							output.setNbEFPTotal(valueInt);
						if ("10341".equals(strkey))
							output.setNbEFPDataFP(valueInt);
						if ("10342".equals(strkey))
							output.setNbEFPTransactionalFP(valueInt);
					}
				}
			} catch (Exception l_exception) {
				logger.error(l_exception.getMessage(), l_exception);
				throw l_exception;
			} finally {
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Report the outputs
	 * 
	 * @throws Exception
	 */
	protected void reportMetricsOutputstoLog() throws Exception {
		logger.info("==============================");
		String headerForFullReport = "";
		if (isMetricsKPIReport()) {
			headerForFullReport = "Application;Version;TQI;Efficiency;Robustness;Security;Changeability;Transferability;Coding Best Practices;Application Operability;Code Maintenability;Nb LOC;AEP;AEP Added;AEP Deleted;AEP Modified;";
		}
		if (isMetricsFullReport()) {
			headerForFullReport = "Application;Version;TQI;Efficiency;Robustness;Security;Changeability;Transferability;Coding Best Practices;Application Operability;Code Maintenability;Nb LOC;AFP;AFP Datafunctions FP;AFP transactions FP;AEP;AEP Added;AEP Deleted;AEP Modified;";
		}
		logger.info(headerForFullReport);
		for (MetricReportOutput out : listMetricsReportOutputs) {
			logger.info(out.toString());
		}
		logger.info("==============================");
	}

	/**
	 * Report the outputs
	 * 
	 * @throws Exception
	 */
	protected void logInputs() throws Exception {
		logger.info("==============================");
		logger.info("Version:" + VERSION);
		logger.info("Parameters:");
		logger.info("Rest API URL:" + getRootURL());
		logger.info("AADDomain:" + getAADDomain());
		logger.info("AEDDomains:" + getAEDDomains());
		logger.info("User:" + getUser());
		logger.info("Password:" + "*********");
		logger.info("Encoding:" + getEncodingToUse());
		logger.info("Environment:" + getEnvironment());
		logger.info("Report type:" + getReportType());
		logger.info("Version filter:" + this.filterVersions);
		logger.info("Application filter:" + this.filterApplicationNames);
		logger.info("==============================");
	}

	/**
	 * Intialize the attribute depending if we are loading in health or
	 * engineering RestAPI
	 */
	private void initDomainApplications() throws Exception {

		// for metrics report we take the data from engineering RestAPI
		if (isMetricsReport()) {
			String[] arrAEDDomains = AEDDomains.split(",");
			for (int i = 0; i < arrAEDDomains.length; i++) {
				String AEDDomain = arrAEDDomains[i];
				this.listApplications.addAll(retrieveApplicationList(AEDDomain));
			}
		}
		// for QR reports we take the data from health RestAPI
		if (isQRReport()) {
			this.listApplications.addAll(retrieveApplicationList(AADDomain));
		}
		logger.info("Application list :");
		if (this.listApplications.size() == 0)
			logger.info(" Empty");
		for (ApplicationCharacteristics appchar : this.listApplications) {
			logger.info(" -" + appchar.getName() + "/" + appchar.getHref());
		}
		this.listMetricsReportOutputs = retrieveSnapshotListAsMetricOutput();
	}

	/**
	 * Retrieve the data from the CAST Rest API
	 * 
	 * @throws Exception
	 */
	private void run() throws Exception {
		logInputs();
		initDomainApplications();

		if (isQRReport()) {
			retrieveQualityIndicatorsMetrics();
		}

		if (isMetricsReport()) {
			retrieveBusinessCriteriasMetrics();
			retrieveFunctionalWeightMeasures();

			if (REPORTTYPE_METRICS_FULLREPORT.equals(this.reportType)) {
				retrieveTechnicalSizeMetrics();
				retrieveEvolutionSummaryMetrics();
				if (bRetrieveActionPlanItems)
					retrieveActionPlans();
				// from central schema
				if (bRetrieveDBCentralSchemaMetrics)
					retrieveDBCentralSchemaMetrics();

				// compute CV / AFP and CV AM / AEP
				// Specific to Telefonica
				for (MetricReportOutput output : listMetricsReportOutputs) {

					if (output.getNbAEPTotal() > 0.0) {
						double d1 = (new Double(output.getCritViolationsInNewAndModifiedCode())
								/ new Double(output.getNbAEPTotal()));
						output.setAddedCVOnNewCodePerAEP(d1);
					}
					if (output.getNbAFPTotalFP() > 0.0) {
						double d2 = (new Double(output.getTotalCritViolations()).doubleValue()
								/ new Double(output.getNbAFPTotalFP()).doubleValue());
						output.setCVPerAFP(d2);
					}
				}
			}
			reportMetricsOutputstoLog();
		}

		// Generation the excel file(s)
		if (isMetricsReport())
			createMetricReport();
		else if (isQRReport())
			createQRReport();

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private MetricReportOutput getSnapshotOutput(String applicationName, Long time) {
		for (MetricReportOutput output : listMetricsReportOutputs) {
			if (output.getApplicationName().equals(applicationName) && output.getSnapshotTime().equals(time))
				return output;
		}
		return null;
	}

	private MetricReportOutput getSnapshotOutput(String applicationName, Integer snapshot_id) {
		for (MetricReportOutput output : listMetricsReportOutputs) {
			if (output.getApplicationName().equals(applicationName) && output.getSnapshotId().equals(snapshot_id))
				return output;
		}
		return null;
	}

	private MetricReportOutput getSnapshotOutput(String SnapshotHref) {
		for (MetricReportOutput output : listMetricsReportOutputs) {
			if (output.getSnapshotHref().equals(SnapshotHref))
				return output;
		}
		return null;

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		RestAPIReports reportgen = new RestAPIReports();
		reportgen.parseCmdLineParameters(args);
		reportgen.checkParameters();

		reportgen.run();
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getRootURL() {
		return rootURL;
	}

	public void setRootURL(String rootURL) {
		this.rootURL = rootURL;
	}

	public String getAADDomain() {
		return AADDomain;
	}

	public void setAADDomain(String domain) {
		this.AADDomain = domain;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEnvironment() {
		return environment;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getCssDbHostname() {
		return cssDbHostname;
	}

	public void setCssDbHostname(String cssDbHostname) {
		this.cssDbHostname = cssDbHostname;
	}

	public String getCssDbListCentralSchemas() {
		return cssDbListCentralSchemas;
	}

	public void setCssDbListCentralSchemas(String cssDbListCentralSchemas) {
		this.cssDbListCentralSchemas = cssDbListCentralSchemas;
	}

	public String getAEDDomains() {
		return AEDDomains;
	}

	public void setAEDDomains(String aEDDomains) {
		AEDDomains = aEDDomains;
	}

	public String getCssDbPort() {
		return cssDbPort;
	}

	public void setCssDbPort(String cssDbPort) {
		this.cssDbPort = cssDbPort;
	}

	public String getCssDbDatabase() {
		return cssDbDatabase;
	}

	public void setCssDbDatabase(String cssDbDatabase) {
		this.cssDbDatabase = cssDbDatabase;
	}

	public String getCssDbUser() {
		return cssDbUser;
	}

	public void setCssDbUser(String cssDbUser) {
		this.cssDbUser = cssDbUser;
	}

	public String getCssDbPassword() {
		return cssDbPassword;
	}

	public void setCssDbPassword(String cssDbPassword) {
		this.cssDbPassword = cssDbPassword;
	}

	private boolean isTelefonicaSpecificApplicationWithNoFP(String applicationName) {
		for (int i = 0; i < TELEFONICASPECIFIC_APPLICATIONS_WITH_NOFP.length; i++) {
			if (TELEFONICASPECIFIC_APPLICATIONS_WITH_NOFP[i].equals(applicationName))
				return true;
		}
		return false;
	}

	private String getFPValue(String value, boolean isFPApp) {
		if (isFPApp)
			return value;
		else
			return "N/A";
	}

	private int getFPValue(int value, boolean isFPApp) {
		if (isFPApp)
			return value;
		else
			return -1;
	}

	private double getFPValue(double value, boolean isFPApp) {
		if (isFPApp)
			return value;
		else
			return -1.0;
	}

	private void createQRReport() throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Report");
		String[] nameCells = null;

		if (REPORTTYPE_QR_SIMPLEREPORT.equals(reportType)) {
			nameCells = new String[] { "Application", "Snapshot Version", "Type", "Quality rule Id",
					"Quality rule name", "Critical", "Grade", "Failed Checks", "Sucessful Checks", "Total Checks",
					"Compliance Ratio", "Added violations", "Removed violations" };
		} else if (REPORTTYPE_QR_FULLREPORT.equals(reportType)) {
			nameCells = new String[] { "Application", "Snapshot Version", "BC Id", "BC Name", "TC Id", "TC Name", "TC Weight",
					"Type", "Quality rule Id", "Quality rule name", "Critical", "Weight", "Grade", "Failed Checks",
					"Sucessful Checks", "Total Checks", "Compliance Ratio", "Total critical violations",
					"Total violations", "Added critical violations", "Removed critical violations", "Added violations",
					"Removed violations", "Threshold 1", "Threshold 2", "Threshold 3", "Threshold 4", };
		}
		logger.info("Creating excel file " + getFileName());

		Row row = sheet.createRow((short) 0);
		Cell cell;
		int headerColNum = 0;
		int rowNum = 1;
		for (String nc : nameCells) {
			cell = row.createCell(headerColNum++);
			cell.setCellValue(nc);
		}

		List<QualityRulesReportOutput> listQR = null;
		if (isQRSimpleReport()) {
			listQR = listQRReportOutputs;
		} else if (isQRFullReport()) {
			listQR = listQRReportOutputsWithTCAndBC;
		}

		// Data
		for (QualityRulesReportOutput obj : listQR) {
			int i = 0;
			row = sheet.createRow(rowNum++);
			cell = row.createCell(i++);
			cell.setCellValue(obj.getSnapshotCharacteristics().getApplicationName());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getSnapshotCharacteristics().getVersion());
			if (REPORTTYPE_QR_FULLREPORT.equals(reportType)) {
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBusinessCriterionId());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBusinessCriterionName());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getTechnicalCriterionId());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getTechnicalCriterionName());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getWeightTechnicalCriterion());				
			}
			cell = row.createCell(i++);
			cell.setCellValue(obj.getType());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getMetricId());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getMetricName());
			cell = row.createCell(i++);
			cell.setCellValue(obj.isCritical());
			if (REPORTTYPE_QR_FULLREPORT.equals(reportType)) {
				cell = row.createCell(i++);
				cell.setCellValue(obj.getWeight());					
			}
			cell = row.createCell(i++);
			if (obj.getGrade() != null)
				cell.setCellValue(obj.getGrade());
			else
				cell.setCellValue(-1);
			cell = row.createCell(i++);
			cell.setCellValue(obj.getFailedChecks());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getSuccessfulChecks());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getTotalChecks());

			cell = row.createCell(i++);
			if (obj.getComplianceRatio() != null)
				cell.setCellValue(obj.getComplianceRatio());
			else
				cell.setCellValue(-1);

			if (REPORTTYPE_QR_FULLREPORT.equals(reportType)) {
				cell = row.createCell(i++);
				cell.setCellValue(obj.getTotalCriticalViolations());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getTotalViolations());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getAddedCriticalViolations());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getRemovedCriticalViolations());
			}
			cell = row.createCell(i++);
			cell.setCellValue(obj.getAddedViolations());
			cell = row.createCell(i++);
			cell.setCellValue(obj.getRemovedViolations());

			if (REPORTTYPE_QR_FULLREPORT.equals(reportType)) {
				cell = row.createCell(i++);
				if (obj.getThreshold1() != null)
					cell.setCellValue(obj.getThreshold1());
				else
					cell.setCellValue(-1);

				cell = row.createCell(i++);
				if (obj.getThreshold2() != null)
					cell.setCellValue(obj.getThreshold2());
				else
					cell.setCellValue(-1);

				cell = row.createCell(i++);
				if (obj.getThreshold3() != null)
					cell.setCellValue(obj.getThreshold3());
				else
					cell.setCellValue(-1);

				cell = row.createCell(i++);
				if (obj.getThreshold4() != null)
					cell.setCellValue(obj.getThreshold4());
				else
					cell.setCellValue(-1);
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(getFileName());
			workbook.write(outputStream);
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private void createMetricReport() throws Exception {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Report");
		String[] nameGroupCells = null;
		String[] nameCells = null;

		if (REPORTTYPE_METRICS_KPIREPORT.equals(reportType)) {
			nameGroupCells = new String[] { "", "", "Business criteria", "", "", "", "", "", "", "", "", "AEP", "", "",
					"" };
			nameCells = new String[] { "Application", "Version", "TQI", "Efficiency", "Robustness", "Security",
					"Changeability", "Transferability", "Coding Best Practices", "Application Operability",
					"Code Maintenability", "AEP", "AEP Added", "AEP Deleted", "AEP Modified" };
		}
		if (REPORTTYPE_METRICS_FULLREPORT.equals(reportType)) {

			// full including central schema metrics
			if (cssDbHostname != null) {
				nameGroupCells = new String[] { "", "", "Business criteria", "", "", "", "", "", "", "", "", "AFP", "",
						"", "AEP", "", "", "", "", "", "AEFP", "", "", "", "", "", "", "", "", "", "", "", "AETP", "",
						"", "", "Misc AEP", "", "", "", "", "", "", "", "EFP", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "Critical violations", "", "", "", "Non critical violations", "", "", "",
						"Technical size", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"", "", "Action plan Added", "", "", "", "", "Action plan Pending", "", "", "", "",
						"Action plan Solved", "", "", "", "", };
				nameCells = new String[] { "Application", "Version", "TQI", "Efficiency", "Robustness", "Security",
						"Changeability", "Transferability", "Coding Best Practices", "Application Operability",
						"Code Maintenability", "AFP", "AFP datafunctions FP", "AFP transactions FP", "AEP", "AEP Added",
						"AEP Deleted", "AEP Modified", "CV on new and modified code / AEP", "CV / AFP", "AEFP",
						"AEFP DF", "AEFP TF", "AEFP Added", "AEFP Deleted", "AEFP Modified", "AEFP Added DF",
						"AEFP Added TF", "AEFP Deleted DF", "AEFP Deleted TF", "AEFP Modified DF", "AEFP Modified TF",
						"AETP", "AETP Added", "AETP Deleted", "AETP Modified", "#transactions", "#evolved transactions",
						"#enhanced shared artifacts", "#enhanced specific artifacts", "Effort Complexity",
						"Equivalent ratio", "ImplementationPointsAEFP", "ImplementationPointsAETP", "EFP", "EFP DF",
						"EFP TF", "EFP Added", "EFP DF Added", "EFP TF Added", "EFP Modified", "EFP DF Modified",
						"EFP TF Modified", "EFP Deleted", "EFP DF Deleted", "EFP TF Deleted", "EFP Unchanged",
						"EFP DF Unchanged", "EFP TF Unchanged", "Total", "Added", "Removed", "InNewAndModified Code",
						"Total", "Added", "Removed", "In NewAndModified Code", "LOC", "Comment lines",
						"Commented-out lines", "#Artifacts", "#Files", "#Programs", "#Classes", "#Methods",
						"#Interfaces", "#Packages", "#Tables", "#Functions and Procedures", "#SQL artifacts",
						"#Includes", "#Functions pools", "#ABAP User exits", "#ABAP Transactions", "#Decision points",
						"Added artifacts", "Modified artifacts", "Deleted artifacts", "LOC Central", "Total", "Low",
						"Average", "High", "Extreme", "Total", "Low", "Average", "High", "Extreme", "Total", "Low",
						"Average", "High", "Extreme", };
			} else {
				nameGroupCells = new String[] { "", "", "Business criteria", "", "", "", "", "", "", "", "", "AFP", "",
						"", "AEP", "", "", "", "", "", "AEFP", "", "", "", "", "", "", "", "", "", "", "", "AETP", "",
						"", "", "Misc AEP", "", "", "", "", "", "", "", "EFP", "", "", "", "", "", "", "", "", "", "",
						"", "", "", "", "Critical violations", "", "", "", "Non critical violations", "", "", "",
						"Technical size", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
						"Action plan Added", "", "", "", "", "Action plan Pending", "", "", "", "",
						"Action plan Solved", "", "", "", "", };
				nameCells = new String[] { "Application", "Version", "TQI", "Efficiency", "Robustness", "Security",
						"Changeability", "Transferability", "Coding Best Practices", "Application Operability",
						"Code Maintenability", "AFP", "AFP datafunctions FP", "AFP transactions FP", "AEP", "AEP Added",
						"AEP Deleted", "AEP Modified", "CV on new and modified code / AEP", "CV / AFP", "AEFP",
						"AEFP DF", "AEFP TF", "AEFP Added", "AEFP Deleted", "AEFP Modified", "AEFP Added DF",
						"AEFP Added TF", "AEFP Deleted DF", "AEFP Deleted TF", "AEFP Modified DF", "AEFP Modified TF",
						"AETP", "AETP Added", "AETP Deleted", "AETP Modified", "#transactions", "#evolved transactions",
						"#enhanced shared artifacts", "#enhanced specific artifacts", "Effort Complexity",
						"Equivalent ratio", "ImplementationPointsAEFP", "ImplementationPointsAETP", "EFP", "EFP DF",
						"EFP TF", "EFP Added", "EFP DF Added", "EFP TF Added", "EFP Modified", "EFP DF Modified",
						"EFP TF Modified", "EFP Deleted", "EFP DF Deleted", "EFP TF Deleted", "EFP Unchanged",
						"EFP DF Unchanged", "EFP TF Unchanged", "Total", "Added", "Removed", "InNewAndModified Code",
						"Total", "Added", "Removed", "In NewAndModified Code", "LOC", "Comment lines",
						"Commented-out lines", "#Artifacts", "#Files", "#Programs", "#Classes", "#Methods",
						"#Interfaces", "#Packages", "#Tables", "#Functions and Procedures", "#SQL artifacts",
						"#Includes", "#Functions pools", "#ABAP User exits", "#ABAP Transactions", "#Decision points",
						"Total", "Low", "Average", "High", "Extreme", "Total", "Low", "Average", "High", "Extreme",
						"Total", "Low", "Average", "High", "Extreme", };

			}
		}
		logger.info("Creating excel file " + getFileName());

		Row row = sheet.createRow((short) 0);
		Cell cell;
		int headerColNum = 0;
		int rowNum = 1;
		if (REPORTTYPE_METRICS_FULLREPORT.equals(reportType)) {
			for (String nc : nameGroupCells) {
				cell = row.createCell(headerColNum++);
				cell.setCellValue(nc);
			}
			row = sheet.createRow(rowNum++);
		}
		if (REPORTTYPE_METRICS_FULLREPORT.equals(reportType) || REPORTTYPE_METRICS_KPIREPORT.equals(reportType)) {
			headerColNum = 0;
			for (String nc : nameCells) {
				cell = row.createCell(headerColNum++);
				cell.setCellValue(nc);
			}
		}

		if (REPORTTYPE_METRICS_FULLREPORT.equals(reportType) || REPORTTYPE_METRICS_KPIREPORT.equals(reportType)) {
			// Data
			for (MetricReportOutput obj : listMetricsReportOutputs) {
				// Check if the application is eligible for FP (based on
				// harcoded list)
				boolean isFPApp = !isTelefonicaSpecificApplicationWithNoFP(obj.getApplicationName());

				int i = 0;
				row = sheet.createRow(rowNum++);
				cell = row.createCell(i++);
				cell.setCellValue(obj.getApplicationName());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getSnapshotVersion());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcTQI());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcEffi());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcRobus());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcSecu());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcChang());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcTrans());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcCodinBP());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcApplicationOp());
				cell = row.createCell(i++);
				cell.setCellValue(obj.getBcCodeMaintain());

				if (isMetricsFullReport()) {
					cell = row.createCell(i++);
					// if (obj.getNbAFPTotalFP()!=null)
					cell.setCellValue(getFPValue(obj.getNbAFPTotalFP(), isFPApp));
					/// else
					// cell.setCellValue("N/A");
					cell = row.createCell(i++);
					// if (obj.getNbAFPDatafunctionsFP()!=null)
					cell.setCellValue(getFPValue(obj.getNbAFPDatafunctionsFP(), isFPApp));
					// else
					// cell.setCellValue("N/A");
					cell = row.createCell(i++);
					//// if (obj.getNbAFPTransactionsFP()!=null)
					cell.setCellValue(getFPValue(obj.getNbAFPTransactionsFP(), isFPApp));
					// else
					// cell.setCellValue("N/A");
				}

				cell = row.createCell(i++);
				// if (obj.getNbAEPTotal()!=null)
				cell.setCellValue(getFPValue(obj.getNbAEPTotal(), isFPApp));
				// else
				// cell.setCellValue("N/A");
				cell = row.createCell(i++);
				// if (obj.getNbAEPAdded()!=null)
				cell.setCellValue(getFPValue(obj.getNbAEPAdded(), isFPApp));
				// else
				// cell.setCellValue("N/A");
				cell = row.createCell(i++);
				// if (obj.getNbAEPDeleted()!=null)
				cell.setCellValue(getFPValue(obj.getNbAEPDeleted(), isFPApp));
				// else
				// cell.setCellValue("N/A");
				cell = row.createCell(i++);
				// if (obj.getNbAEPModifed()!=null)
				cell.setCellValue(getFPValue(obj.getNbAEPModifed(), isFPApp));
				// else
				// cell.setCellValue("N/A");

				if (isMetricsFullReport()) {
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getAddedCVOnNewCodePerAEP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getCVPerAFP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue("" + getFPValue(obj.getNbAEFPDataFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPTransactionalFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPAdded(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPDeleted(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPModified(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPAddedDataFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPAddedTransactionalFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPDeletedDataFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPDeletedTransactionalFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPModifiedDataFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAEFPModifiedTransactionalFunctions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAETP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAETPAdded(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAETPDeleted(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbAETPModified(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbTransactions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEvovedTransactions(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEnhancementSharedArtifacts(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEnhancementSpecificArtifacts(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getEffortComplexity(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getEquivalentRatio(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbImplementationPointsAEFP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbImplementationPointsAETP(), isFPApp));
					// EFP
					// Total
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTotal(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPDataFP(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTransactionalFP(), isFPApp));
					cell = row.createCell(i++);
					// Added
					cell.setCellValue(getFPValue(obj.getNbEFPTotalAdded(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPDataFPAdded(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTransactionalFPAdded(), isFPApp));
					// Modified
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTotalModified(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPDataFPModified(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTransactionalFPModified(), isFPApp));
					// Deleted
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTotalDeleted(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPDataFPDeleted(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTransactionalFPDeleted(), isFPApp));
					// Unchanged
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTotalUnchanged(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPDataFPUnchanged(), isFPApp));
					cell = row.createCell(i++);
					cell.setCellValue(getFPValue(obj.getNbEFPTransactionalFPUnchanged(), isFPApp));
				}

				if (isMetricsFullReport()) {
					cell = row.createCell(i++);
					cell.setCellValue(obj.getTotalCritViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedCritViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getRemovedCritViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getCritViolationsInNewAndModifiedCode());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getTotalViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getRemovedViolations());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getViolationsInNewAndModifiedCode());

					cell = row.createCell(i++);
					cell.setCellValue(obj.getLoc());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbCommentLines());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbCommentedOutLines());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbArtifacts());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbFiles());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbPrograms());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbClasses());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbMethods());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbInterfaces());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbPackages());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbTables());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbFuncProc());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbSQLArtifacts());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbIncludes());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbFunctionPools());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbABAPUserExits());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbABAPTransactions());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getNbDecisionsPoints());

					// full including central schema metrics
					if (cssDbHostname != null) {
						cell = row.createCell(i++);
						cell.setCellValue(obj.getNbArtifactsAdded());
						cell = row.createCell(i++);
						cell.setCellValue(obj.getNbArtifactsModified());
						cell = row.createCell(i++);
						cell.setCellValue(obj.getNbArtifactsDeleted());
						cell = row.createCell(i++);
						cell.setCellValue(obj.getLocCentral());
					}
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedActionPlanItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedActionPlanLowComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedActionPlanAvgComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedActionPlanHigComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getAddedActionPlanExtComItems());

					cell = row.createCell(i++);
					cell.setCellValue(obj.getPendingActionPlanItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getPendingActionPlanLowComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getPendingActionPlanAvgComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getPendingActionPlanHigComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getPendingActionPlanExtComItems());

					cell = row.createCell(i++);
					cell.setCellValue(obj.getSolvedActionPlanItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getSolvedActionPlanLowComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getSolvedActionPlanAvgComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getSolvedActionPlanHigComItems());
					cell = row.createCell(i++);
					cell.setCellValue(obj.getSolvedActionPlanExtComItems());
				}

			}
		}
		try {
			FileOutputStream outputStream = new FileOutputStream(getFileName());
			workbook.write(outputStream);
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Execute a Rest API request with default parameters
	 * 
	 * @param URI
	 * @return
	 * @throws Exception
	 */

	private String executeRestAPIRequest(String URI) throws Exception {
		return executeRestAPIRequest(URI, getUser(), getPassword(), getEncodingToUse(), logger);
	}

	/***
	 * Execute a Rest API request
	 * 
	 * @param URI
	 * @param user
	 * @param pwd
	 * @param encodingToUse
	 * @param logger
	 * @return
	 * @throws Exception
	 */
	private String executeRestAPIRequest(String URI, String user, String pwd, String encodingToUse, Logger logger)
			throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String responseBody = null;
		try {
			HttpGet httpget = new HttpGet(URI);
			// JSON
			httpget.addHeader("accept", "application/json");
			// authentication parameters
			String encoding = DatatypeConverter.printBase64Binary((user + ":" + pwd).getBytes(encodingToUse));
			httpget.setHeader("Authorization", "Basic " + encoding);

			logger.info("Executing request " + httpget.getRequestLine() + " -- [Begin] ");
			response = httpclient.execute(httpget);
			logger.debug("	HTTP code=" + response.getStatusLine());
			responseBody = EntityUtils.toString(response.getEntity());
			logger.debug(responseBody);

			// Parsing the JSON structure
			EntityUtils.consume(response.getEntity());
			logger.info("Executing request " + httpget.getRequestLine() + " -- [End] ");
		} catch (Exception l_exception) {
			logger.error(l_exception.getMessage(), l_exception);
			throw l_exception;
		} finally {
			if (response != null)
				response.close();
			if (response != null)
				response.close();
		}
		return responseBody;
	}

	boolean isMetricsKPIReport() {
		return REPORTTYPE_METRICS_KPIREPORT.equals(reportType);
	}

	boolean isMetricsFullReport() {
		return REPORTTYPE_METRICS_FULLREPORT.equals(reportType);
	}

	boolean isMetricsReport() {
		return REPORTTYPE_METRICS_FULLREPORT.equals(reportType) || REPORTTYPE_METRICS_KPIREPORT.equals(reportType);
	}

	boolean isQRReport() {
		return REPORTTYPE_QR_SIMPLEREPORT.equals(reportType) || REPORTTYPE_QR_FULLREPORT.equals(reportType);
	}

	boolean isQRSimpleReport() {
		return REPORTTYPE_QR_SIMPLEREPORT.equals(reportType);
	}

	boolean isQRFullReport() {
		return REPORTTYPE_QR_FULLREPORT.equals(reportType);
	}

	public static void printUsage(final String applicationName, final Options options, final OutputStream out) {
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter usageFormatter = new HelpFormatter();
		usageFormatter.printUsage(writer, 80, applicationName, options);
		writer.close();
	}

	/**
	 * Write "help" to the provided OutputStream.
	 */
	public static void printHelp(final Options options, final int printedRowWidth, final String header,
			final String footer, final int spacesBeforeOption, final int spacesBeforeOptionDescription,
			final boolean displayUsage, final OutputStream out) {
		final String commandLineSyntax = "To be completed";
		final PrintWriter writer = new PrintWriter(out);
		final HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(writer, printedRowWidth, commandLineSyntax, header, options, spacesBeforeOption,
				spacesBeforeOptionDescription, footer, displayUsage);
		writer.close();
	}

	/////////////////////////////////////////////

	private Options constructCmdLineOptions() {
		Options options = new Options();
		// add input directory option
		options.addOption(OPTION_SK_URL, OPTION_LK_URL, true, "url");
		options.addOption(OPTION_SK_HDOMAIN, OPTION_LK_HDOMAIN, true, "health domain (default=AAD)");
		options.addOption(OPTION_SK_EDOMAINS, OPTION_LK_EDOMAINS, true, "engineering domains");
		options.addOption(OPTION_SK_USR, OPTION_LK_USR, true, "Rest API user");
		options.addOption(OPTION_SK_PWD, OPTION_LK_PWD, true, "Rest API password");
		options.addOption(OPTION_SK_ENV, OPTION_LK_ENV, true, "environment (PROD / DEV)");
		options.addOption(OPTION_SK_RTYPE, OPTION_LK_RTYPE, true,
				"report type (Metrics_KPIReport / Metrics_FullReport / Env_DeltaReport / QR_SimpleReport / QR_FullReport");
		options.addOption(OPTION_SK_APPFILTER, OPTION_LK_APPFILTER, true,
				"Application to be processed names (App1,App2)");
		options.addOption(OPTION_SK_VERSIONFILTER, OPTION_LK_VERSIONFILTER, true,
				"Version filter (VERSIONS_LASTONE, VERSIONS_LASTTWO, VERSIONS_ALL)");
		options.addOption(OPTION_SK_DB_RUNSQL, OPTION_LK_DB_RUNSQL, false, "Run SQL Queries");
		options.addOption(OPTION_SK_DB_HOST, OPTION_LK_DB_HOST, true, "DB host");
		options.addOption(OPTION_SK_DB_PORT, OPTION_LK_DB_PORT, true, "DB port");
		options.addOption(OPTION_SK_DB_DBNAME, OPTION_LK_DB_DBNAME, true, "DB name");
		options.addOption(OPTION_SK_DB_SCHEMAS, OPTION_LK_DB_SCHEMAS, true, "DB schemas list");
		options.addOption(OPTION_SK_DB_USER, OPTION_LK_DB_USER, true, "DB user");
		options.addOption(OPTION_SK_DB_PWD, OPTION_LK_DB_PWD, true, "DB password");

		return options;
	}

	/**
	 * Load the command line parameters
	 * 
	 * @throws ParseException
	 */
	protected void parseCmdLineParameters(String[] p_parameters) {
		logger.info("parsing command line ...");

		try {
			Options options = constructCmdLineOptions();
			CommandLineParser parser = new GnuParser();
			org.apache.commons.cli.CommandLine cmd = parser.parse(options, p_parameters);
			if (logger.isDebugEnabled()) {
				StringBuffer sb = new StringBuffer("  command line=");
				for (String arg : p_parameters) {
					sb.append(arg);
					sb.append(" ");
				}
				logger.debug(sb.toString());
			}
			// set only if value not null, else default value
			if (cmd.getOptionValue(OPTION_LK_RTYPE) != null)
				this.reportType = cmd.getOptionValue(OPTION_LK_RTYPE);
			this.rootURL = cmd.getOptionValue(OPTION_LK_URL);
			this.AADDomain = cmd.getOptionValue(OPTION_LK_HDOMAIN);
			this.AEDDomains = cmd.getOptionValue(OPTION_LK_EDOMAINS);
			this.user = cmd.getOptionValue(OPTION_LK_USR);
			this.password = cmd.getOptionValue(OPTION_LK_PWD);
			this.environment = cmd.getOptionValue(OPTION_LK_ENV);
			if (cmd.hasOption(OPTION_LK_APPFILTER)) {
				this.filterApplicationNames = cmd.getOptionValue(OPTION_LK_APPFILTER);
			}
			this.bRetrieveDBCentralSchemaMetrics = cmd.hasOption(OPTION_LK_DB_RUNSQL);
			this.cssDbHostname = cmd.getOptionValue(OPTION_LK_DB_HOST);
			this.cssDbPort = cmd.getOptionValue(OPTION_LK_DB_PORT);
			this.cssDbDatabase = cmd.getOptionValue(OPTION_LK_DB_DBNAME);
			this.cssDbListCentralSchemas = cmd.getOptionValue(OPTION_LK_DB_SCHEMAS);
			this.cssDbUser = cmd.getOptionValue(OPTION_LK_DB_USER);
			this.cssDbPassword = cmd.getOptionValue(OPTION_LK_DB_PWD);
			if (cmd.hasOption(OPTION_LK_VERSIONFILTER)) {
				this.filterVersions = cmd.getOptionValue(OPTION_LK_VERSIONFILTER);
			}

		} catch (org.apache.commons.cli.ParseException e) {
			logger.error("Error parsing command line : " + e.getMessage());
			printHelp(constructCmdLineOptions(), 80, "", "", 5, 3, true, System.out);
			System.exit(-1);
		}
		logger.info("parsing command line [OK]");
	}

	/**
	 * Check that the parameters are correct
	 */
	protected void checkParameters() {
		boolean abort = false;

		logger.info("checking parameters ...");
		/*
		 * User parameters (command line)
		 */
		// Mandatory parameters
		if (reportType == null || rootURL == null || user == null || password == null || environment == null) {
			if (reportType == null) {
				logger.fatal("Aborting ! reportType parameter is not set");
			}
			if (rootURL == null) {
				logger.fatal("Aborting ! rootURL parameter is not set");
			}
			if (user == null) {
				logger.fatal("Aborting ! user parameter is not set");
			}
			if (password == null) {
				logger.fatal("Aborting ! password parameter is not set");
			}
			if (environment == null) {
				logger.fatal("Aborting ! environment parameter is not set");
			}
			System.exit(-1);
		}

		// Check that the report type is in the accepted list
		if (!reportType.equals(REPORTTYPE_METRICS_KPIREPORT) && !reportType.equals(REPORTTYPE_METRICS_FULLREPORT)
				&& !reportType.equals(REPORTTYPE_ENV_DELTAREPORT) && !reportType.equals(REPORTTYPE_QR_SIMPLEREPORT)
				&& !reportType.equals(REPORTTYPE_QR_FULLREPORT)) {
			logger.fatal(
					"Aborting ! reportType parameter do not have a correct value : Metrics_KPIReport / Metrics_FullReport / Env_DeltaReport / QR_QRSimpleReport / QR_QRReportWithBC");
			System.exit(-1);
		}

		//
		if (!FILTER_VERSIONS_ALL.equals(filterVersions) && !FILTER_VERSIONS_LASTONE.equals(filterVersions)
				&& !FILTER_VERSIONS_LASTTWO.equals(filterVersions)) {
			logger.fatal(
					"Aborting ! Version filter paramater value do not have a correct value : VERSIONS_LASTONE / VERSIONS_LASTTWO / VERSION_ALL");
			abort = true;
		}

		// Metrics report are done on engineering domains
		if (isMetricsReport()) {
			if (AEDDomains == null || "".equals("AEDDomains")) {
				logger.fatal("Aborting ! engineering domains parameter is not set/empty");
				abort = true;
			}
		}
		// QR report are done on health domain
		if (isQRReport()) {
			if (AADDomain == null || "".equals("AADDomain")) {
				logger.fatal("Aborting ! health domains parameter is not set/empty");
				abort = true;
			}
		}
		// If DB parameters are set, all of them must be set
		if (cssDbHostname != null) {
			if (cssDbPort == null || "".equals("cssDbPort") || cssDbDatabase == null || "".equals("cssDbDatabase")
					|| cssDbListCentralSchemas == null || "".equals("cssDbListCentralSchemas") || cssDbUser == null
					|| "".equals("cssDbUser") || cssDbPassword == null || "".equals("cssDbPassword")) {
				logger.fatal(
						"Aborting ! One of the parameters is not set/empty : DB port, DB name, DB schemas list, DB user or DB password");
				abort = true;
			}
		}

		if (abort) {
			System.exit(-1);
		}
	}

}
