import java.util.HashMap;
 
public class ApacheCommonsDBCPTest {
 
    public static void main(String[] args) {
    	
    	////////Creation for Cassandra
    	QueryExecutorAll.create_table_cassandra("test", "CREATE TABLE example (a text,b text, PRIMARY KEY (a))");
    	
    	////////Creation for Mysql
    	QueryExecutorAll.create_table_mysql("CREATE TABLE IF NOT EXISTS example (a VARCHAR(20), b VARCHAR(20))");

    	////////Creation for Mongo
    	QueryExecutorAll.create_table_mongo("SELECT a, b from example");
    	
    	///////Droptable
//    	HashMap<String, Long> result =  QueryExecutorAll.run_all("Drop TABLE example");
    	
    	// Select statement
    	HashMap<String, Long> result =  QueryExecutorAll.run_all("SELECT a, b from example");
    	for (String name: result.keySet()){
            String key =name.toString();
            String value = result.get(name).toString();  
            System.out.println("db is: "+ key + " run time is: " + value);  
    	} 

    }

}