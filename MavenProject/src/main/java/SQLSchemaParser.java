import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.*;

/**
 * Created by trannguyen on 11/9/14.
 */
public class SQLSchemaParser {
    private static String rawSqlSchema;
    private static String[] tableNameStatic;
    private static TupleDesc tupleDescStatic;
    private static JSONObject tableJSON;
    private static Set<String> primaryKeyWord = new HashSet<String> (Arrays.asList("primary", "key", "auto_incremented"));

    public SQLSchemaParser(String rawSqlSchema) {
        this.rawSqlSchema = rawSqlSchema;
    }

    private static String[] readSQL(String rawSqlSchema) {
        return rawSqlSchema.trim().split(";");
    }

    public static TupleDesc getTupleDescription(String tableName) {
        List<Type> columnType = new ArrayList<Type>();
        List<String> columnName = new ArrayList<String>();
        CCJSqlParserManager pm = new CCJSqlParserManager();

        String[] sqlStatements = readSQL(rawSqlSchema);

        for (String sqlStatement : sqlStatements) {
            Statement statement = null;
            try {
                statement = pm.parse(new StringReader(sqlStatement));
            } catch (JSQLParserException e) {
                e.printStackTrace();
            }
            if (statement == null) {
                continue;
            }
            if (statement instanceof CreateTable) {
                CreateTable create = (CreateTable) statement;
                String name = create.getTable().getName();

                if (name.equalsIgnoreCase(tableName)) {
                    List<ColumnDefinition> columns = create.getColumnDefinitions();
                    for (ColumnDefinition def : columns) {
                        columnName.add(def.getColumnName());
                        boolean isPrimaryKey = false;
                        for (Object extra: def.getColumnSpecStrings()) {
                            if (primaryKeyWord.contains(extra.toString().toLowerCase())) {
                                isPrimaryKey = true;
                                break;
                            }
                        }
                        if (def.getColDataType().getDataType().equalsIgnoreCase("varchar")) {
                            columnType.add(new Type(Type.SupportedType.STRING_TYPE, isPrimaryKey,Integer.parseInt((String) def.getColDataType().getArgumentsStringList().get(0))));
                        } else if (def.getColDataType().getDataType().equalsIgnoreCase("integer") || def.getColDataType().getDataType().equalsIgnoreCase("bigint")) {
                            columnType.add(new Type(Type.SupportedType.INT_TYPE, isPrimaryKey));
                        } else if (def.getColDataType().getDataType().equalsIgnoreCase("text")) {
                            columnType.add(new Type(Type.SupportedType.STRING_TYPE, isPrimaryKey));
                        } else {
                            System.out.println(def.getColDataType().toString());
                        }
                    }
                }
            }
        }
        Type[] resultType = new Type[columnType.size()];
        String[] resultName = new String[columnName.size()];
        columnType.toArray(resultType);
        columnName.toArray(resultName);
        tupleDescStatic = new TupleDesc(resultType, resultName);
        return tupleDescStatic;
    }

    public static String[] getTableName() {
        CCJSqlParserManager pm = new CCJSqlParserManager();
        String[] sqlStatements = readSQL(rawSqlSchema);
        List<String> tableName = new ArrayList<String>();
        for (String sqlStatement : sqlStatements) {
            Statement statement = null;
            try {
                statement = pm.parse(new StringReader(sqlStatement));
            } catch (JSQLParserException e) {
                e.printStackTrace();
            }
            if (statement == null) {
                continue;
            }
            if (statement instanceof CreateTable) {
                CreateTable create = (CreateTable) statement;
                String name = create.getTable().getName();
                tableName.add(name);
            }
        }
        tableNameStatic = new String[tableName.size()];
        tableName.toArray(tableNameStatic);
        return tableNameStatic;
    }

    public static String getRawSchema() {
        return rawSqlSchema;
    }

    public static JSONObject getJSON() {
        String[] tableNames = getTableName();
        JSONObject result = new JSONObject();
        result.put("table", tableNames);
        for (String tName:tableNames) {
            TupleDesc columnInfo = getTupleDescription(tName);
            Iterator<TupleDesc.TDItem> iter = columnInfo.iterator();
            JSONArray columnName = new JSONArray();
            JSONArray columnType = new JSONArray();
            JSONArray columnLength = new JSONArray();
            JSONObject primaryKey = new JSONObject();
            while (iter.hasNext()) {
                TupleDesc.TDItem current = iter.next();
                columnName.put(current.fieldName);
                columnType.put(current.fieldType.toString());
                columnLength.put(current.fieldType.getLen());
                if (current.fieldType.isPrimaryKey) {
                    primaryKey.put("primaryKey", current.fieldName);
                }
            }
            JSONObject nameJson = new JSONObject();
            nameJson.put("columnName", columnName);
            JSONObject typeJson = new JSONObject();
            typeJson.put("columnType", columnType);
            JSONObject lengthJson = new JSONObject();
            lengthJson.put("columnLength", columnLength);
            JSONArray all = new JSONArray();
            all.put(nameJson);
            all.put(typeJson);
            all.put(lengthJson);
            all.put(primaryKey);
            result.put(tName, all);
        }
        tableJSON = result;
        return tableJSON;
    }

    public static void main(String[] args) {
        String rawSchema = "drop table user;" +
                "create table user (" +
                "user_id integer primary key auto_increment," +
                "username text not null," +
                "email text not null," +
                "pw_hash text not null);" +
                "drop table follower;" +
                "create table follower (" +
                "who_id integer," +
                "whom_id integer);" +
                "drop table message;" +
                "create table message (" +
                "message_id integer primary key auto_increment," +
                "author_id integer not null," +
                "text text not null," +
                "pub_date integer);";
        SQLSchemaParser parser = new SQLSchemaParser(rawSchema);
        System.out.println(parser.getJSON().toString());
    }

}
