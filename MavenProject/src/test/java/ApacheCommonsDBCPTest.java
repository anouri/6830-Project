import java.util.HashMap;
 
public class ApacheCommonsDBCPTest {
 
    public static void main(String[] args) {
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("CREATE TABLE IF NOT EXISTS example (name VARCHAR(20))");
    	// FOR MONGO JUST RUN SELECT NO NEED FOR CREATE
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("SELECT a,b from example2");
    	//cassandra needs to create keyspace!
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("create keyspace test1 with replication = {'class':'SimpleStrategy','replication_factor':1}");
    	HashMap<String, Long> result =  QueryExecutorAll.run_all("CREATE TABLE test (firstname text,lastname text,age int,email text,city text,PRIMARY KEY (lastname))");
    	for (String name: result.keySet()){
            String key =name.toString();
            String value = result.get(name).toString();  
            System.out.println("db is: "+ key + "run time is: " + value);  
    	} 
    }

}