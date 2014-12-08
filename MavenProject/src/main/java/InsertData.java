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

    public static List<QueryArgs> QueryArgsFromJSON(String jsonData) {
        List<QueryArgs> queryArgs = new ArrayList<QueryArgs>();
        JSONObject jsonObject = new JSONObject(jsonData);
        String[] tableName = null;
        try {
            tableName = SQLSchemaParser.getTableName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String table: tableName) {
            JSONObject tableObject = jsonObject.getJSONObject(table);
            JSONArray columnName = tableObject.getJSONArray(COLUMN_NAME); // [colName1, colName2, colName3]
            JSONArray columnData = tableObject.getJSONArray(COLUMN_DATA);  // [ [1,2,'x'], [2,3,'y'], [3,4,'z'] ]
            
            // Build the INSERT query template
            StringBuffer insertTemplate = new StringBuffer(String.format("INSERT INTO %s (",table));
            for (int i = 0; i < columnName.length(); i++) {
                insertTemplate.append(columnName.getString(i));
                if (i == columnName.length()-1) {
                    insertTemplate.append(")");
                }
                else {
                    insertTemplate.append(",");
                }
            }
            insertTemplate.append(" VALUES (");
            if (columnData.length() == 0) continue;
            int numCols = columnData.getJSONArray(0).length();
            for (int i = 0; i < numCols; i++) {
                insertTemplate.append("?");
                if (i == numCols-1) {
                    insertTemplate.append(")");
                } else {
                    insertTemplate.append(",");
                }
            }

            // Build the list of values to insert with the Prepared Statement
            Arguments[] arguments = new Arguments[columnData.length()];
            for (int i = 0 ; i < columnData.length(); i++) {
                JSONArray values = columnData.getJSONArray(i);    // [1,2,'x']
                Data[] dataList = new Data[values.length()];
                for (int j = 0; j < values.length(); j++) {
                    Object val = values.get(j);
                    // if (tupleDesc.getFieldType(j).getType() == Type.SupportedType.INT_TYPE) {
                    //     dataList[j] = new Data(true, values.getInt(j));
                    // } else {
                    //     dataList[j] = new Data(false, values.getString(j));
                    // }
                    if (val instanceof String) {
                        dataList[j] = new Data(false, (String)val);
                    } else {
                        dataList[j] = new Data(true, (Integer)val);
                    }
                }
                arguments[i] = new Arguments(dataList);
            }

            queryArgs.add(new QueryArgs(insertTemplate.toString(), arguments));
        }
        if (!jsonObject.isNull("endOfData")) {
            isEOF = true;
        } else {
            isEOF = false;
        }
        return queryArgs;
    }

    public static boolean isDone() {
        return isEOF;
    }

    public static void main(String argsp[]) {
        // Test InsertStatementFromJSON

        // String[] tableName = new String[] {"chartevents","chartitems","follower","icustayevents","labevents","labitems","meddurations","medevents","meditems", "message","patients"};
        // QueryExecutorAll.dropTables(tableName);
        // SQLSchemaParser schemaParser = new SQLSchemaParser(SQLSchemaParser.EXAMPLE_SCHEMA.trim());
        // String createProcedure = SQLSchemaParser.getRawSchema();
        // QueryExecutorAll.shema_creation_all(createProcedure);
        // FastDataGenerator fdg = new FastDataGenerator(SQLSchemaParser.EXAMPLE_DISTRIBUTION);
        // HashMap<String, Long> results = new HashMap<String, Long>();
        // while (true) {
        //     String distribution = fdg.generateMoreData();
        //     if (distribution == null) {
        //         break;
        //     }
        //     for (String s : InsertStatementFromJSON(distribution)) {
        //         HashMap<String, Long> result =  QueryExecutorAll.run_all(s);
        //         for (String key : result.keySet()) {
        //             if (results.containsKey(key)) {
        //                 results.put(key, results.get(key) + result.get(key));
        //             } else {
        //                 results.put(key, result.get(key));
        //             }

        //         }
        //     };
        //     for (String db: results.keySet()){
        //         long run = results.get(db);
        //         System.out.println("db is: "+ db + " run time is: " + run);
        //     }
        // }
        // for (String db: results.keySet()){
        //     long run = results.get(db);
        //     System.out.println("db is: "+ db + " run time is: " + run);
        // }

        // Test QueryArgsFromJSON
        String[] tableName = new String[] {"chartevents","chartitems","follower","icustayevents","labevents","labitems","meddurations","medevents","meditems", "message","patients"};
        QueryExecutorAll.dropTables(tableName);
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
            for (QueryArgs qa : QueryArgsFromJSON(distribution)) {
                HashMap<String, Long> result =  QueryExecutorAll.bulk_run_insertion(qa);
                for (String key : result.keySet()) {
                    if (results.containsKey(key)) {
                        results.put(key, results.get(key) + result.get(key));
                    } else {
                        results.put(key, result.get(key));
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
