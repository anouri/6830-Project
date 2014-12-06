import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
                insertStatement.append(";");
                resultQuery.add(insertStatement.toString());
            }
        }
        if (!jsonObject.isNull("endOfData")) {
            isEOF = true;
        } else {
            isEOF = false;
        }
        return resultQuery;
    }

    public static boolean isDone() {
        return isEOF;
    }

    public static void main(String argsp[]) {
        SQLSchemaParser schemaParser = new SQLSchemaParser(SQLSchemaParser.EXAMPLE_SCHEMA.trim());
        String createProcedure = SQLSchemaParser.getRawSchema();
        QueryExecutorAll.shema_creation_all(createProcedure);
        FastDataGenerator fdg = new FastDataGenerator(SQLSchemaParser.EXAMPLE_DISTRIBUTION);
        HashMap<String, Long> results = new HashMap<String, Long>();
        while (true) {
            String distribution = fdg.generateMoreData();
            if (distribution == null) {
                break;
            }
            for (String s : InsertStatementFromJSON(distribution)) {
                HashMap<String, Long> result =  QueryExecutorAll.run_all(s);
                for (String key : result.keySet()) {
                    if (results.containsKey(key)) {
                        result.put(key, results.get(key) + result.get(key));
                    } else {
                        result.put(key, result.get(key));
                    }
                }
            };
        }
        for (String db: results.keySet()){
            long run = results.get(db);
            System.out.println("db is: "+ db + " run time is: " + run);
        }
    }
}
