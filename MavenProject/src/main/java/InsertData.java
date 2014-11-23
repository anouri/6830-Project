import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by trannguyen on 11/22/14.
 */
public class InsertData {
    private static boolean isEOF = false;
    private static String COLUMN_NAME = "colNames";
    private static String COLUMN_DATA = "colData";

    public static List<String> InsertStatementFromJSON(String jsonData) {
        List<String> resultQuery = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject(jsonData);
        String[] tableName = null;
        try {
            tableName = SQLSchemaParser.getTableName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String table: tableName) {
            JSONObject tableObject = jsonObject.getJSONObject(table);
            JSONArray columnName = tableObject.getJSONArray(COLUMN_NAME);
            StringBuffer buffer = new StringBuffer(String.format("INSERT INTO %s (",table));
            for (int i = 0; i < columnName.length(); i++) {
                buffer.append(columnName.getString(i));
                if (i == columnName.length()-1) {
                    buffer.append(")");
                }
                else {
                    buffer.append(",");
                }
            }
            JSONArray columnData = tableObject.getJSONArray(COLUMN_DATA);
            for (int i = 0 ; i < columnData.length(); i++) {
                StringBuffer insertStatement = new StringBuffer(String.format(buffer.toString() + " VALUES %s", columnData.get(i).toString()
                        .replace("[", "(").replace("]", ")").replace("\"", "'")));
                resultQuery.add(insertStatement.toString());
            }
        }
        return resultQuery;
    }

    public static void main(String argsp[]) {
        SQLSchemaParser schemaParser = new SQLSchemaParser("drop table follower;" +
                "create table follower (" +
                "who_id integer," +
                "whom_id integer);" +
                "drop table message;" +
                "create table message (" +
                "message_id integer primary key autoincrement," +
                "text text not null);");
        System.out.println(InsertStatementFromJSON("{\"follower\":{\"colNames\":[\"who_id\",\"whom_id\"],\"colData\":[[4,1],[2,1],[4,1]]},\"message\":{\"colNames\":[\"message_id\",\"text\"],\"colData\":[[43,\"ecM\"],[56,\"ecM\"],[28,\"ecM\"],[51,\"ecM\"]]}}"));

    }
}
