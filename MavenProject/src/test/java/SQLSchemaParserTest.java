import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class SQLSchemaParserTest {


    SchemaParser sqlSchemaParser;
    String rawSchema;

    @Test
    public void testGetTupleDescription() throws Exception {
        rawSchema = "drop table user;" +
                "create table user (" +
                "user_id integer primary key autoincrement," +
                "username text not null," +
                "email text not null," +
                "pw_hash text not null);";
        sqlSchemaParser = new SQLSchemaParser(rawSchema);
        TupleDesc expected = new TupleDesc(new Type[] {new Type(Type.SupportedType.INT_TYPE), new Type(Type.SupportedType.STRING_TYPE),
                new Type(Type.SupportedType.STRING_TYPE), new Type(Type.SupportedType.STRING_TYPE)}, new String[] {"user_id","username","email", "pw_hash"});
        TupleDesc actual = sqlSchemaParser.getTupleDescription("user");
        Assert.assertEquals(actual, expected);

    }

    @Test
    public void testGetTableName() throws Exception {
        rawSchema = "drop table user;" +
                "create table user (" +
                "user_id integer primary key autoincrement," +
                "username text not null," +
                "email text not null," +
                "pw_hash text not null);" +
                "drop table follower;" +
                "create table follower (" +
                "who_id integer," +
                "whom_id integer);" +
                "drop table message;" +
                "create table message (" +
                "message_id integer primary key autoincrement," +
                "author_id integer not null," +
                "text text not null," +
                "pub_date integer);";
        sqlSchemaParser = new SQLSchemaParser(rawSchema);
        String[] expectedTableName = new String[] {"user", "follower", "message"};
        String[] actualTableName = sqlSchemaParser.getTableName();
        Assert.assertEquals(actualTableName,expectedTableName);
    }

    @Test
    public void testGetJSON() {
        rawSchema = "drop table user;" +
                "create table user (" +
                "user_id integer primary key autoincrement," +
                "username text not null," +
                "email text not null," +
                "pw_hash text not null);";
        sqlSchemaParser = new SQLSchemaParser(rawSchema);
        JSONObject expected = new JSONObject();
        expected.put("table", new String[] {"user",});
        JSONArray columnType = new JSONArray();
        columnType.put(Type.SupportedType.INT_TYPE.toString());
        columnType.put(Type.SupportedType.STRING_TYPE.toString());
        columnType.put(Type.SupportedType.STRING_TYPE.toString());
        columnType.put(Type.SupportedType.STRING_TYPE.toString());
        JSONObject typeJson = new JSONObject();
        typeJson.put("columnType",columnType);
        JSONArray columnName = new JSONArray();
        columnName.put("user_id");
        columnName.put("username");
        columnName.put("email");
        columnName.put("pw_hash");
        JSONObject nameJSON = new JSONObject();
        nameJSON.put("columnName", columnName);
        JSONArray columnLength = new JSONArray();
        columnLength.put(4);
        columnLength.put(128);
        columnLength.put(128);
        columnLength.put(128);
        JSONObject lengthJSON = new JSONObject();
        lengthJSON.put("columnLength", columnLength);
        JSONArray all = new JSONArray();
        all.put(nameJSON);
        all.put(typeJson);
        all.put(lengthJSON);
        expected.put("user",all);

        JSONObject actual = sqlSchemaParser.getJSON();

        Assert.assertEquals(actual.toString(),expected.toString());
    }
}