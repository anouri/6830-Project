import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.*;

/**
 * Created by trannguyen on 11/9/14.
 */
public class SQLSchemaParser {
    public static String EXAMPLE_SCHEMA = "CREATE TABLE patients (\n" +
            "subject_id int,\n" +
            "sex int, \n" +
            "dob int, \n" +
            "dod int,\n" +
            "hospital_expire_flg int,\n" +
            "PRIMARY KEY (subject_id)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE chartitems (\n" +
            "\titemid int,\n" +
            "\tlabel TEXT,\n" +
            "\tcategory TEXT,\n" +
            "\tdescription TEXT,\n" +
            "\tPRIMARY KEY (itemid)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE labitems (\n" +
            "\titemid int,\n" +
            "\ttest_name TEXT,\n" +
            "\tfluid TEXT,\n" +
            "\tcategory TEXT,\n" +
            "\tdescription TEXT,\n" +
            "\tPRIMARY KEY (itemid)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE meditems (\n" +
            "\titemid int,\n" +
            "\tlabel TEXT,\n" +
            "\tcategory TEXT,\n" +
            "\tPRIMARY KEY (itemid)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE icustayevents (\n" +
            "icustay_id int,\n" +
            "subject_id int,\n" +
            "intime int,\n" +
            "outtime int,\n" +
            "PRIMARY KEY (icustay_id)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE chartevents (\n" +
            "\tchartevent_id int,\n" +
            "\tsubject_id int,\n" +
            "\ticustay_id int,\n" +
            "\titemid int,\n" +
            "\tcharttime int,\n" +
            "\telemid int,\n" +
            "\tresultstatus TEXT,\n" +
            "\tannotation TEXT,\n" +
            "PRIMARY KEY (chartevent_id)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE labevents (\n" +
            "\tlabevent_id int,\n" +
            "\tsubject_id int,\n" +
            "\ticustay_id int,\n" +
            "\titemid int,\n" +
            "\tcharttime int,\n" +
            "\tlabvalue TEXT,\n" +
            "\tPRIMARY KEY (labevent_id)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE meddurations (\n" +
            "\tmeddurations_id int,\n" +
            "\ticustay_id int,\n" +
            "\titemid int,\n" +
            "\tmedevent int,\n" +
            "\tstarttime int,\n" +
            "\tendtime int,\n" +
            "\tduration int,\n" +
            "\tPRIMARY KEY (meddurations_id)\n" +
            ");\n" +
            "\n" +
            "CREATE TABLE medevents (\n" +
            "\tmedevent_id int,\n" +
            "\tsubject_id int,\n" +
            "\ticustay_id int,\n" +
            "\titemid int,\n" +
            "\tmedduration int,\n" +
            "\tcharttime int,\n" +
            "\tvolume int,\n" +
            "\tdose int,\n" +
            "\tsolution TEXT,\n" +
            "\troute TEXT,\n" +
            "\tstopped TEXT,\n" +
            "\tsite TEXT,\n" +
            "\tannotation TEXT,\n" +
            "\tPRIMARY KEY (medevent_id)\n" +
            ");";

    public static String EXAMPLE_DISTRIBUTION = "{patients:{cardinality:100,fields:[{category:Integer,length:4,name:subject_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:sex,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1},{category:Integer,length:4,name:dob,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:dod,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:hospital_expire_flag,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1}]},icustayevents:{cardinality:1000,fields:[{category:Integer,length:4,name:icustay_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:intime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:outtime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000}]},chartevents:{cardinality:300,fields:[{category:Integer,length:4,name:chartevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1000},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:elemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:String,length:4,name:resultstatus,distribution:normal,distinct:null,mean:7,stdv:3,min:null,max:null},{category:String,length:4,name:annotation,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},chartitems:{cardinality:100,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:label,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:description,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},labitems:{cardinality:50,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:test_name,distribution:normal,distinct:null,mean:15,stdv:5,min:null,max:null},{category:String,length:4,name:fluid,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:10},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:description,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},labevents:{cardinality:200,fields:[{category:Integer,length:4,name:labevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1000},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:50},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:String,length:4,name:labvalue,distribution:uniform,distinct:null,mean:null,stdv:null,min:10,max:50}]},medevents:{cardinality:250,fields:[{category:Integer,length:4,name:medevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1000},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:medduration,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:volume,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:10000},{category:Integer,length:4,name:dose,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:10000},{category:String,length:4,name:solution,distribution:uniform,distinct:null,mean:null,stdv:null,min:10,max:20},{category:String,length:4,name:route,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:stopped,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:site,distribution:normal,distinct:null,mean:10,stdv:3,min:null,max:null},{category:String,length:4,name:annotation,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},meditems:{cardinality:100,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:label,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3}]},meddurations:{cardinality:100,fields:[{category:Integer,length:4,name:meddurations_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1000},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:Integer,length:4,name:medevent,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:250},{category:Integer,length:4,name:starttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:endtime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:duration,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100000}]}}";
    private static String rawSqlSchema;
    private static String[] tableNameStatic;
    private static TupleDesc tupleDescStatic;
    private static JSONObject tableJSON;
    private static Set<String> primaryKeyWord = new HashSet<String> (Arrays.asList("primary", "key", "auto_incremented"));
    private static Map<String, String> primaryKeyTable = new HashMap<String, String>();
    private static Map<String, ForeignKeyIndex> foreignKeyIndexTable = new HashMap<String, ForeignKeyIndex>();

