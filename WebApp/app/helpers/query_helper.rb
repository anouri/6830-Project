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
	 "table"=>["user", "follower", "message"]}
=end

	# Expected output
=begin
	{"follower"=>[{"name"=>"who_id", "type"=>"Integer", "length"=>4}, 
								{"name"=>"whom_id", "type"=>"Integer", "length"=>4}], 
		"message"=>[{"name"=>"message_id", "type"=>"Integer", "length"=>4}, 
								{"name"=>"author_id", "type"=>"Integer", "length"=>4}, 
								{"name"=>"text", "type"=>"String", "length"=>128}, 
								{"name"=>"pub_date", "type"=>"Integer", "length"=>4}], 
		"user"=>[{"name"=>"user_id", "type"=>"Integer", "length"=>4}, 
						 {"name"=>"username", "type"=>"String", "length"=>128}, 
						 {"name"=>"email", "type"=>"String", "length"=>128}, 
						 {"name"=>"pw_hash", "type"=>"String", "length"=>128}]}
=end

	def format_schema_json(json)
		result = {}
		json.each do |table, data|
			if table != "table"
				columnName = data[0]["columnName"]		 # ["who_id", "whom_id"]
				columnType = data[1]["columnType"]  	 # ["Integer", "Integer"]
				columnLength = data[2]["columnLength"] # [4, 4]
				fields = []
				for i in 0...columnName.length
					fields << {"name" => columnName[i], "type" => columnType[i], "length" => columnLength[i]}
				end
				result[table] = fields
			end
		end
		return result
	end
end
