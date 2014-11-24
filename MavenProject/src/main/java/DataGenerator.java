import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class DataGenerator {

	private static Iterator<Integer> generateIntegerData(JSONObject colInfo, int count) {
		// extract data and ensure validity
		String distribution = colInfo.getString("distribution");
		if (distribution == null || !(distribution.equalsIgnoreCase("delta") || 
				distribution.equalsIgnoreCase("uniform") || distribution.equalsIgnoreCase("normal"))) {
			throw new RuntimeException("distribution must be uniform, normal, or delta");
		}
		int max = colInfo.getInt("max");
		int min = colInfo.getInt("min");
		double mean = colInfo.getDouble("mean");
		double stdv = colInfo.getDouble("stdv"); 
		
		ArrayList<Integer> data = new ArrayList<Integer>();
		
		// determine distribution and sample to get distinct values
		if (distribution.equalsIgnoreCase("delta")) {
			// all data are the same value
			int value = (new Random()).nextInt(max-min) + min;
			for (int i = 0; i < count; i++) {
				data.add(value);
			}
			return data.iterator();
		} else if (distribution.equalsIgnoreCase("uniform")) {
			Random generator = new Random(); 
			for (int i = 0; i < count; i++) {
				int nextValue = generator.nextInt(max-min) + min;
				data.add(nextValue);
			}
			return data.iterator();
		} else { //distribution: normal
			NormalDistribution dist = new NormalDistribution(mean, stdv);
			for (int i = 0; i < count; i++) {
				data.add((int) dist.sample());
			}
			return data.iterator();
		}
	}
	
	private static Iterator<String> generateStringData(JSONObject colInfo, int count) {
		// extract data and ensure validity
		String distribution = colInfo.getString("distribution");
		if (distribution == null || !(distribution.equalsIgnoreCase("delta") || 
				distribution.equalsIgnoreCase("uniform") || distribution.equalsIgnoreCase("normal"))) {
			throw new RuntimeException("distribution must be uniform, normal, or delta");
		}
		int distinct = colInfo.getInt("distinct");
		if (distinct > count) distinct = count;
		// if distinct = 0, assume they know nothing about distinctness of values
		int max = colInfo.getInt("max");
		int min = colInfo.getInt("min");
		double mean = colInfo.getDouble("mean");
		double stdv = colInfo.getDouble("stdv"); 
		
		HashSet<String> distinctVals = new HashSet<String>();
		ArrayList<String> data = new ArrayList<String>();
		Random generator = new Random();
		
		// determine distribution
		if (distribution.equalsIgnoreCase("delta")) {
			// all words have the same length
			int wordLen = (new Random()).nextInt(max-min) + min;
			if (distinct > 0) {
				// get the distinct values to use
				while (distinctVals.size() < distinct) {
					distinctVals.add(RandomStringUtils.randomAlphabetic(wordLen));
				}
				if (distinct < count) {
					// sample from the distinct values to get the complete data
					ArrayList<String> distinctList = new ArrayList<String>();
					distinctList.addAll(distinctVals);
					for (int i = 0; i < count; i++) {
						data.add(distinctList.get(generator.nextInt(distinct)));
					}
					return data.iterator();
				} else { 
					// distinct == count
					return distinctVals.iterator();
				}
			} else {
				// we know nothing about distinctness, so just sample
				for (int i = 0; i < count; i++) {
					data.add(RandomStringUtils.randomAlphabetic(wordLen));
				}
				return data.iterator();
			}	
		} else if (distribution.equalsIgnoreCase("uniform")) {			
			if (distinct > 0) {
				// get the distinct values to use
				while (distinctVals.size() < distinct) {
					int wordLen = (new Random()).nextInt(max-min) + min;
					distinctVals.add(RandomStringUtils.randomAlphabetic(wordLen));	
				}
				if (distinct < count) {
					// sample from the distinct values to get the complete data
					ArrayList<String> distinctList = new ArrayList<String>();
					distinctList.addAll(distinctVals);
					for (int i = 0; i < count; i++) {
						data.add(distinctList.get(generator.nextInt(distinct)));
					}
					return data.iterator();
				} else { 
					// distinct == count
					return distinctVals.iterator();
				}
			} else {
				// we know nothing about distinctness, so just sample
				for (int i = 0; i < count; i++) {
					int wordLen = (new Random()).nextInt(max-min) + min;
					data.add(RandomStringUtils.randomAlphabetic(wordLen));
				}
				return data.iterator();
			}
		} else { //distribution: normal
			NormalDistribution dist = new NormalDistribution(mean, stdv);
			if (distinct > 0) {
				while (distinctVals.size() < distinct) {
					int wordLen = (int) dist.sample();
					distinctVals.add(RandomStringUtils.randomAlphabetic(wordLen));
				}
				if (distinct < count) {
					// sample from the distinct values to get the complete data
					ArrayList<String> distinctList = new ArrayList<String>();
					distinctList.addAll(distinctVals);
					for (int i = 0; i < count; i++) {
						data.add(distinctList.get(generator.nextInt(distinct)));
					}
					return data.iterator();
				} else {
					// distinct == count
					return distinctVals.iterator();
				}
			} else {
				// we know nothing about distinctness, so just sample
				for (int i = 0; i < count; i++) {
					int wordLen = (int) dist.sample();
					data.add(RandomStringUtils.randomAlphabetic(wordLen));
				}
				return data.iterator();
			}	
		}
	}

    public static String generateJsonData(String jsonString) {
    	JSONObject json = new JSONObject(jsonString);
    	JSONObject outputJson = new JSONObject();
    	
    	// get info for each table
    	for (String tableName : JSONObject.getNames(json)) {
    		JSONObject table = json.getJSONObject(tableName);
    		int cardinality = table.getInt("cardinality");
    		JSONArray fields = table.getJSONArray("fields");
    		
    		// generate data for each of the fields in this table
    		ArrayList<Iterator> dataIterators = new ArrayList<Iterator>();
    		ArrayList<String> fieldNames = new ArrayList<String>();
    		for (int i = 0; i < fields.length(); i++) {
    			JSONObject col = fields.getJSONObject(i);
    			fieldNames.add(col.getString("name"));
    			if (col.getString("category").equalsIgnoreCase("Integer")) {
    				dataIterators.add(generateIntegerData(col, cardinality));
    			} else if (col.getString("category").equalsIgnoreCase("String")) {
    				dataIterators.add(generateStringData(col, cardinality));
    			} else {
    				throw new RuntimeException("field categories must be Integer or String");
    			}
    		}
    		
    		// now put that data into json output grammar
    		JSONObject tabledata = new JSONObject();
    		tabledata.put("colNames", fieldNames);
    		ArrayList<ArrayList> data = new ArrayList<ArrayList>();
    		for (int i = 0; i < cardinality; i++) {
    			ArrayList l = new ArrayList();
    			for (Iterator it : dataIterators) {
    				l.add(it.next());
    			}
    			data.add(l);
    		}
    		tabledata.put("colData", data);
    		outputJson.put(tableName, tabledata);
    	}
    	return outputJson.toString();
    }
    
    // TODO: will implement this if compression is necessary
    private static byte[] compress(String output) {
    	// string -> gzip information:
    	// http://stackoverflow.com/questions/16351668/compression-and-decompression-of-string-data-in-java
    	return null;
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
//        			"{category:Integer,length:4,name:author_id,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
        			"{category:String,length:128,name:text,distribution:uniform,"+
        				"distinct:2,mean:0,stdv:0,min:3,max:6},"+
        			"]}}";
//        			"{category:Integer,length:4,name:pub_date,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0}]},"+
//        		"user:{cardinality:0,fields:"+
//        			"[{category:Integer,length:4,name:user_id,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
//        			"{category:String,length:128,name:username,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
//        			"{category:String,length:128,name:email,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0},"+
//        			"{category:String,length:128,name:pw_hash,distribution:uniform,"+
//        				"distinct:0,mean:0,stdv:0,min:0,max:0}]}};";
        //JSONObject json = new JSONObject(sampleTables);
        System.out.println(generateJsonData(sampleTables));
    }
}