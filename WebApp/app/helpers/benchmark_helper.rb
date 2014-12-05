module BenchmarkHelper
	# run_times = {"mongo"=>-5670856046566, "cassandra"=>8, "mysql"=>169}
	def create_bar_chart(run_times)
		# Bar chart input {"Query Type" => {"mongo"=>-5670856046566, "cassandra"=>8, "mysql"=>169}}
		run_times_java_hashmap = java.util.HashMap.new()
		run_times.each { |db, time| run_times_java_hashmap.put(db, time) }
		result_java_hashmap = java.util.HashMap.new()
		result_java_hashmap.put("Query Type", run_times_java_hashmap)
		return BarChart.new("Benchmark", result_java_hashmap)
	end

	# def benchmark(schema)
	# 	run_times = {}
	# 	2.times do |i|
	# 		result = QueryExecutorAll.run_all(schema.next_query)
	# 		result.each do |db, time|
	# 			(run_times.has_key? db) ? run_times[db] += time : run_times[db] = time
	# 		end
	# 	end
	# 	run_times
	# end

end
