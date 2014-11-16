import java.util.HashMap;
 
public class ApacheCommonsDBCPTest {
 
    public static void main(String[] args) {
    	HashMap<String, Long> result =  QueryExecutorAll.run_all("select * from test");
    	for (String name: result.keySet()){
            String key =name.toString();
            String value = result.get(name).toString();  
            System.out.println("db is: "+ key + "run time is: " + value);  
    	} 
        
    }

}