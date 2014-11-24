import java.sql.*;

/**
 * Demonstrates how to write queries using the UnityJDBC driver. 
 * 
 * <p>General query notes:
 * <ul>
 * <li>UnityJDBC supports SELECT SQL syntax except for subqueries across multiple databases.  Subqueries are only supported in the FROM clause.</li>
 * <li>Fields and tables should normally be prefixed with the database name where they come from. e.g. MyDB1.MyTable.MyField or MyDB2.MyTable2</li>
 * <li>It is possible to alias using AS in the FROM and SELECT clauses as usual (recommended as database.table names are normally long).</li>
 * <li>When querying, imagine that all tables and fields are LOCAL IN A SINGLE DATABASE.  UnityJDBC will handle joins across systems and perform the necessary 
 * optimizations to make your query efficient.</li>
 * </ul>
 */
@SuppressWarnings({"nls"})
public class UnityQueryExecutor 
{
	// URL for sources.xml file specifying what databases to integrate.  This file must be locally accessible or available over the Internet.
	private static String url="jdbc:unity://src/main/java/xspec/UnityConfig.xml";
	

	/**
	 * Main method
	 * 
	 * @param args
	 * 			no args required
	 */
	public static void main(String [] args)
	{
		Connection con = null;
		Statement stmt = null;
		ResultSet rst;
		
		try {
			// Create new instance of UnityDriver and make connection
			System.out.println("\nRegistering driver.");
			Class.forName("unity.jdbc.UnityDriver");
			
			System.out.println("\nGetting connection:  "+url);
			con = DriverManager.getConnection(url);
			System.out.println("\nConnection successful for "+ url);

			System.out.println("\nCreating statement.");
			stmt = con.createStatement();
			// Unity supports scrollable resultsets, but performance is much better with FORWARD_ONLY
			// stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			// A query is exactly like SQL.  
			// Attributes should be FULLY qualified: databaseName.tableName.fieldName (although non-fully qualified attribute references are allowed if they are unique)
			// Statement must end with a semi-colon ;			
			// This is a very complex example query that performs two joins on the client-side (across databases).
			String sql = "SELECT * from table_name;";
			
			// Note: Client's local JVM is used to process some operations.  
			// For large queries, this may require setting a large heap space.
			// JVM command line parameters: -Xms500m -Xmx500m
			// These parameters set heap space to 500 MB.  Do not set higher than 80% of physical machine memory size.			
			System.out.println("\nExecuting query: \n"+sql);
			rst = stmt.executeQuery(sql);
						
			System.out.println("\n\nTHE RESULTS:");	
			int i=0;
			long timeStart = System.currentTimeMillis();
			long timeEnd;
			ResultSetMetaData meta = rst.getMetaData();
            
			// Print out a row of column headers
			System.out.println("Total columns: " + meta.getColumnCount());
			System.out.print(meta.getColumnName(1));
			for (int j = 2; j <= meta.getColumnCount(); j++)
				System.out.print(", " + meta.getColumnName(j));
			System.out.println();

			// Print out all rows in the ResultSet
			while (rst.next()) 
			{
				System.out.print(rst.getObject(1));
				for (int j = 2; j <= meta.getColumnCount(); j++)
					System.out.print(", " + rst.getObject(j));
				System.out.println();
				i++;
			}			
				
			timeEnd = System.currentTimeMillis();
			System.out.println("Query took: "+((timeEnd-timeStart))+" milliseconds");
			System.out.println("Number of results printed: "+i);
			stmt.close();  
			rst.close();
			System.out.println("\nOPERATION COMPLETED SUCCESSFULLY!"); 
		}
		catch (Exception ex)
		{	System.out.println("Exception: " + ex);
		}
		finally
		{			
			if (con != null)
			{	try
				{	// Close the connection
					con.close();				
				}
				catch (SQLException ex)
				{	System.out.println("SQLException: " + ex);
				}
			}
		}
	}
}
