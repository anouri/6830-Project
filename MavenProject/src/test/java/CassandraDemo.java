import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Created by trannguyen on 11/4/14.
 */
public class CassandraDemo {
    Cluster cluster;
    Session session;
    public CassandraDemo(String contactPoint, String keyspace) {
        cluster = Cluster.builder().addContactPoint(contactPoint).build();
        session = cluster.connect(keyspace);
        session.execute("CREATE TABLE if not exists users (firstname text,lastname text,age int,email text,city text,PRIMARY KEY (lastname));");
    }

    public void executeAndPrint(String sqlStatement, String tableName) {
        session.execute(sqlStatement);
        ResultSet results = session.execute("Select * from " + tableName);
        for (Row row : results) {
            System.out.println(row.toString());
        }
    }

    public void close() {
        session.execute("DROP TABLE users;");
        session.close();
        cluster.close();
    }

    public static void main(String[] args) {
        CassandraDemo demo = new CassandraDemo("127.0.0.1", "mykeyspace");
        String sqlStatement = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')";
        demo.executeAndPrint(sqlStatement,"users");
        demo.close();
    }
}