    public SQLSchemaParser(String rawSqlSchema) {
        this.rawSqlSchema = rawSqlSchema;
    }

    public static void setRawSqlSchema(String raw) {
        rawSqlSchema = raw;
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
                String primaryKey = getPrimaryKey(create.getIndexes());
                primaryKeyTable.put(name, primaryKey);
                if (name.equalsIgnoreCase(tableName)) {
                    List<ColumnDefinition> columns = create.getColumnDefinitions();
                    List<Index> indexes = create.getIndexes();
                    for (ColumnDefinition def : columns) {
                        columnName.add(def.getColumnName());

                        if (def.getColDataType().getDataType().equalsIgnoreCase("varchar")) {
                            columnType.add(new Type(Type.SupportedType.STRING_TYPE,Integer.parseInt((String) def.getColDataType().getArgumentsStringList().get(0))));
                        } else if (def.getColDataType().getDataType().equalsIgnoreCase("int") || def.getColDataType().getDataType().equalsIgnoreCase("integer") || def.getColDataType().getDataType().equalsIgnoreCase("bigint")) {
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
        tupleDescStatic = new TupleDesc(resultType, resultName);
        return tupleDescStatic;
    }

    private static String getPrimaryKey(List indices) {
        if (indices != null) {
            for (Object extra : indices) {
                if (extra instanceof Index) {
                    if (((Index) extra).getType().equalsIgnoreCase("primary key")) {
                        for (Object column : ((Index) extra).getColumnsNames()) {
                            return column.toString();
                        }
                        break;
                    }
                }
            }
        }
        return "";
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

    /**
     * ONLY CALL THIS FUNCTION AFTER getTupleDescription
     * @param tableName
     * @return
     */
    public static String getPrimaryKey(String tableName) {
        assert primaryKeyTable.containsKey(tableName);
        return primaryKeyTable.get(tableName);
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
            JSONObject primaryKey = null;
            if (primaryKeyTable.containsKey(tName)) {
                primaryKey = new JSONObject();
                primaryKey.put("primaryKey", primaryKeyTable.get(tName));
            }
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
            if (primaryKey != null) {
                all.put(primaryKey);
            }
            result.put(tName, all);
        }
        tableJSON = result;
        return tableJSON;
    }

    public static void main(String[] args) {
        String rawSchema = EXAMPLE_SCHEMA;
        SQLSchemaParser parser = new SQLSchemaParser(rawSchema.trim());
        System.out.println(parser.getJSON().toString());
        System.out.println(SQLSchemaParser.getPrimaryKey("patients"));
        System.out.println(SQLSchemaParser.getPrimaryKey("icustayevents"));
        System.out.println(SQLSchemaParser.getPrimaryKey("chartevents"));
        System.out.println(SQLSchemaParser.getPrimaryKey("chartitems"));
        System.out.println(SQLSchemaParser.getPrimaryKey("labitems"));
        System.out.println(SQLSchemaParser.getPrimaryKey("labevents"));
        System.out.println(SQLSchemaParser.getPrimaryKey("medevents"));
        System.out.println(SQLSchemaParser.getPrimaryKey("meditems"));
        System.out.println(SQLSchemaParser.getPrimaryKey("meddurations"));
    }

}
