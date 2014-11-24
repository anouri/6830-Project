import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.json.JSONArray;

// TODO: add auto-incrementing distribution
// TODO: update README with new documentation
// TODO: write unit tests

public class FastDataGenerator {
	private JSONObject outputTemplate = null;
	private ArrayList<Table> tables = new ArrayList<Table>();
	
	private class Table {
		private ArrayList<Row> rows;
		private int rowsLeft;
		private String name;
		
		private Table(ArrayList<Row> rows, int cardinality, String name) {
			this.rows = rows;
			this.rowsLeft = cardinality;
			this.name = name;
		}
		
		private ArrayList<ArrayList> generateData() {
			int generateThisMany = 100;
			boolean done = false;
			if (rowsLeft < 100) {
				generateThisMany = rowsLeft;
			}
			ArrayList<ArrayList> data = new ArrayList<ArrayList>();
    		for (int i = 0; i < generateThisMany; i++) {
    			ArrayList l = new ArrayList();
    			for (Row r : rows) {
    				Object sample = r.sample();
    				l.add(sample);
    			}
    			data.add(l);
    		}
    		rowsLeft -= generateThisMany;
    		return data;
		}
	}
	
	private class Row {
		private int dist; // 0=delta, 1=uniform, 2=normal
		private int max;
		private int min;
		private double mean;
		private double stdv;
		private String category;
		private String name;
		private NormalDistribution normal;
		private Random generator;
		private Object delta;
				
		private Row (JSONObject colInfo) {
			this.name = colInfo.getString("name");
			this.category = colInfo.getString("category");
			if (category == null || !(category.equalsIgnoreCase("integer") || 
					category.equalsIgnoreCase("string"))) {
				throw new RuntimeException("category must be integer or string");
			}
			String distribution = colInfo.getString("distribution");
			if (distribution == null || !(distribution.equalsIgnoreCase("delta") || 
					distribution.equalsIgnoreCase("uniform") || distribution.equalsIgnoreCase("normal"))) {
				throw new RuntimeException("distribution must be uniform, normal, or delta");
			}
			if (distribution.equalsIgnoreCase("normal")) {
				this.dist = 2;
				this.mean = colInfo.getDouble("mean");
				this.stdv = colInfo.getDouble("stdv");
				this.normal = new NormalDistribution(mean, stdv);
			} else {
				this.max = colInfo.getInt("max");
				this.min = colInfo.getInt("min");
				this.generator = new Random();
				if (distribution.equalsIgnoreCase("delta")) {
					this.dist = 0;
					if (category.equalsIgnoreCase("integer")) {
						this.delta = generator.nextInt(max-min) + min;
					} else {
						int wordLen = generator.nextInt(max-min) + min;
						this.delta = RandomStringUtils.randomAlphabetic(wordLen);
					}
				} else {
					this.dist = 1;
				}
			} 
		}
				
		private Object sample() {
			if (this.category.equalsIgnoreCase("integer")) {
				switch (dist) {
				case 0: return delta;
				case 1: return generator.nextInt(max-min) + min;
				case 2: return (int) normal.sample();
				}
			} else {
				switch (dist) {
				case 0: return delta;
				case 1: return RandomStringUtils.randomAlphabetic(generator.nextInt(max-min) + min);
				case 2: return RandomStringUtils.randomAlphabetic((int) normal.sample());
				}
			}
			System.out.println("why are you here for row: "+this.name);
			return null;
		}
	}
	
	public FastDataGenerator(String jsonString) {
		JSONObject json = new JSONObject(jsonString);
    	JSONObject outputJson = new JSONObject();
    	// get info for each table
    	for (String tableName : JSONObject.getNames(json)) {
    		JSONObject table = json.getJSONObject(tableName);
    		int cardinality = table.getInt("cardinality");
    		JSONArray fields = table.getJSONArray("fields");
    		
    		// generate data for each of the fields in this table
    		ArrayList<Row> rows = new ArrayList<Row>();
    		ArrayList<String> fieldNames = new ArrayList<String>();
    		for (int i = 0; i < fields.length(); i++) {
    			JSONObject col = fields.getJSONObject(i);
    			fieldNames.add(col.getString("name"));
    			rows.add(this.new Row(col));
    		}
    		this.tables.add(this.new Table(rows, cardinality, tableName));
    		
    		JSONObject tabledata = new JSONObject();
    		tabledata.put("colNames", fieldNames);
    		tabledata.put("colData", new ArrayList());
    		outputJson.put(tableName, tabledata);
    	}
    	this.outputTemplate = outputJson;
	}
	
    private String generateMoreData() {
    	JSONObject outputJson = new JSONObject(this.outputTemplate.toString()); //deep clone
    	ArrayList data = null;
    	for (Table t : tables) {
    		if (t.rowsLeft>0) {
    			data = t.generateData();
    			// put the data into json[tablename][coldata]
    	    	//JSONObject person =  jsonArray.getJSONObject(0).getJSONObject("person");
    	    	//person.put("name", "Sammie");
    	    	JSONObject tableData = outputJson.getJSONObject(t.name);
    	    	tableData.put("colData", data);
    			break;
    		}
    	}
    	if (data == null) return null;
    	return outputJson.toString();
    }
    
    public static void main(String[] args) {
        String sampleTables = '{' +
        		"follower: {cardinality:3, fields:"+
        			"[{category:Integer,length:4,name:who_id,distribution:uniform,"+
        				"distinct:3,mean:0,stdv:0,min:1,max:10},"+
        			"{category:Integer,length:4,name:whom_id,distribution:delta,"+
        				"distinct:0,mean:0,stdv:0,min:1,max:10}]},"+
        		"message: {cardinality:4, fields:"+
        			"[{category:Integer,length:4,name:message_id,distribution:normal,"+
        				"distinct:0,mean:50,stdv:10,min:0,max:0},"+
        			"{category:String,length:128,name:text,distribution:uniform,"+
        				"distinct:2,mean:0,stdv:0,min:3,max:6},"+
        			"]}}";
        
        FastDataGenerator fdg = new FastDataGenerator(sampleTables);
        System.out.println(fdg.generateMoreData());
        System.out.println(fdg.generateMoreData());
        System.out.println(fdg.generateMoreData());
        System.out.println(fdg.generateMoreData());
        System.out.println(fdg.generateMoreData());
        System.out.println(fdg.generateMoreData());
        
    }
}