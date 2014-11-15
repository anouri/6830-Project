module QueryHelper

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

	def parse_schema_json(schema, json)
		tables = []
		json.each do |table_name, data|
			if table_name != "table"
				table = schema.tables.create(name: table_name)
				columnName = data[0]["columnName"]		 # ["who_id", "whom_id"]
				columnType = data[1]["columnType"]  	 # ["Integer", "Integer"]
				columnLength = data[2]["columnLength"] # [4, 4]
				for i in 0...columnName.length
					field = table.fields.new
					field.category = columnType[i]
					field.name = columnName[i]
					field.length = columnLength[i]
					field.save
				end
				tables << table
			end
		end
		tables
	end

	def update_tables_and_fields(params)
		params.each do |k, v|	# k = "1-cardinality", v = "1000"
			name = k.split("-")
			if name.size != 1
				id = name[0]
				prop = name[1]
				if prop == "cardinality"
					Table.find(id.to_i).update_attribute(:cardinality, v.to_i)
				else
					value = (prop == "distribution") ? v.to_s : v.to_i
					field = Field.find(id.to_i).update_attribute(prop.to_sym, value)
				end
			end
		end
	end

	# Example output
=begin
	{"follower":{"cardinality":100,
							 "fields":[{"category":"Integer","length":4,"name":"who_id","distribution":"uniform","distinct":1,"min":1,"max":1},
												 {"category":"Integer","length":4,"name":"whom_id","distribution":"uniform","distinct":1,"min":1,"max":1}]},
		"message":{"cardinality":200,
							 "fields":[{"category":"Integer","length":4,"name":"message_id","distribution":"uniform","distinct":1,"min":1,"max":1},
							 					 {"category":"Integer","length":4,"name":"author_id","distribution":"uniform","distinct":1,"min":1,"max":1},
							 					 {"category":"String","length":128,"name":"text","distribution":"uniform","distinct":1,"min":null,"max":null},
							 					 {"category":"Integer","length":4,"name":"pub_date","distribution":"uniform","distinct":1,"min":1,"max":1}]},
		"user":{"cardinality":300,
						"fields":[{"category":"Integer","length":4,"name":"user_id","distribution":"uniform","distinct":1,"min":1,"max":1},
											{"category":"String","length":128,"name":"username","distribution":"uniform","distinct":1,"min":null,"max":null},
											{"category":"String","length":128,"name":"email","distribution":"uniform","distinct":1,"min":null,"max":null},
											{"category":"String","length":128,"name":"pw_hash","distribution":"uniform","distinct":1,"min":null,"max":null}]}
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

end
