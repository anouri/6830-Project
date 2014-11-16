import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
 
public class QueryExecutorAll {
	
	public static HashMap<String, Long> run_all(String rawSQLQuery){
		HashMap<String, Long> result = new HashMap<String, Long>();
//		result.put("mysql", executeQuery(getDataSource("mysql"), rawSQLQuery));
//		result.put("mongo", executeQuery(getDataSource("mongo"), rawSQLQuery));
		result.put("cassandra", executeQuery(getDataSource("cassandra"), rawSQLQuery));
		return result;
	}
    public static DataSource getDataSource(String dbType){
        Properties props = new Properties();
        FileInputStream fis = null;
        BasicDataSource ds = new BasicDataSource();
         
        try {
            fis = new FileInputStream("src/main/java/db.properties");
            props.load(fis);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
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