import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class GetDataValueTest {
    Cluster cluster ;
    Session session;
    @Before
    public void setUp() throws Exception {
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        session = cluster.connect("mykeyspace");
        session.execute("CREATE TABLE if not exists users (firstname text,lastname text,age int,email text,city text,PRIMARY KEY (lastname));");
        String firstInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Kristin', 25, 'Austin', 'kristin@example.com', 'Bob');";
        String secondInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Tony', 10, 'California', 'tuantran@example.com', 'Bob');";
        String thirdInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Tran', 12, 'Seattle', 'viettran@example.com', 'Bob');";
        String forthInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Shirley', 20, 'Beijing', 'shirley@example.com', 'Bob');";
        List<String> sqlStatement = new ArrayList<String>(
                Arrays.asList(firstInsert, secondInsert, thirdInsert,forthInsert));
        for (String sql : sqlStatement) {
            session.execute(sql);
        }
    }

    @After
    public void tearDown() throws Exception {
        session.execute("Drop table users");
        cluster.close();
        session.close();
    }

    @Test
    public void testGetValueFromColumnTable() throws Exception {
        GetDataValue getDataValue = new GetDataValue();
        // users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')";
        Set<String> expectedAge = new HashSet<String>(
                Arrays.asList("25","10", "12", "20")
        );

        String actualAge = getDataValue.getValueFromColumnTable("age", "users");
        Assert.assertTrue(expectedAge.contains(actualAge));

        Set<String> expectedCity = new HashSet<String>(
                Arrays.asList("Austin","California", "Seattle", "Beijing")
        );
        String actualCity = getDataValue.getValueFromColumnTable("city", "users");
        Assert.assertTrue(expectedCity.contains(actualCity));
    }

    @Test
    public void testGetRowFromTable() throws Exception {
        GetDataValue getDataValue = new GetDataValue();
        // users (lastname, age, city, email, firstname) VALUES ('Jones', 35, 'Austin', 'bob@example.com', 'Bob')";
        Set<String> expected = new HashSet<String>(
                Arrays.asList("('Kristin',25,'Austin','kristin@example.com','Bob')","('Tony',10,'California','tuantran@example.com','Bob')",
                        "('Tran',12,'Seattle','viettran@example.com','Bob')", "('Shirley',20,'Beijing','shirley@example.com','Bob')")
        );

        String actual = getDataValue.getRowFromTable("users");
        Assert.assertTrue(expected.contains(actual));
    }
}