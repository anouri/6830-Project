/**
 * Created by trannguyen on 11/14/14.
 * This class use cassandra database to implement the execute Query Method
 */
public class CassandraExecutor implements QueryExecutor {

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
        return 0;
    }
}
