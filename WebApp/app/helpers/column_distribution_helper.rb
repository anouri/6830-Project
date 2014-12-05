module ColumnDistributionHelper

# Example input
=begin
	{"follower"=>[{"columnName"=>["who_id", "whom_id"]}, 
								{"columnType"=>["Integer", "Integer"]}, 
								{"columnLength"=>[4, 4]}
							 ], 
	 "message"=>[{"columnName"=>["message_id", "author_id", "text", "pub_date"]}, 
	             {"columnType"=>["Integer", "Integer", "String", "Integer"]}, 
	             {"columnLength"=>[4, 4, 128, 4]},
	             {"primaryKey"=>"message_id"}
	            ], 
	 "user"=>[{"columnName"=>["user_id", "username", "email", "pw_hash"]}, 
	          {"columnType"=>["Integer", "String", "String", "String"]}, 
	          {"columnLength"=>[4, 128, 128, 128]},
	          {"primaryKey=>"user_id"}
	         ], 
	 "table"=>["user", "follower", "message"]
	}
=end

	def create_models_from_schema_json(schema, json)
		table_names = json["table"]
		table_names.each do |table_name|
			table = schema.tables.create(name: table_name)
			table_data = json[table_name]
			column_names = table_data[0]["columnName"]		 # ["who_id", "whom_id"]
			column_types = table_data[1]["columnType"]  	 # ["Integer", "Integer"]
			column_lengths = table_data[2]["columnLength"] # [4, 4]
			table.update_attribute(:primary_key, SQLSchemaParser.getPrimaryKey(table_name))
			for i in 0...column_names.length
				field = table.fields.create(category: column_types[i], name: column_names[i], length: column_lengths[i])
			end
		end
	end
	
end
