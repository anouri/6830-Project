import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class InsertDataTest {
	@BeforeClass
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

    @Test
    public void testDataModificationFromJSON() throws Exception {
//    	String sampleTables = '{' +
//        		"follower: {cardinality:3, fields:"+
//        			"[{category:Integer,length:4,name:who_id,distribution:uniform,"+
//        				"distinct:3,mean:0,stdv:0,min:1,max:10},"+
//        			"{category:Integer,length:4,name:whom_id,distribution:delta,"+
//        				"distinct:0,mean:0,stdv:0,min:1,max:10}]},"+
//        		"message: {cardinality:4, fields:"+
//        			"[{category:Integer,length:4,name:message_id,distribution:normal,"+
//        				"distinct:0,mean:50,stdv:10,min:0,max:0},"+
//        			"{category:String,length:128,name:text,distribution:uniform,"+
//        				"distinct:2,mean:0,stdv:0,min:3,max:6},"+
//        			"]}}";
//    	List<String> allInsert = InsertData.InsertStatementFromJSON(DataGenerator.generateJsonData(sampleTables));
    	SQLSchemaParser schemaParser = new SQLSchemaParser("drop table follower;" +
                 "create table follower (" +
                 "who_id integer," +
                 "whom_id integer);" +
                 "drop table message;" +
                 "create table message (" +
                 "message_id integer primary key autoincrement," +
                 "text text not null);");
    	List<String> allInsert = InsertData.InsertStatementFromJSON("{\"follower\":{\"colNames\":[\"who_id\",\"whom_id\"],\"colData\":[[4,1],[2,1],[4,1]]},\"message\":{\"colNames\":[\"message_id\",\"text\"],\"colData\":[[43,\"ecM\"],[56,\"ecM\"],[28,\"ecM\"],[51,\"ecM\"]]}}");
    	for (String s : allInsert) {
        	System.out.println(s);
        	HashMap<String, Long> result =  QueryExecutorAll.run_all(s);
        	for (String name: result.keySet()){
                String key =name.toString();
                String value = result.get(name).toString();
                System.out.println("db is: "+ key + " run time is: " + value);
        	}
        }
    }
  
    @Test
    public void testBatchStatementFromJSON() throws Exception {
    	
    }
}