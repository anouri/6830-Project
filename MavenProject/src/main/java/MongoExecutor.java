import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * Created by trannguyen on 11/14/14.
 */
public class MongoExecutor implements QueryExecutor {

	private MongoClient mongoClient;
	private String host;
	private int port;
	private String db_name;
	private DB db;
	
	public MongoExecutor(String host, int port, String db_name) throws Exception{
		this.host = host;
		this.port = port;
		this.db_name = db_name;
		if(!connect()){
			throw new Exception("Could not initiate the the Mongo Executor");
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
    @Override
    public long executeQuery(String rawSQLQuery) {
    	long fStart = System.currentTimeMillis();
    	// translation
    	long fEnd = System.currentTimeMillis();
    	return (fEnd - fStart);
    }

    @Override
    public boolean connect(){
    	 try{   
             this.mongoClient = new MongoClient( host , port);
             this.db = mongoClient.getDB(db_name);
             return true;
//             boolean auth = db.authenticate(myUserName, myPassword);
//    		 System.out.println("Authentication: "+auth);
          }catch(Exception e){
    	     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    	     return false;
    	  }
    }
	@Override
	public void cleanUP() {
		mongoClient.close();		
	}
}
