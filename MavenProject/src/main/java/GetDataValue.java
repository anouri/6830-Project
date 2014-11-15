import com.datastax.driver.core.*;


/**
 * Created by trannguyen on 11/14/14.
 * This class will retrieve the value associated with a table
 * and column from the data base
 */
public class GetDataValue {

    Cluster cluster;
    Session session;
    public GetDataValue() {
        cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        session = cluster.connect("mykeyspace");
    }
    /**
     * In order to reduce the dependency between each component
     * and deal with randomness of the data, this class is created
     * to internally select the value out for doing the benching marking     *
     */

    /**
     * Get the value in the database associated with the table and column
     * Precondition: The table and column name need to be valid, this method
     * does no check to see if the table and column exist in the db
     * @param column name of the column
     * @param table name of the table
     * @return value of the record of null if there is none.
     */
    public String getValueFromColumnTable(String column, String table) {
        String sqlQuery = String.format("Select %s from mykeyspace.%s limit 1;", column, table);
        ResultSet results =  session.execute(sqlQuery);
        for (Row row : results) {
            if (row.getColumnDefinitions().getType(column).getName().equals(DataType.Name.INT)) {
                return String.valueOf(row.getInt(column));
            } else {
                return String.valueOf(row.getString(column));
            }
        }
        return null;
    }

    /**
     * Get the row in the database associated with the table
     * @param table the name of the table
     * @return row in the format (columnValue1, columnValue2, columnValue3)
     */
    public String getRowFromTable(String table) {
        String sqlQuery = String.format("Select * from mykeyspace.%s limit 1;", table);
        ResultSet results =  session.execute(sqlQuery);
        for (Row row : results) {
            StringBuffer buffer = new StringBuffer("(");
            for (int i = 0; i < row.getColumnDefinitions().size(); i ++) {
                if (row.getColumnDefinitions().getType(i).getName().equals(DataType.Name.INT)) {
                    buffer.append(row.getInt(i));
                } else {
                    buffer.append('\'').append(row.getString(i)).append('\'');
                }
                if (i == row.getColumnDefinitions().size()-1) {
                    buffer.append(")");
                } else {
                    buffer.append(",");
                }
            }
            return buffer.toString();
        }
        return null;
    }

}
