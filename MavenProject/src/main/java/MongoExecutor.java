import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Created by trannguyen on 11/14/14.
 */
public class MongoExecutor implements QueryExecutor {
	private Connection conn;
	private String driverLink = "jdbc:mongo://localhost/test";
	
	public MongoExecutor() throws Exception{
		if(!connect()){
			throw new Exception("Could not initiate the the Mongo Executor");
		}
	}
	
	public MongoExecutor(String driverLink) throws Exception{
		this.driverLink = driverLink;
		if(!connect()){
			throw new Exception("Could not initiate the the Mongo Executor");
		}
	}
	
    /**
     * Each implementation of this will take in
     * the raw sql query and execute in its underlying database
     * returning how long it takes to execute that statement
     *
     * @param rawSQLQuery SQL statement
     * @return time it takes to excute those in mili seconds
     */
    @Override
    public long executeQuery(String rawSQLQuery) {
    	long fStart = 0;
    	long fEnd = 0;
    	
	    ResultSet rs = null;
	    Statement stmt = null;
    	try {
    	    fStart = System.currentTimeMillis();
    	    stmt = conn.createStatement();
    	    if (stmt.execute(rawSQLQuery)) {
    	        rs = stmt.getResultSet();
    	        
    	    }
    	    fEnd = System.currentTimeMillis();
    	    System.out.println(fEnd - fStart);
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
    	}
    }

    @Override
    public boolean connect(){
    	try {
    	   Class.forName("mongodb.jdbc.MongoDriver");
 		   this.conn =DriverManager.getConnection(driverLink);
 		   System.out.println("Connected!");
 		   return true;
 		} catch (SQLException ex){
    	    System.out.println("SQLException: " + ex.getMessage());
    	    System.out.println("SQLState: " + ex.getSQLState());
    	    System.out.println("VendorError: " + ex.getErrorCode());
    	    return false;
    	} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
    }
	@Override
	public void cleanUP() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	
	public static void main( String args[] ){
		MongoExecutor me;
		try {
			me = new MongoExecutor();
			me.executeQuery("SELECT * from test");
			me.cleanUP();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
}
