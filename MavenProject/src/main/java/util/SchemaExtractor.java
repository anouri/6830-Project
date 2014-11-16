package util;

import java.sql.*;
import java.io.*;

import com.unityjdbc.sourcebuilder.AnnotatedExtractor;

import unity.io.FileManager;
import unity.util.EncryptDecrypt;

/**
 Builds an XML schema document describing a database schema.
 User provides URL of database system and information is extracted using JDBC.

 Notes:
 - A schema file must be produced for each database to be queried by UnityJDBC.

 - The <semanticTableName> and <semanticFieldName> tags for each table and field can be used to assign your own
 names to tables and fields in the sources.  Those names can then be used in queries.

 To compile (if in the code directory): 
 javac -cp ..\\unityjdbc.jar com\\unityjdbc\\sourcebuilder\\SchemaExtractor.java
 
 To run:
 java -cp ..\\unityjdbc.jar com.unityjdbc.sourcebuilder.SchemaExtractor
 */
public class SchemaExtractor
{
    /**
     * Main method.
     * @param args
     *      no arguments
     * @throws Exception
     *      if an error occurs
     */
	@SuppressWarnings("nls")
	public static void main(String [] args) throws Exception
	{
		String schemaName = null;						// Provide a schema name (especially for Oracle) to prevent extracting all visible tables in all schemas.

		//String unityDatabaseName = "testdb_mongodb";				// The name of the database when used by UnityJDBC.  Defaults to database name if left as null.
		//String unityDatabaseName = "testdb_mysql";				// The name of the database when used by UnityJDBC.  Defaults to database name if left as null.
		String unityDatabaseName = "testdb_cassandra";				// The name of the database when used by UnityJDBC.  Defaults to database name if left as null.

		// Enter the URL of your database here that you want to generate the schema file for.
		//String url = "jdbc:mysql://localhost/test";				// MySQL
		String url = "jdbc:cassandra://127.0.0.1:9160";			// Cassandra
		// String url = "jdbc:mongo://localhost/test";				// Mongo
		// String url = "jdbc:sybase:Tds:localhost:";				// Sybase
		
		//jdbc:sybase:Tds:<server>:<port>/<database>?user=<userid>&password=<password>
		// This URL would be for the sample database in the release

		// Enter the JDBC driver name (driver must be in your classpath) for the chosen DBMS
		//String driverName = "com.mysql.jdbc.Driver";																
		//String driverName = "mongodb.jdbc.MongoDriver";																		// Microsoft SQL Server driver
		String driverName = "org.apache.cassandra.cql.jdbc.CassandraDriver";														// Microsoft Access (ODBC)		
						
		// Enter the local path (absolute or relative to current directory) of output XML file (XSpec)
		//String outputFileName = "src/main/java/xspec/testdb_mongodb.xml";
		//String outputFileName = "src/main/java/xspec/testdb_mysql.xml";
		String outputFileName = "src/main/java/xspec/testdb_cassandra.xml";
		
		try
		{	// Make database connection
		    Class.forName(driverName).newInstance();

        	// Extract database schema information into local data structure
            AnnotatedExtractor extractor = new AnnotatedExtractor();
            System.out.println("Extracting schema information.");

            /**
             * Parameters:
             * - String driverName 	- Java Class Name for Driver
             * - String url			- URL for connection
             * - String user		- user id (if not specified in URL)
             * - String password	- password (if not specified in URL) 
             * - Properties prop	- Connection properties
             * - String unityDBName	- Name to refer to database in virtualization
             * - String schema		- Schema pattern to extract
             * - String tableInc	- Tables to include in extraction (JDBC API pattern - use % for wildcard)
             * - String tableExc	- Tables to exclude in extraction (Java String pattern - use .* to match all characters)
             * - String catalogInc  - Catalogs to include in extraction (Java String pattern - use .* to match all characters)
             * - int statsType		- Statistics to collect: 0 - none, 1 - Row Counts Only, 2 - All Stats
             */
            String user = "";
            String password = "";
            extractor.extract(driverName, url, user, password, null, unityDatabaseName, schemaName, null, null, null, 0);

            // Write out schema information into an XML file
            System.out.println("Writing information into XML schema file.");            
            OutputStream os  = FileManager.openOutputFile(outputFileName);
            
            extractor.exportXML(os);

            System.out.println("Operation complete.");
		}
		catch(ClassNotFoundException ce)
		{	System.out.println("ClassNotFoundException for Driver: "+ce.getMessage());}
		catch (SQLException sqlEx)
        { System.out.println("SQLException "+ sqlEx.getMessage()); }
   	}
}
