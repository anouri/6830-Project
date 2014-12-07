import org.apache.commons.collections.iterators.*;

public class QueryArgs {
	public String query;
	public Arguments[] args;
	
	public QueryArgs(String query, Arguments[] args){
		this.query = query;
		this.args = args;
	}
	
	public Arguments get_args_at(int ind){
		return args[ind];
	}
	
	public Arguments[] get_args(){
		return args;
	}
	public String get_query(){
		return query;
	}
	public ArrayIterator get_iterator(){
		ArrayIterator itr = new ArrayIterator(args); 
		return itr;
	}
}
