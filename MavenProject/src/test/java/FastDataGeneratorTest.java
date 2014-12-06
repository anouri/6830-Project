import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;


public class FastDataGeneratorTest {

	@Test
	public void test() {
        String sampleTables = '{' +
        		"integers: {cardinality:10, fields:"+
        			"[{category:Integer,length:4,name:ID,distribution:autoincrement,"+
        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
        			"{category:Integer,length:4,name:uniform,distribution:uniform,"+
        				"distinct:0,mean:0,stdv:0,min:1,max:10},"+
        			"{category:Integer,length:4,name:normal,distribution:normal,"+
        				"distinct:0,mean:50,stdv:10,min:0,max:0},"+
        			"{category:Integer,length:4,name:delta,distribution:delta,"+
        				"distinct:0,mean:0,stdv:0,min:1,max:1}]},"+
        		"strings: {cardinality:10, fields:"+
        			"[{category:Integer,length:4,name:ID,distribution:autoincrement,"+
        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
        			"{category:String,length:128,name:uniformtext,distribution:uniform,"+
        				"distinct:2,mean:0,stdv:0,min:1,max:10},"+
        			"{category:String,length:128,name:normaltext,distribution:normal,"+
        				"distinct:0,mean:50,stdv:10,min:0,max:0},"+
        			"{category:String,length:128,name:deltatext,distribution:delta,"+
        				"distinct:0,mean:0,stdv:0,min:1,max:1},"+
        			"]}}";
        
        FastDataGenerator fdg = new FastDataGenerator(sampleTables);

        
        String integerData = fdg.generateMoreData();
        assertNotNull(integerData);
        // test Integer data
        JSONObject intData = new JSONObject(integerData);
        JSONArray intColData = intData.getJSONObject("integers").getJSONArray("colData");
        assertEquals(intColData.length(), 10);
        // check that the integer values match their distributions properly
        for (int rowIndex = 0; rowIndex < 10; rowIndex++) {
        	JSONArray row = intColData.getJSONArray(rowIndex);
        	int autoinc = row.getInt(0);
        	assertEquals(autoinc, rowIndex);
        	int uniform = row.getInt(1);
        	assert(uniform>=1);
        	assert(uniform<=10);
        	int normal = row.getInt(2);
        	// these pass like 99.999999% of the time or whatever
        	assert(normal>=20);
        	assert(normal<=80);
        	int delta = row.getInt(3);
        	assertEquals(delta, 1);
        }
        JSONArray stringColData = intData.getJSONObject("strings").getJSONArray("colData");
        assertEquals(stringColData.length(), 0);
        
        String stringData = fdg.generateMoreData();
        assertNotNull(stringData);
        // test String data
        JSONObject strData = new JSONObject(stringData);
        JSONArray stringColData2 = strData.getJSONObject("strings").getJSONArray("colData");
        assertEquals(stringColData2.length(), 10);
        // check that the integer values match their distributions properly
        for (int rowIndex = 0; rowIndex < 10; rowIndex++) {
        	JSONArray row = stringColData2.getJSONArray(rowIndex);
        	int autoinc = row.getInt(0);
        	assertEquals(autoinc, rowIndex);
        	String uniform = row.getString(1);
        	assert(uniform.length()>=1);
        	assert(uniform.length()<=10);
        	String normal = row.getString(2);
        	// these pass like 99.999999% of the time or whatever
        	assert(normal.length()>=20);
        	assert(normal.length()<=80);
        	String delta = row.getString(3);
        	assertEquals(delta.length(), 1);
        }
        JSONArray intColData2 = strData.getJSONObject("integers").getJSONArray("colData");
        assertEquals(intColData2.length(), 0);
        
        // further calls should be null as all data has been sent
        assertNull(fdg.generateMoreData());
        assertNull(fdg.generateMoreData());
        assertNull(fdg.generateMoreData());
	}
}
