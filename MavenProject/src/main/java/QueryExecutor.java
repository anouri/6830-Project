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
     * @return time it takes to excute those in mili seconds
     */
    public long executeQuery(String rawSQLQuery);
}
