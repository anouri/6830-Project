import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Created by trannguyen on 11/14/14.
 * This class use cassandra database to implement the execute Query Method
 */
public class CassandraExecutor implements QueryExecutor {
	private Cluster cluster;
    private Session session;
    private String contactPoint = "127.0.0.1";
    private String keyspace = "mykeyspace";
    
    public CassandraExecutor(String contactPoint, String keyspace) throws Exception{
    	this.contactPoint = contactPoint;
    	this.keyspace = keyspace;
    	if (!connect()){
    		throw new Exception("Could not initiate the the Cassandra Executor");
    	}
    }
    public CassandraExecutor() throws Exception{
    	this.contactPoint = contactPoint;
    	this.keyspace = keyspace;
    	if (!connect()){
    		throw new Exception("Could not initiate the the Cassandra Executor");
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
    public long executeQuery(String rawSQLQuery) {
    	long fStart = System.currentTimeMillis();
    	session.execute(rawSQLQuery);
    	long fEnd = System.currentTimeMillis();
	    return (fEnd - fStart);
    }

	public boolean connect() {
		this.cluster = Cluster.builder().addContactPoint(contactPoint).build();
        this.session = cluster.connect(keyspace);
        return true;
	}

	public void cleanUP() {
        session.close();
        cluster.close();
		
	}
}
