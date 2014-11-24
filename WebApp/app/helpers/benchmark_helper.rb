module BenchmarkHelper

	# Example input
=begin
	{"follower"=>[{"columnName"=>["who_id", "whom_id"]}, 
								{"columnType"=>["Integer", "Integer"]}, 
								{"columnLength"=>[4, 4]}], 
	 "message"=>[{"columnName"=>["message_id", "author_id", "text", "pub_date"]}, 
	             {"columnType"=>["Integer", "Integer", "String", "Integer"]}, 
	             {"columnLength"=>[4, 4, 128, 4]}], 
	 "user"=>[{"columnName"=>["user_id", "username", "email", "pw_hash"]}, 
	          {"columnType"=>["Integer", "String", "String", "String"]}, 
	          {"columnLength"=>[4, 128, 128, 128]}], 
	 "table"=>["user", "follower", "message"]
	}
=end

	def create_models_from_schema_json(schema, json)
		table_names = json["table"]
		table_names.each do |table_name|
			data = json[table_name]
			table = schema.tables.create(name: table_name)
			columnName = data[0]["columnName"]		 # ["who_id", "whom_id"]
			columnType = data[1]["columnType"]  	 # ["Integer", "Integer"]
			columnLength = data[2]["columnLength"] # [4, 4]
			for i in 0...columnName.length
				field = table.fields.create(category: columnType[i], name: columnName[i], length: columnLength[i])
			end
		end
	end

	def update_models(params)
		params.each do |k, v|	# k = "1-cardinality", v = "1000"
			name = k.split("-")
			if name.size != 1
				id = name[0]
				prop = name[1]
				if prop == "cardinality"
					Table.find(id.to_i).update_attribute(:cardinality, v.to_i)
				else
					if v != ""
						value = (prop == "distribution") ? v : v.to_i
						field = Field.find(id.to_i).update_attribute(prop.to_sym, value)
					end
				end
			end
		end
	end

	# Example output
=begin
	{"user"=>{"cardinality"=>1000, 
						"fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"user_id", "distribution"=>"normal", "distinct"=>10, "mean"=>5, "stdv"=>1, "min"=>nil, "max"=>nil}, 
											 {"category"=>"String", "length"=>128, "name"=>"username", "distribution"=>"uniform", "distinct"=>50, "mean"=>nil, "stdv"=>nil, "min"=>4, "max"=>8}, 
											 {"category"=>"String", "length"=>128, "name"=>"email", "distribution"=>"delta", "distinct"=>100, "mean"=>nil, "stdv"=>nil, "min"=>6, "max"=>18}, 
											 {"category"=>"String", "length"=>128, "name"=>"pw_hash", "distribution"=>"normal", "distinct"=>nil, "mean"=>10, "stdv"=>4, "min"=>nil, "max"=>nil}]}, 
	 "follower"=>{"cardinality"=>2000, 
	 							"fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"who_id", "distribution"=>"normal", "distinct"=>10, "mean"=>5, "stdv"=>1, "min"=>nil, "max"=>nil}, 
	 												 {"category"=>"Integer", "length"=>4, "name"=>"whom_id", "distribution"=>"delta", "distinct"=>10, "mean"=>nil, "stdv"=>nil, "min"=>1, "max"=>100}]}, 
	 "message"=>{"cardinality"=>3000, 
	 						 "fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"message_id", "distribution"=>"uniform", "distinct"=>10, "mean"=>nil, "stdv"=>nil, "min"=>1, "max"=>100}, 
	 						 						{"category"=>"Integer", "length"=>4, "name"=>"author_id", "distribution"=>"normal", "distinct"=>10, "mean"=>2, "stdv"=>1, "min"=>nil, "max"=>nil}, 
	 						 						{"category"=>"String", "length"=>128, "name"=>"text", "distribution"=>"delta", "distinct"=>20, "mean"=>nil, "stdv"=>nil, "min"=>10, "max"=>40}, 
	 						 						{"category"=>"Integer", "length"=>4, "name"=>"pub_date", "distribution"=>"uniform", "distinct"=>18, "mean"=>nil, "stdv"=>nil, "min"=>20, "max"=>60}]}
	}
=end
	def create_schema_distribution_hash(schema)
		relevant_attributes = [:category, :length, :name, :distribution, :distinct, :mean, :stdv, :min, :max]
		result = {}
		schema.tables.each do |table|
			data = {}
			data["cardinality"] = table.cardinality
			fields_array = []
			table.fields.each do |field|
				field_hash = {}
				relevant_attributes.each do |att|
					field_hash[att.to_s] = field[att]
				end
				fields_array << field_hash
			end
			data["fields"] = fields_array
			result[table.name] = data
		end
		result
	end

	def create_query(params)
		query_type = params[:query_type]
		if query_type == "SELECT"
			create_query_select(params)
		elsif query_type == "UPDATE"
			create_query_update(params)
		elsif query_type == "INSERT"
			create_query_insert(params)
		elsif query_type == "DELETE"
			create_query_delete(params)
		end
	end

	def create_query_select(params)
		query = create_base_query(params)
		add_select_or_update_fields(query, params)
		create_predicates(query, params)
		add_groupby_and_orderby_fields(query, params)
		return query
	end

	def create_query_update(params)
		query = create_base_query(params)
		add_select_or_update_fields(query, params)
		create_predicates(query, params)
		return query
	end

	def create_query_insert(params)
		query = create_base_query(params)
		return query
	end

	def create_query_delete(params)
		query = create_base_query(params)
		create_predicates(query, params)
		return query
	end


	def create_base_query(params)
		schema = Schema.find(params[:schema_id])
		query = schema.queries.create(percentage: params[:percent].to_i, 
																	kind: params[:query_type],
																	tables: params[:tables].join(","))
	end

	# Example data we want from params hash
