import org.json.JSONObject;

/**
 * Created by trannguyen on 11/9/14.
 */
public interface SchemaParser {

    /**
     * Get the tuple description containing information about the column and type
     * @param tableName Table name
     * @return TupleDesc
     */
    public TupleDesc getTupleDescription(String tableName);

    /**
     *
     * @return list of table name
     */
    public String[] getTableName();

    /**
     *
     * @return raw sql schema
     */
    public String getRawSchema();

    /**
     * {
     *     "table":[ table1, table2, table3],
     *     "table1": {
     *         "columnName": [columnName1, columnName2, columnName3],
     *         "columnType": ["Integer","String","String"],
     *         "columnLength": [4,128,20]
     *     },
     *     "table2": {
     *         "columnName": [columnName1, columnName2, columnName3],
     *         "columnType": ["Integer","String","String"],
     *         "columnLength": [4,20,100]
     *     }
     *     "table3": {
     *         "columnName": [columnName1, columnName2, columnName3],
     *         "columnType": ["Integer","String","String"],
     *         "columnLength": [5,20,100]
     *     }
     * }
     * @return
     */
    public JSONObject getJSON();
}
