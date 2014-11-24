import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
 
public class QueryExecutorAll {
	private static Properties props = initializeProp();
	public static Properties initializeProp(){
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
            fis = new FileInputStream("../MavenProject/src/main/java/db.properties");
            props.load(fis);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
		return props;
	}
	
	public static void set_cassandra_keyspace(String keyspaceName){
		BasicDataSource ds = (BasicDataSource)getDataSource("cassandra");
		executeQuery(ds, "create keyspace IF NOT EXISTS "+ keyspaceName+" with replication = {'class':'SimpleStrategy','replication_factor':1}");
		String url_base = props.getProperty("CASSANDRA_DB_BASE_URL");
		props.setProperty("CASSANDRA_DB_URL", url_base+ "/" + keyspaceName);
	}
	
	public static void plot_result(HashMap<String, Long> results){
		
	}
	
	public static HashMap<String, Long> run_all(String rawSQLQuery){
		HashMap<String, Long> result = new HashMap<String, Long>();
		result.put("mysql", executeQuery(getDataSource("mysql"), rawSQLQuery));
		result.put("mongo", executeQuery(getDataSource("mongo"), rawSQLQuery));
		result.put("cassandra", executeQuery(getDataSource("cassandra"), rawSQLQuery));
		return result;
	}
	
	public static void create_table_cassandra(String creation_q){
		System.out.println("cassandra");
		String[] stmts = creation_q.split(";");
		for(String s : stmts){
			executeQuery(getDataSource("cassandra"), s);
		}
	}
	public static void create_table_mongo(String creation_q){
		System.out.println("mongo");
		executeQuery(getDataSource("mongo"), creation_q);
	}
	public static void create_table_mysql(String creation_q){
		System.out.println("mysql");
		createProcedure(getDataSource("mysql"), "mysql_create", creation_q);
		executeProcedure(getDataSource("mysql"), "mysql_create()");
		deleteProcedure(getDataSource("mysql"), "mysql_create");
	}
	
    public static DataSource getDataSource(String dbType){
        BasicDataSource ds = new BasicDataSource();
        if("mysql".equals(dbType)){
            ds.setDriverClassName(props.getProperty("MYSQL_DB_DRIVER_CLASS"));
            ds.setUrl(props.getProperty("MYSQL_DB_URL"));
            ds.setUsername(props.getProperty("MYSQL_DB_USERNAME"));
            ds.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        }else if("mongo".equals(dbType)){
            ds.setDriverClassName(props.getProperty("MONGODB_DB_DRIVER_CLASS"));
            ds.setUrl(props.getProperty("MONGODB_DB_URL"));
            ds.setUsername(props.getProperty("MONGODB_DB_USERNAME"));
            ds.setPassword(props.getProperty("MONGODB_DB_PASSWORD"));
        }else if("cassandra".equals(dbType)){
            ds.setDriverClassName(props.getProperty("CASSANDRA_DB_DRIVER_CLASS"));
            ds.setUrl(props.getProperty("CASSANDRA_DB_URL"));
            ds.setUsername(props.getProperty("CASSANDRA_DB_USERNAME"));
            ds.setPassword(props.getProperty("CASSANDRA_DB_PASSWORD"));
        }else{
            return null;
        }
         
        return ds;
    }
    
    public static void createProcedure(DataSource ds, String procedureName, String rawProcedureSQL){
    	Connection conn = null;
	    PreparedStatement stmt = null;
    	try {
    		conn = ds.getConnection();
    		stmt = conn.prepareStatement(rawProcedureSQL);   
        	stmt.execute(); 
        }catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
        }finally {
    	    if (stmt != null) {
    	        try {
    	            stmt.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        stmt = null;
    	    }
    	    
    	    if (conn != null) {
    	        try {
    	            conn.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        conn = null;
    	    }
    	}
    }
    public static void deleteProcedure(DataSource ds, String procedureName){
    	executeQuery(ds, "drop procedure "+procedureName+";");
    }
    
    public static long executeProcedure(DataSource ds, String procedureName) {
    	long fStart = 0;
    	long fEnd = 0;
    	Connection conn = null;
	    ResultSet rs = null;
	    CallableStatement stmt = null;
    	try {
        	conn = ds.getConnection();
        	stmt = conn.prepareCall("{ call " + procedureName + "}");
    	    fStart = System.currentTimeMillis();
    	    rs = stmt.executeQuery();
    	    fEnd = System.currentTimeMillis();
			return (fEnd - fStart);
    	    
    	}
    	catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	    return (fEnd - fStart);
    	}
    	finally {
    	    if (rs != null) {
    	        try {
    	            rs.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        rs = null;
    	    }

    	    if (stmt != null) {
    	        try {
    	            stmt.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        stmt = null;
    	    }
    	    
    	    if (conn != null) {
    	        try {
    	            conn.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        conn = null;
    	    }
    	}
    }
    public static long executeQuery(DataSource ds, String rawSQLQuery) {
    	long fStart = 0;
    	long fEnd = 0;
    	Connection conn = null;
	    ResultSet rs = null;
	    Statement stmt = null;
    	try {
        	conn = ds.getConnection();
    	    fStart = System.currentTimeMillis();
    	    stmt = conn.createStatement();
    	    if (stmt.execute(rawSQLQuery)) {
    	        rs = stmt.getResultSet();
    	    }
    	    fEnd = System.currentTimeMillis();
//    	    System.out.println("\n\nTHE RESULTS:");	
//			int i=0;
//			ResultSetMetaData meta = null;
//            
//			// Print out a row of column headers
//			if(rs != null){
//				meta = rs.getMetaData();
//				System.out.println("Total columns: " + meta.getColumnCount());
//				System.out.print(meta.getColumnName(1));
//				for (int j = 2; j <= meta.getColumnCount(); j++)
//					System.out.print(", " + meta.getColumnName(j));
//				System.out.println();
//				// Print out all rows in the ResultSet
//				while (rs.next()) 
//				{
//					System.out.print(rs.getObject(1));
//					for (int j = 2; j <= meta.getColumnCount(); j++)
//						System.out.print(", " + rs.getObject(j));
//					System.out.println();
//					i++;
//				}	
//			}
//			
			return (fEnd - fStart);
    	    
    	}
    	catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	    return (fEnd - fStart);
    	}
    	finally {
    	    if (rs != null) {
    	        try {
    	            rs.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        rs = null;
    	    }

    	    if (stmt != null) {
    	        try {
    	            stmt.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        stmt = null;
    	    }
    	    
    	    if (conn != null) {
    	        try {
    	            conn.close();
    	        } catch (SQLException sqlEx) { } // ignore

    	        conn = null;
    	    }
    	}
    }
    
}