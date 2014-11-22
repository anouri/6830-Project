import java.util.HashMap;
 
public class ApacheCommonsDBCPTest {
 
    public static void main(String[] args) {
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("CREATE TABLE IF NOT EXISTS example (a VARCHAR(20), b VARCHAR(20))");
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("Drop TABLE example");
    	
    	// FOR MONGO JUST RUN SELECT NO NEED FOR CREATE
    	HashMap<String, Long> result =  QueryExecutorAll.run_all("SELECT a, b from example");
    	//cassandra needs to create keyspace!
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("create keyspace test3 with replication = {'class':'SimpleStrategy','replication_factor':1}");
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("CREATE TABLE example (a text,b text, PRIMARY KEY (a))");
    	for (String name: result.keySet()){
            String key =name.toString();
            String value = result.get(name).toString();  
            System.out.println("db is: "+ key + " run time is: " + value);  
    	} 
//    	for (String name: result2.keySet()){
//            String key =name.toString();
//            String value = result2.get(name).toString();  
//            System.out.println("db is: "+ key + "run time is: " + value);  
//    	} 
    }

}