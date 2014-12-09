module BenchmarkHelper
	
	# run_times = {"mongo"=>13, "cassandra"=>6, "mysql"=>5}
	def create_bar_chart(run_times)
		# Bar chart input {"Query Type" => {"mongo"=>13, "cassandra"=>6, "mysql"=>5}}
		run_times_java_hashmap = java.util.HashMap.new()
		run_times.each { |db, time| run_times_java_hashmap.put(db, time) }
		result_java_hashmap = java.util.HashMap.new()
		result_java_hashmap.put("My Workload", run_times_java_hashmap)
		return BarChart.new("Benchmark", result_java_hashmap)
	end

	def run_benchmark(schema)
		# QueryExecutorAll.set_cassandra_keyspace("test");
		run_times = {}
		10.times do |i|
			result = QueryExecutorAll.run_all(schema.next_query)
			result.each do |db, time|
				(run_times.has_key? db) ? run_times[db] += time : run_times[db] = time
			end
		end
		run_times
	end

end