=begin 
	"select_fields-follower"=>["whom_id"], 
	"select_fields-message"=>["message_id", "author_id", "text"], 
	"select_fields-user"=>["email", "pw_hash"]
=end

	def add_select_or_update_fields(query, params)
		fields = []
		params.keys.each do |k|
			if k.include?("select_fields")
				fields += params[k]
			end
		end
		fields = fields.join(",")
		if query.kind == "SELECT"
			query.update_attribute(:select_fields, fields)
		elsif query.kind == "UPDATE"
			query.update_attribute(:update_fields, fields)
		end
	end

	# Example params hash
=begin
	{"utf8"=>"✓", "authenticity_token"=>"9KHR5Vn4PurSdpwbe/sElUqH+iX/IL8j2ixYwZ/ly0o=", "tables_to_fields"=>"{\"follower\":[\"who_id\",\"whom_id\"],\"message\":[\"message_id\",\"author_id\",\"text\",\"pub_date\"],\"user\":[\"user_id\",\"username\",\"email\",\"pw_hash\"]}", 
	 "percent"=>"40", "query_type"=>"DELETE", "tables"=>["message"], 
	 "0-field1"=>"message_id", "0-operator"=>"=", "0-field2"=>"?", 
	 "1-prefix"=>"AND", "1-field1"=>"author_id", "1-operator"=>"=", "1-field2"=>"?", 
	 "groupby_fields-follower"=>"", "groupby_fields-message"=>"", "groupby_fields-user"=>"", 
	 "orderby_fields-follower"=>"", "orderby_fields-message"=>"", "orderby_fields-user"=>"", "asc_desc"=>"ASC", 
	 "commit"=>"ADD QUERY", "controller"=>"benchmark", "action"=>"add_query"} 
=end

	# Example predicates_hash
=begin
	{"0"=>{"field1"=>"message_id", "operator"=>"=", "field2"=>"?"}, 
	 "1"=>{"prefix"=>"AND", "field1"=>"author_id", "operator"=>"=", "field2"=>"?"}} 
=end
	def create_predicates_hash(params)
		predicates_hash = {}
		params.keys.each do |k|
			if k.include?("prefix") || k.include?("field1") || k.include?("operator") || k.include?("field2")
				split = k.split("-")	# ["1", "field1"]
				pid = split[0]
				ptype = split[1]
				if predicates_hash.include?(pid)
					predicates_hash[pid][ptype] = params[k]
				else
					predicates_hash[pid] = {ptype => params[k]}
				end
			end
		end
		return predicates_hash
	end 

	def create_predicates(query, params)
		predicates_hash = create_predicates_hash(params)
		predicates_hash.each do |id, data|
			predicate = query.predicates.new
			data.each do |k, v|
				predicate[k.to_sym] = v
			end
			predicate.save
		end
	end

	# Example data we want from params hash
=begin
	"groupby_fields-follower"=>"who_id", "groupby_fields-message"=>"text", "groupby_fields-user"=>"", 
	"orderby_fields-follower"=>"whom_id", "orderby_fields-message"=>"author_id", "orderby_fields-user"=>"", 
	"orderby_direction"=>"DESC"
=end
	def add_groupby_and_orderby_fields(query, params)
		groupby_fields = []
		orderby_fields = []
		orderby_direction = 1 # Default 1 = ASC, 0 = DESC

		params.keys.each do |k|
			if k.include?("groupby_fields")
				groupby_fields << params[k]
			elsif k.include?("orderby_fields")
				orderby_fields << params[k]
			elsif k == "orderby_direction"
				orderby_direction = params[k]
			end
		end

		groupby_fields = groupby_fields.join(",")
		orderby_fields = orderby_fields.join(",")

		query.update_attributes(groupby_fields: groupby_fields, orderby_fields: orderby_fields, orderby_direction: orderby_direction)
	end

	def create_database_tables()
		#QueryExecutorAll.set_cassandra_keyspace("test");
    shortQuery = SQLSchemaParser.getRawSchema.split(";")
    shortQuery.each do |query|
      # Creation for Cassandra
      #QueryExecutorAll.create_table_cassandra(query)
      # Creation for MySQL
      QueryExecutorAll.create_table_mysql(query)
    end
	end
end
