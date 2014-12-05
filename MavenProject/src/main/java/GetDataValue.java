import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * Created by trannguyen on 11/14/14.
 * This class will retrieve the value associated with a table
 * and column from the data base
 */
public class GetDataValue {
    private static Connection conn = null;
    private static ResultSet results = null;
    private static Statement stmt = null;

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
    public static String getValueFromColumnTable(String column, String table) {
        String sqlQuery = String.format("Select %s from %s limit 1;", column, table);
        ResultSet results = executeAndGet(sqlQuery);
        if (results == null) {
            return null;
        }
        try {
            while (results.next()) {
                return results.getObject(1).toString();
            }
        } catch (SQLException e1) {

        } finally {
            close();
        }
        return null;
    }

    protected static ResultSet executeAndGet(String rawQuery) {
        DataSource ds = QueryExecutorAll.getDataSource("mysql");
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            if (stmt.execute(rawQuery)) {
                results = stmt.getResultSet();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return results;
    }

    private static void close() {
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


    /**
     * Get the row in the database associated with the table
     * @param table the name of the table
     * @return row in the format (columnValue1, columnValue2, columnValue3)
     */
    public static String[] getRowFromTable(String table) {
        String[] columnValues = new String[2];
        TupleDesc metaData = SQLSchemaParser.getTupleDescription(table);
        try {
            List<String> metaList = new ArrayList<String>();
            for (int i = 0 ;i < metaData.numFields(); i ++) {
                metaList.add(metaData.getFieldName(i));
            }
            columnValues[0] = getDatabaseFormat(metaList);
            StringBuffer buffer = null;
            int maxID = getMaxID(table);
            assert maxID != -1;
            List<Object> columnValue = getRandomColumnFromTable(table,maxID);

            buffer = new StringBuffer("(");
            for (int i = 0; i < metaData.numFields(); i ++) {
                if (metaData.getFieldName(i).equalsIgnoreCase(SQLSchemaParser.getPrimaryKey(table))) {
                    buffer.append(maxID+1);
                } else if (metaData.getFieldType(i).type == Type.SupportedType.INT_TYPE) {
                    buffer.append(columnValue.get(i));
                } else {
                    buffer.append('\'').append(columnValue.get(i)).append('\'');
                }
                if (i == metaData.numFields()-1) {
                    buffer.append(")");
                } else {
                    buffer.append(",");
                }
            }
            columnValues[1] =  buffer.toString();
        } finally {
            close();
        }
        return columnValues;
    }

    private static List<Object> getRandomColumnFromTable(String table, int maxID) {
        List<Object> result = new ArrayList<Object>();
        Random rand = new Random();
        Iterator<TupleDesc.TDItem> tdItem = SQLSchemaParser.getTupleDescription(table).iterator();
        while (tdItem.hasNext()) {
            TupleDesc.TDItem current = tdItem.next();
            String sqlQuery = String.format("Select %s from %s where %s=%d;", current.fieldName,table, SQLSchemaParser.getPrimaryKey(table),rand.nextInt(maxID));
            ResultSet queryResult =  executeAndGet(sqlQuery);
            try {
                while (queryResult.next()) {
                    result.add(queryResult.getObject(1).toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static int getMaxID(String table) {
        String sqlQuery = String.format("Select max(%s) from %s;", SQLSchemaParser.getPrimaryKey(table), table);
        ResultSet queryResult =  executeAndGet(sqlQuery);
        try {
            while (queryResult.next()) {
                return queryResult.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
        }
        return -1;
    }

    private static String getDatabaseFormat(List<String> iterator) {
        StringBuffer buffer = new StringBuffer("(");
        for (int i = 0; i < iterator.size(); i++) {
            buffer.append(iterator.get(i));
            if (i == iterator.size() - 1) {
                buffer.append(")");
            } else {
                buffer.append(",");
            }
        }
        return buffer.toString();
    }
}
