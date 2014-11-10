import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by trannguyen on 11/9/14.
 */
public class SQLSchemaParser implements SchemaParser {
    String rawSqlSchema;
    public SQLSchemaParser(String rawSqlSchema) {
        this.rawSqlSchema = rawSqlSchema;
    }

    private String[] readSQL(String rawSqlSchema) {
        return rawSqlSchema.split(";");
    }

    @Override
    public TupleDesc getTupleDescription(String tableName) {
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
                        if (def.getColDataType().getDataType().equalsIgnoreCase("varchar")) {
                            columnType.add(new Type(Type.SupportedType.STRING_TYPE, Integer.parseInt(def.getColDataType().getArgumentsStringList().get(0))));
                        } else if (def.getColDataType().getDataType().equalsIgnoreCase("integer")) {
                            columnType.add(new Type(Type.SupportedType.INT_TYPE));
                        } else if (def.getColDataType().getDataType().equalsIgnoreCase("text")) {
                            columnType.add(new Type(Type.SupportedType.STRING_TYPE));
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
        return new TupleDesc(resultType,resultName);
    }

    @Override
    public String[] getTableName() {
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
        String[] resultTableName = new String[tableName.size()];
        tableName.toArray(resultTableName);
        return resultTableName;
    }

    @Override
    public String getRawSchema() {
        return rawSqlSchema;
    }

    @Override
    public JSONObject getJSON() {
        String[] tableNames = getTableName();
        JSONObject result = new JSONObject();
        result.put("table", tableNames);
        for (String tName:tableNames) {
            TupleDesc columnInfo = getTupleDescription(tName);
            Iterator<TupleDesc.TDItem> iter = columnInfo.iterator();
            JSONArray columnName = new JSONArray();
            JSONArray columnType = new JSONArray();
            JSONArray columnLength = new JSONArray();
            while (iter.hasNext()) {
                TupleDesc.TDItem current = iter.next();
                columnName.put(current.fieldName);
                columnType.put(current.fieldType.toString());
                columnLength.put(current.fieldType.getLen());
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
            result.put(tName, all);
        }
        return result;
    }

}
