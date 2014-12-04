module QueriesHelper

def update_models(params)
	params.each do |k, v|	# k = "1-cardinality", v = "1000"
		id_property = k.split("-")
		if id_property.size != 1
			id = id_property[0]
			property = id_property[1]
			if property == "cardinality"
				Table.find(id.to_i).update_attribute(:cardinality, v.to_i)
			else
				if v != ""
					value = (property == "distribution") ? v : v.to_i
					Field.find(id.to_i).update_attribute(property.to_sym, value)
				end
			end
		end
	end
end

	# Example output
=begin
	{"user"=>{"cardinality"=>1000, 
						"fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"user_id", "distribution"=>"auto_increment", "distinct"=>nil, "mean"=>nil, "stdv"=>nil, "min"=>nil, "max"=>nil}, 
											 {"category"=>"String", "length"=>128, "name"=>"username", "distribution"=>"uniform", "distinct"=>50, "mean"=>nil, "stdv"=>nil, "min"=>4, "max"=>8}, 
											 {"category"=>"String", "length"=>128, "name"=>"email", "distribution"=>"delta", "distinct"=>100, "mean"=>nil, "stdv"=>nil, "min"=>6, "max"=>18}, 
											 {"category"=>"String", "length"=>128, "name"=>"pw_hash", "distribution"=>"normal", "distinct"=>nil, "mean"=>10, "stdv"=>4, "min"=>nil, "max"=>nil}
											]
						}, 
	 "follower"=>{"cardinality"=>2000, 
	 							"fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"who_id", "distribution"=>"normal", "distinct"=>10, "mean"=>5, "stdv"=>1, "min"=>nil, "max"=>nil}, 
	 												 {"category"=>"Integer", "length"=>4, "name"=>"whom_id", "distribution"=>"delta", "distinct"=>10, "mean"=>nil, "stdv"=>nil, "min"=>1, "max"=>100}
	 												]
	 							}, 
	 "message"=>{"cardinality"=>3000, 
	 						 "fields"=>[{"category"=>"Integer", "length"=>4, "name"=>"message_id", "distribution"=>"auto_increment", "distinct"=>nil, "mean"=>nil, "stdv"=>nil, "min"=>nil, "max"=>nil}, 
	 						 						{"category"=>"Integer", "length"=>4, "name"=>"author_id", "distribution"=>"normal", "distinct"=>10, "mean"=>2, "stdv"=>1, "min"=>nil, "max"=>nil}, 
	 						 						{"category"=>"String", "length"=>128, "name"=>"text", "distribution"=>"delta", "distinct"=>20, "mean"=>nil, "stdv"=>nil, "min"=>10, "max"=>40}, 
	 						 						{"category"=>"Integer", "length"=>4, "name"=>"pub_date", "distribution"=>"uniform", "distinct"=>18, "mean"=>nil, "stdv"=>nil, "min"=>20, "max"=>60}
	 						 					 ]
	 						}
	}
=end
	def create_schema_distribution_hash(schema)
		relevant_attributes = [:category, :length, :name, :distribution, :distinct, :mean, :stdv, :min, :max]
		result = {}
		schema.tables.each do |table|
			table_data = {}
			table_data["cardinality"] = table.cardinality
			fields_array = []

			table.fields.each do |field|
				field_data = {}
				relevant_attributes.each { |attribute| field_data[attribute.to_s] = field[attribute] }
				fields_array << field_data
			end

			table_data["fields"] = fields_array
			result[table.name] = table_data
		end
		result
	end

end