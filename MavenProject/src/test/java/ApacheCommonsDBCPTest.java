import java.util.HashMap;
import java.util.List;

import org.jfree.ui.RefineryUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
 
public class ApacheCommonsDBCPTest {
	static HashMap<String, HashMap<String, Long>> results = new HashMap<String, HashMap<String, Long>>();
	
    public static void setup() throws Exception {
//        SQLSchemaParser schemaParser = new SQLSchemaParser("DROP TABLE IF EXISTS USER;CREATE TABLE USER (user_id integer PRIMARY KEY autoincrement, username text NOT NULL, email text NOT NULL, pw_hash text NOT NULL);\nDROP TABLE IF EXISTS follower;\nCREATE TABLE follower (who_id integer,whom_id integer);\nDROP TABLE IF EXISTS message;\nCREATE TABLE message (message_id integer PRIMARY KEY autoincrement,author_id integer NOT NULL,text text NOT NULL,pub_date integer);\n");
//        String shortQuery = schemaParser.getRawSchema();
    	
    	// BATCH PROCEDURE FOR MYSQL
    	///// make sure you run the following:
        ///// GRANT ALL PRIVILEGES ON *.* TO ''@'localhost';
        ///// FLUSH PRIVILEGES;
        String createProcedure_mysql =
        		"CREATE PROCEDURE mysql_create()"+
                "BEGIN " +
                	"DROP TABLE IF EXISTS follower;" +
                    "CREATE TABLE follower (who_id int, whom_id int);"+
                    "DROP TABLE IF EXISTS message;" +
                    "CREATE TABLE message (message_id int NOT NULL AUTO_INCREMENT, text VARCHAR(20), PRIMARY KEY (message_id));"+
                 "END";
        QueryExecutorAll.create_table_mysql(createProcedure_mysql);
        
        // BATCH PROCEDURE FOR CASSANDRA
      QueryExecutorAll.set_cassandra_keyspace("test");
      String createProcedure_cassandra =
              	"DROP TABLE IF EXISTS follower;" +
                  "CREATE TABLE follower (who_id int, whom_id int, PRIMARY KEY (who_id));"+
                  "DROP TABLE IF EXISTS message;" +
                  "CREATE TABLE message (message_id int, text text,PRIMARY KEY (message_id));";
      QueryExecutorAll.create_table_cassandra(createProcedure_cassandra);
      
      // NO need to do so for Mongo
      QueryExecutorAll.create_table_mongo("SELECT who_id, whom_id from follower");
      QueryExecutorAll.create_table_mongo("SELECT _id, message_id, text from message");
    }

    
    public static void testDataModificationFromJSON() throws Exception {
    	SQLSchemaParser schemaParser = new SQLSchemaParser("drop table follower;" +
                 "create table follower (" +
                 "who_id integer," +
                 "whom_id integer);" +
                 "drop table message;" +
                 "create table message (" +
                 "message_id integer primary key autoincrement," +
                 "text text not null);");
    	List<String> allInsert = InsertData.InsertStatementFromJSON("{\"follower\":{\"colNames\":[\"who_id\",\"whom_id\"],\"colData\":[[4,1],[2,1],[4,1]]},\"message\":{\"colNames\":[\"message_id\",\"text\"],\"colData\":[[43,\"ecM\"],[56,\"ecM\"],[28,\"ecM\"],[51,\"ecM\"]]}}");
    	
    	/// adding runtime for each insertion
    	for (int i = 0; i < allInsert.size(); i++) {
    		String s = allInsert.get(i);
        	HashMap<String, Long> result =  QueryExecutorAll.run_all(s);
	        if (result != null){
	        	results.put("insertion_"+i, result);
        	}
        	
        }

    }
    
    
    public static void cleanUpTables() throws Exception {
    	String[] tables = new String[]{"follower","message"};
    	QueryExecutorAll.dropTables(tables);
    }
    
    public void testBatchInsertionFromJSON() throws Exception {
    	
    }
    
    public static void plotResult(){
    	 final BarChart demo = new BarChart("Bar Chart Demo", results);
         demo.pack();
         RefineryUtilities.centerFrameOnScreen(demo);
         demo.setVisible(true); 
    }
    
    public static void main(String[] args) throws Exception{
    	setup();
    	testDataModificationFromJSON();
    	cleanUpTables();
    	plotResult();
    }
//    public static void main(String[] args) {
//
//    	////////Creation for Cassandra
//    	QueryExecutorAll.set_cassandra_keyspace("test");
//    	QueryExecutorAll.create_table_cassandra("CREATE TABLE example (a text,b text, PRIMARY KEY (a))");
//
//    	////////Creation for Mysql
//    	QueryExecutorAll.create_table_mysql("CREATE TABLE IF NOT EXISTS example (a VARCHAR(20), b VARCHAR(20))");
//
//    	////////Creation for Mongo
//    	QueryExecutorAll.create_table_mongo("SELECT a, b from example");
//
//    	///////Droptable
////    	HashMap<String, Long> result =  QueryExecutorAll.run_all("Drop TABLE example");
//
//    	// Select statement
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("SELECT a, b from example");
//    	for (String name: result.keySet()){
//            String key =name.toString();
//            String value = result.get(name).toString();
//            System.out.println("db is: "+ key + " run time is: " + value);
//    	}
//
//    }

}