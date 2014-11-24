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
	private String driverLink = "jdbc:mysql://localhost/test";
	
	public MySqlExecutor(String driverLink) throws Exception{
		this.driverLink = driverLink;
		if(!connect()){
			throw new Exception("Could not initiate the the Mysql Executor");
		}
		
	}
	public MySqlExecutor() throws Exception{
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

	public boolean connect() {
		try {
		   Class.forName("com.mysql.jdbc.Driver");
		   this.conn =DriverManager.getConnection(driverLink);
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
	public void cleanUP() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

