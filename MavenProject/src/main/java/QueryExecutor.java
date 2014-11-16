/**
 * Created by trannguyen on 11/14/14.
 * The BenchMarking class will call the function in this
 * interface to execute the query
 */
public interface QueryExecutor {

    /**
     * Each implementation of this will take in
     * the raw sql query and execute in its underlying database
     * returning how long it takes to execute that statement
     * @param rawSQLQuery SQL statement
     * @return time it takes to excute those in mili seconds, if failed to execute, return 0
     */
    public long executeQuery(String rawSQLQuery);
    
    /**
     * Each implementation of this will establish connection with the specified db config
     * in each specific executor constructor
     * returning whether the connection was successful
     * @return true for successful connection, and false otherwise
     */
    public boolean connect();
    
    /**
     * Each implementation of this will free the db resources created with this executor
     */
    public void cleanUP();
    
}
