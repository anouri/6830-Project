import org.apache.commons.collections.iterators.ArrayIterator;


public class Arguments {
	Data[] data;
	
	public Arguments(Data[] data){
		this.data = data;
	}
	
	public Data get_data_at(int index){
		return data[index];
	}
	
	public Data[] get_data(){
		return data;
	}
	
	public ArrayIterator get_iterator(){
		ArrayIterator itr = new ArrayIterator(data); 
		return itr;
	}
	
}
