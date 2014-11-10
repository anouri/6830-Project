/**
 * Created by trannguyen on 11/5/14.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This example assumes there exist tables a and b filled with some data.
 * On these tables some queries are executed and the JDBC driver is tested
 * on it's accuracy and robustness against 'users'.
 *
 * @author Fabian Groffen
 */
public class MonetDbDemo {
    public static void main(String[] args) throws Exception {
        // make sure the driver is loaded
        Class.forName("nl.cwi.monetdb.jdbc.MonetDriver");
        Connection con = DriverManager.getConnection("jdbc:monetdb://localhost/voc", "monetdb", "monetdb");
        Statement st = con.createStatement();
        ResultSet rs;

        st.execute("Insert into test (id, data) values (123, 'John')");
        rs = st.executeQuery("Select * from test");
        for (int i = 0; rs.next(); i++) {
            System.out.println(rs.getInt("id"));
            System.out.println(rs.getString("data"));
        }
        st.execute("delete from test where id = 123");
        con.close();
    }
}