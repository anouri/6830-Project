import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class GetDataValueTest {

    Connection conn;
    ResultSet results;
    Statement stmt;
    @Before
    public void setUp() throws Exception {
        conn = null;
        results = null;
        stmt = null;
        DataSource ds = QueryExecutorAll.getDataSource("mysql");
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.execute("CREATE TABLE if not exists users (lastname text,age int,city text,email text, firstname text);");
            String firstInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Kristin', 25, 'Austin', 'kristin@example.com', 'Bob');";
            String secondInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Tony', 10, 'California', 'tuantran@example.com', 'Bob');";
            String thirdInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Tran', 12, 'Seattle', 'viettran@example.com', 'Bob');";
            String forthInsert = "INSERT INTO users (lastname, age, city, email, firstname) VALUES ('Shirley', 20, 'Beijing', 'shirley@example.com', 'Bob');";
            List<String> sqlStatement = new ArrayList<String>(
                    Arrays.asList(firstInsert, secondInsert, thirdInsert, forthInsert));
            for (String sql : sqlStatement) {
                stmt.execute(sql);
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    @After
    public void tearDown() throws Exception {
            stmt.execute("Drop table users");
            if (results != null) {
                try {
                    results.close();
                } catch (SQLException sqlEx) {
                } // ignore

                results = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException sqlEx) {
                } // ignore

                conn = null;
            }
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

        String[] actual = getDataValue.getRowFromTable("users");
        Assert.assertTrue(expected.contains(actual[1]));
        Assert.assertEquals("(lastname,age,city,email,firstname)", actual[0]);
    }
}