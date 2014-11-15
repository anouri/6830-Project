import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
/**
 * Created by trannguyen on 11/14/14.
 */
public class MySqlExecutor implements QueryExecutor {
	private Connection conn;
	private String driverLink;
	
	public MySqlExecutor(String driverLink) throws Exception{
		this.driverLink = driverLink;
		if(!connect()){
			throw new Exception("Could not initiate the the Mysql Executor");
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
	public boolean connect() {
		try {
		   Class.forName("com.mysql.jdbc.Driver");
		   conn =DriverManager.getConnection(driverLink);
		   return true;
		} catch (Exception ex) {
		    System.out.println("Exception: " + ex.getMessage());
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
}

