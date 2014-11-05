import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * Created by trannguyen on 11/4/14.
 */
public class GettingStarted {
    Cluster cluster;
    Session session;
    public GettingStarted(String contactPoint, String keyspace) {
        cluster = Cluster.builder().addContactPoint(contactPoint).build();
        session = cluster.connect(keyspace);
    }

    public void executeAndPrint(String sqlStatement, String tableName) {
        session.execute(sqlStatement);
        ResultSet results = session.execute("Select * from " + tableName);
        for (Row row : results) {
            System.out.println(row.toString());
        }
    }

    public void close() {
        session.close();
        cluster.close();
    }

    public static void main(String[] args) {
        GettingStarted demo = new GettingStarted("127.0.0.1", "mykeyspace");
        String sqlStatement = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')";
        demo.executeAndPrint(sqlStatement,"users");
        demo.close();
    }
}
