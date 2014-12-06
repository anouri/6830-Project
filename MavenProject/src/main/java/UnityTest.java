import java.util.HashMap;
import java.util.List;

import org.jfree.ui.RefineryUtilities;

public class UnityTest {
	static HashMap<String, HashMap<String, Long>> results = new HashMap<String, HashMap<String, Long>>();
	
    public static void setup() throws Exception {    	
    	// BATCH PROCEDURE FOR MYSQL
    	///// make sure you run the following:
        ///// GRANT ALL PRIVILEGES ON *.* TO ''@'localhost';
        ///// FLUSH PRIVILEGES;
        String createProcedure =
                	"DROP TABLE IF EXISTS follower;" +
                    "CREATE TABLE follower (who_id int, whom_id int, PRIMARY KEY (who_id));"+
                    "DROP TABLE IF EXISTS message;" +
                    "CREATE TABLE message (message_id int, text text, PRIMARY KEY (message_id));";
        QueryExecutorAll.shema_creation_all(createProcedure);
    }

    public static void testInsertionFromJSON() throws Exception {
    	SQLSchemaParser schemaParser = new SQLSchemaParser("drop table follower;" +
                 "create table follower (" +
                 "who_id integer," +
                 "whom_id integer);" +
                 "drop table message;" +
                 "create table message (" +
                 "message_id integer primary key," +
                 "text text not null);");
    	List<String> allInsert = InsertData.InsertStatementFromJSON("{\"follower\":{\"colNames\":[\"who_id\",\"whom_id\"],\"colData\":[[4,1],[2,1],[3,1]]},\"message\":{\"colNames\":[\"message_id\",\"text\"],\"colData\":[[43,\"ecM\"],[56,\"ecM\"],[28,\"ecM\"],[51,\"ecM\"]]}}");
    	
    	for (int i = 0; i < allInsert.size(); i++) {
    		String s = allInsert.get(i);
        	HashMap<String, Long> result =  QueryExecutorAll.run_all(s);
	        if (result != null){
	        	results.put("insertion_"+i, result);
	        	System.out.println("insertion_"+i);
	        	System.out.println(s);
        	}
        	
        }
    	for (String name: results.keySet()){
            System.out.println(name);
        	
            HashMap<String, Long> runtimes = results.get(name);
            for (String db: runtimes.keySet()){
            	String runt = runtimes.get(db).toString();
            	System.out.println("db is: "+ db + " run time is: " + runt);
            }
    	}	
    }
    
    
    public static void cleanUpTables() throws Exception {
    	String[] tables = new String[]{"follower","message"};
    	QueryExecutorAll.dropTables(tables);
    }
    
    public static void testBatchInsertionFromJSON() throws Exception {
    	
    }
    
    public static void testDeletion() throws Exception {
    	
    }
    
    public static void testUpdate() throws Exception {
    	
    }
    
    public static void testJoin() throws Exception {
    	
    }
    
    public static void plotResult(){
    	 final BarChart demo = new BarChart("Bar Chart Demo", results);
         demo.pack();
         RefineryUtilities.centerFrameOnScreen(demo);
         demo.setVisible(true); 
    }
    
    public static void main(String[] args) throws Exception{
    	setup();
    	testInsertionFromJSON();
//    	cleanUpTables();
//    	testBatchInsertionFromJSON();
//    	testDeletion();
//    	testUpdate();
//    	testJoin();
//    	plotResult();
    }

}