import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

public class InsertDataTest {
    @Before
    public void setup() throws Exception {
        SQLSchemaParser schemaParser = new SQLSchemaParser("drop table follower;\n" +
                "create table follower (" +
                "who_id integer," +
                "whom_id integer);\n" +
                "drop table message;\n" +
                "create table message (" +
                "message_id integer primary key auto_increment," +
                "text text not null);");
        QueryExecutorAll.set_cassandra_keyspace("test");

        String[] shortQuery = schemaParser.getRawSchema().trim().split(";");
        for (String i : shortQuery) {
            ////////Creation for Cassandra

            QueryExecutorAll.create_table_cassandra(i);
            ////////Creation for Mysql
            QueryExecutorAll.create_table_mysql(i);
        }
        ////////Creation for Mongo
        QueryExecutorAll.create_table_mongo("drop table follower; drop table message");
    }

    @Test
    public void testInsertStatementFromJSON() throws Exception {
       String sampleTables = '{' +
                "follower: {cardinality:3, fields:"+
                "[{category:Integer,length:4,name:who_id,distribution:uniform,"+
                "distinct:3,mean:0,stdv:0,min:1,max:10},"+
                "{category:Integer,length:4,name:whom_id,distribution:delta,"+
                "distinct:0,mean:0,stdv:0,min:1,max:10}]},"+
                "message: {cardinality:4, fields:"+
                "[{category:Integer,length:4,name:message_id,distribution:normal,"+
                "distinct:0,mean:50,stdv:10,min:0,max:0},"+
//        			"{category:Integer,length:4,name:author_id,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
                "{category:String,length:128,name:text,distribution:uniform,"+
                "distinct:2,mean:0,stdv:0,min:3,max:6},"+
                "]}}";
        List<String> allInsert = InsertData.InsertStatementFromJSON(DataGenerator.generateJsonData(new JSONObject(sampleTables)));
        for (String s : allInsert) {
            QueryExecutorAll.run_all(s);
        }
        HashMap<String, Long> result =  QueryExecutorAll.run_all("SELECT * from follower");
        for (String name: result.keySet()){
            String key =name.toString();
            String value = result.get(name).toString();
            System.out.println("db is: "+ key + " run time is: " + value);
        }
    }
}