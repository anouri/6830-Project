import org.apache.commons.math3.distribution;

public class DataGenerator {
	String tableDescriptorJson;
    public DataGenerator(String tableDescriptorJson) {
        this.tableDescriptorJson = tableDescriptorJson;
    }

    private Table[] parseJson(tableDescriptorJson) {
    	// get col and table objects from json
    }

    private class Table {
    	public String name;
    	public Col[] cols;
    	public int numRows;
    	public Table(String name, Col[] cols, int numRows) {
    		this.name = name;
    		this.cols = cols;
    		this.numRows = numRows;
    	}
    }

    private class Col {
    	public String name;
    	public String dataType;
    	public int byteLen;
    	public String distribution; //uniform, delta, or normal
    	public float mean;
    	public float stdv;
    	public float min;
    	public float max;
    	public int distinct;
    	public Col(String name, String dataType, int byteLen, String distribution, float mean, float stdv, float min, float max, float deltaVal, int distinct) {
    		this.name = name;
    		this.dataType = dataType;
    		this.byteLen = byteLen;
    		this.distribution = distribution;
    		this.mean = mean;
    		this.stdv = stdv;
    		this.min = min;
    		this.max = max;
    		this.deltaVal = deltaVal;
    		this.distinct = distinct;
    	}
    }

    private String generateJsonData(Table[] tables) {
    	// write {tables : [
    	for (Table t : tables) {
    		// write 'tablename': {colNames: [
    		for (Col c : t.cols) {
    			// write 'colname' + ','
    		}
    		// remove trailing comma
    		// write end colnames array and start data : ],  colData: [
    		for (int i = 0; i < t.numRows; i++) {
    			// write [
    			for (Col c : t.cols) {
    				// write 'val' + ','
    			}
    			// write ],
    		}
    		// remove trailing comma
    		//write end coldata array and end tablename obj : ]}, 
    	}
    	// remove trailing comma
    	// write end tables array and end Json : ]}
    }