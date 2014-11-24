import org.json.JSONArray;
import org.json.JSONObject;

public class QueryGenerator {
	public String[] queries;
	public int[] percentages;
	public int numQueries;
	GetDataValue getDataValue;
	
	public QueryGenerator(String queriesAndPercentagesJSON) {
		//getDataValue = new GetDataValue();
		initialize(queriesAndPercentagesJSON);
	}
	
	private void initialize(String queriesAndPercentagesJSON) {
		JSONArray allQueries = new JSONArray(queriesAndPercentagesJSON);
		numQueries = allQueries.length();
		queries = new String[numQueries];
		percentages = new int[numQueries];
		for (int i = 0; i < numQueries; i++) {
			JSONArray queryData = allQueries.getJSONArray(i);
			queries[i] = queryData.getString(0);
			percentages[i] = queryData.getInt(1);
		}
	}
	
	public String nextQuery() {
		double p = Math.random() * 100;
		double cumulative = 0.0;
		String next = null;
		for (int i = 0; i < numQueries; i++) {
			cumulative += percentages[i];
			if (cumulative >= p) {
				next = fillQueryDetails(queries[i]);
				break;
			}
		}
		return next;
	}
	
	private String fillQueryDetails(String query) {
		String[] queryArray = query.split(" ");
		String type = queryArray[0];
		if (type.equalsIgnoreCase("SELECT")) {
			return fillSelectQuery(queryArray);
		} else if (type.equalsIgnoreCase("UPDATE")) {
			return fillUpdateQuery(queryArray);
		} else if (type.equalsIgnoreCase("INSERT")) {
			return fillInsertQuery(queryArray);
		} else if (type.equalsIgnoreCase("DELETE")) {
			return fillDeleteQuery(queryArray);
		}
		return type;
	}
	
	private String fillSelectQuery(String[] queryArray) {
		return null;
		
	}
	
	private String fillUpdateQuery(String[] queryArray) {
		return null;
		
	}
	
	private String fillInsertQuery(String[] queryArray) {
		return null;
		
	}
	
	private String fillDeleteQuery(String[] queryArray) {
		return null;
		
	}
    
	public static void main(String[] args) {
//		String input = "[['DELETE FROM message WHERE message_id = ? AND author_id = ?;',25], ['INSERT INTO user VALUES (?, ?, ?, ?);',25], ['UPDATE message SET author_id = ?, text = ? WHERE message_id = ? OR message_id = ?;',25], ['SELECT who_id, user_id FROM follower, user WHERE who_id = user_id GROUP BY who_id ORDER BY who_id DESC;',25]]";
//		QueryGenerator g = new QueryGenerator(input);
//		for (int i = 0; i < g.numQueries; i++) {
//			System.out.println(g.queries[i]);
//			System.out.println(g.percentages[i]);
//		}
		String source = "{\"message\":{\"cardinality\":4,\"fields\":[{\"category\":\"Integer\",\"length\":4,\"name\":\"message_id\",\"distribution\":\"normal\",\"distinct\":0,\"mean\":50,\"stdv\":10,\"min\":null,\"max\":null},{\"category\":\"String\",\"length\":128,\"name\":\"text\",\"distribution\":\"uniform\",\"distinct\":2,\"mean\":null,\"stdv\":null,\"min\":3,\"max\":6}]}}";
		JSONObject x = new JSONObject(source);
		JSONObject message = x.getJSONObject("message");
		JSONArray fields = message.getJSONArray("fields");
		JSONObject field1 = fields.getJSONObject(0);
		System.out.println(field1.getInt("max"));
	}
}
