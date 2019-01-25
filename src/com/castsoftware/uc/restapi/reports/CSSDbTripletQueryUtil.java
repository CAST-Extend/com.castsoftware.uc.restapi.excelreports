package com.castsoftware.uc.restapi.reports;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CSSDbTripletQueryUtil {

	private final static String CSSDriver = "org.postgresql.Driver";
	
	
	private final static String SQL_LIST_SNAPSHOTS = "select * from %1.dss_snapshots sn, %1.dss_objects appo where sn.application_id = appo.object_id and appo.object_type_id = -102 and SNAPSHOT_STATUS = 2 order by appo.object_name, sn.snapshot_id";
	//application_id
	/*
	 * %1 central schema name
	 * %2 first snapshot id
	 * %3 second snapshot id
	 * */
	private final static String SQL_TOTAL_ARTIFACTS_ADDED = "select count(1) as AddedArtifacts"+
				" from (" +
					"select  distinct     cs1.CHANGE_TYPE, cs1.CPLX_TYPE, cs1.OBJECT_ID"+        
					" from"             +
					" %1.ADGV_COST_STATUSES cs1,"+             
					" %1.DSS_LINKS l,"            + 
					" %1.DSS_MODULE_LINKS m"       +  
					" where"+             
					" m.OBJECT_ID = 3"+             
					" and m.SNAPSHOT_ID in (%2,%3)"+             
					" and l.PREVIOUS_OBJECT_ID = m.MODULE_ID"+             
					" and l.LINK_TYPE_ID       = 3"+             
					" and cs1.OBJECT_ID        = l.NEXT_OBJECT_ID"+             
					" and cs1.SNAPSHOT_ID in (%3)           "+
					") A"+
					" where A.CHANGE_TYPE = 1 /*Added*/"; 
	
		private final static String SQL_TOTAL_ARTIFACTS_MODIFIED = "select count(1) as ModifiedArtifacts"+
				" from (" +
					"select  distinct     cs1.CHANGE_TYPE, cs1.CPLX_TYPE, cs1.OBJECT_ID"+        
					" from"             +
					" %1.ADGV_COST_STATUSES cs1,"+             
					" %1.DSS_LINKS l,"            + 
					" %1.DSS_MODULE_LINKS m"       +  
					" where"+             
					" m.OBJECT_ID = 3"+             
					" and m.SNAPSHOT_ID in (%2,%3)"+             
					" and l.PREVIOUS_OBJECT_ID = m.MODULE_ID"+             
					" and l.LINK_TYPE_ID       = 3"+             
					" and cs1.OBJECT_ID        = l.NEXT_OBJECT_ID"+             
					" and cs1.SNAPSHOT_ID in (%3)           "+
					") A"+
					" where A.CHANGE_TYPE = 3 /*Modified*/"; 	

		private final static String SQL_TOTAL_ARTIFACTS_DELETED = "select count(1) as DeletedArtifacts"+
				" from (" +
					"select  distinct     cs1.CHANGE_TYPE, cs1.CPLX_TYPE, cs1.OBJECT_ID"+        
					" from"             +
					" %1.ADGV_COST_STATUSES cs1,"+             
					" %1.DSS_LINKS l,"            + 
					" %1.DSS_MODULE_LINKS m"       +  
					" where"+             
					" m.OBJECT_ID = 3"+             
					" and m.SNAPSHOT_ID in (%2,%3)"+             
					" and l.PREVIOUS_OBJECT_ID = m.MODULE_ID"+             
					" and l.LINK_TYPE_ID       = 3"+             
					" and cs1.OBJECT_ID        = l.NEXT_OBJECT_ID"+             
					" and cs1.SNAPSHOT_ID in (%3)           "+
					") A"+
					" where A.CHANGE_TYPE = 2 /*Deleted*/"; 	

	private static final String SQL_LOC = "SELECT"
		       + " m.METRIC_NUM_VALUE AS LOC"
		       + " FROM   %1.DSS_METRIC_RESULTS m"
		       		+ " JOIN %1.DSS_OBJECTS DSSO"
		       		+ " ON     DSSO.OBJECT_ID=m.OBJECT_ID"
		       		+ " JOIN %1.DSS_SNAPSHOTS DSSS"
		       		+ " ON     DSSS.SNAPSHOT_ID=M.SNAPSHOT_ID"
		       + " WHERE  M.SNAPSHOT_ID          = %2"
		       + " AND    M.OBJECT_ID            = %3"
		       + " AND    m.METRIC_ID            = 10151";
		
	
	public static void main(String[] args) {
		runAddedDeletedModifiedArtifactQueries("localhost","2280", "postgres", "operator", "CastAIP", "training_82_central", Logger.getLogger("MainLogger"));

	}

	/**
	 * Get CSS connection string
	 * @return
	 */
	private static String getCSSConnectionString(String dbHost) {
		return getCSSConnectionString(dbHost, "2280", "postgres");
	}
	
	/**
	 * Get CSS connection string
	 * @return
	 */
	private static String getCSSConnectionString(String dbHost, String dbPort, String dbDatabase) {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:postgresql://");
		sb.append(dbHost);
		sb.append(":");
		sb.append(dbPort);
		sb.append("/");
		sb.append(dbDatabase);

		return sb.toString();
	}	
	
	public static List<CentralSnapshotMetrics> runAddedDeletedModifiedArtifactQueries(String dbHost, String dbPort, String dbDatabase, String schema, Logger logger) {
		return runAddedDeletedModifiedArtifactQueries(dbHost, dbPort, dbDatabase, "operator" , "CastAIP", schema, logger);
	}
	
	public static List<CentralSnapshotMetrics> runAddedDeletedModifiedArtifactQueries(String dbHost, String dbPort, String dbDatabase, String dbUser, String dbPassword, String schema, Logger logger) {
		List<CentralSnapshotMetrics> listCentralSnapshotMetrics = new ArrayList<CentralSnapshotMetrics>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String sqlQuery = "";
		List<String> listSnapshots = new ArrayList<String> ();

		try {
				Class.forName(CSSDriver);
				con = DriverManager.getConnection(getCSSConnectionString(dbHost, dbPort, dbDatabase), dbUser, dbPassword);

				// Set autocommit to true
				con.setAutoCommit(true);
				st = con.createStatement();
				
				// Collect all snapshots for the central schema
				//sqlQuery = String.format(SQL_LIST_SNAPSHOTS, schema);
				sqlQuery = SQL_LIST_SNAPSHOTS.replaceAll("%1", schema);
				
				logger.debug("  running SQL query snapshots : " + sqlQuery);
				rs = st.executeQuery(sqlQuery);								
	
				while (rs.next()) {
					String appName = rs.getString("object_name");
					Integer snapshotId = rs.getInt("snapshot_id");
					Integer application_id = rs.getInt("application_id");
					listSnapshots.add(appName + "/" + application_id + "/" + snapshotId);
				}
				rs.close();
				
				int i = 0;
				String previous_app = null;
				int previous_id = -1;
				for (String appSnapshot : listSnapshots) {
					String appName = appSnapshot.split("/")[0];
					Integer app_id = new Integer(appSnapshot.split("/")[1]);					
					Integer snap_id = new Integer(appSnapshot.split("/")[2]);					
					logger.debug(appSnapshot);

					if (previous_app != null) {
						if (!previous_app.equals(appName))
							i = 0;
						if (i != 0) {
							CentralSnapshotMetrics snapmet = new CentralSnapshotMetrics();
							snapmet.setApplicationName(appName);
							snapmet.setApplicationId(app_id);							
							snapmet.setSnapshotId(snap_id);
							
							// queryAdded
							sqlQuery = SQL_TOTAL_ARTIFACTS_ADDED;
							sqlQuery = sqlQuery.replaceAll("%1", schema).replaceAll("%2", ""+previous_id).replaceAll("%3", ""+snap_id);
							logger.debug("  running SQL query added artifacts : " + sqlQuery);
							rs = st.executeQuery(sqlQuery);								
							while (rs.next()) {
								logger.debug("" + rs.getInt("AddedArtifacts"));
								snapmet.setAddedArtifacts(rs.getInt("AddedArtifacts"));
							}
							rs.close();
							// queryModified
							sqlQuery = SQL_TOTAL_ARTIFACTS_MODIFIED;
							sqlQuery = sqlQuery.replaceAll("%1", schema).replaceAll("%2", ""+previous_id).replaceAll("%3", ""+snap_id);
							logger.debug("  running SQL query modified artifacts : " + sqlQuery);
							rs = st.executeQuery(sqlQuery);								
							while (rs.next()) {
								logger.debug("" + rs.getInt("ModifiedArtifacts"));
								snapmet.setModifiedArtifacts(rs.getInt("ModifiedArtifacts"));
							}
							rs.close();						
							// queryDeleted
							sqlQuery = SQL_TOTAL_ARTIFACTS_DELETED;
							sqlQuery = sqlQuery.replaceAll("%1", schema).replaceAll("%2", ""+previous_id).replaceAll("%3", ""+snap_id);
							logger.debug("  running SQL query deleted artifacts : " + sqlQuery);
							rs = st.executeQuery(sqlQuery);								
							while (rs.next()) {
								logger.debug("" + rs.getInt("DeletedArtifacts"));
								snapmet.setDeletedArtifacts(rs.getInt("DeletedArtifacts"));
							}
							rs.close();
							
							// queryLOC
							sqlQuery = SQL_LOC;
							sqlQuery = sqlQuery.replaceAll("%1", schema).replaceAll("%2", ""+snap_id).replaceAll("%3", ""+app_id);
							logger.debug("  running SQL query LOC : " + sqlQuery);
							rs = st.executeQuery(sqlQuery);								
							while (rs.next()) {
								logger.debug("" + rs.getInt("LOC"));
								snapmet.setLoc(rs.getInt("LOC"));
							}
							rs.close();
							listCentralSnapshotMetrics.add(snapmet);
						}
					}
					previous_id = snap_id;
					previous_app = appName;
					i++;
				}
	
				
		} catch (ClassNotFoundException e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				log.warn("Erreur rollbak : " +  e.getMessage());
//			}
			logger.error("Erreur loading SQL driver : " + e.getMessage());
			return null;
		} catch (SQLException e) {
//			try {
//				if (con != null) { 
//					con.rollback();
//				}
//			} catch (SQLException e1) {
//				log.warn("Erreur rollback : " +  e.getMessage());
//			}
			logger.error("Erreur running SQL query : " + e.getMessage());
			return null;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.info("Erreur closing SQL resultset : " +  e.getMessage());
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					logger.info("Erreur closing SQL statement : " +  e.getMessage());
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					logger.info("Erreur closing SQL connection : " +  e.getMessage());
				}
			}
		}
		return listCentralSnapshotMetrics;
	}
	
	
	
}
