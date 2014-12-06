module AddQueryHelper
	
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
		query_object = create_base_query(params)
		add_select_fields(query_object, params)
		create_predicates(query_object, params)
		add_groupby_and_orderby_fields(query_object, params)
		return query_object
	end

	def create_query_update(params)
		query_object = create_base_query(params)
		add_update_fields(query_object, params)
		create_predicates(query_object, params)
		return query_object
	end

	def create_query_insert(params)
		query_object = create_base_query(params)
		return query_object
	end

	def create_query_delete(params)
		query_object = create_base_query(params)
		create_predicates(query_object, params)
		return query_object
	end


	def create_base_query(params)
		schema = Schema.find(params[:schema_id])
		query_object = schema.queries.create(percentage: params[:percent].to_i, 
																				 kind: params[:query_type],
																				 tables: params[:tables].join(","))
	end

	# Example data we want from params hash
=begin 
	"select_fields-follower"=>["whom_id"], 
	"select_fields-message"=>["message_id", "author_id", "text"], 
	"select_fields-user"=>["email", "pw_hash"]
=end

	def add_select_fields(query_object, params)
		fields = []
		params.keys.each { |k| fields += params[k] if k.include?("select_fields") }
		fields = fields.join(",")
		query_object.update_attribute(:select_fields, fields)
	end

	# Example data we want from params hash
=begin 
	"update_fields-follower"=>["whom_id"], 
	"update_fields-message"=>["message_id", "author_id", "text"], 
	"update_fields-user"=>["email", "pw_hash"]
=end
	def add_update_fields(query_object, params)
		fields = []
		params.keys.each { |k| fields += params[k] if k.include?("update_fields") }
		fields = fields.join(",")
		query_object.update_attribute(:update_fields, fields)
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

	def create_predicates(query_object, params)
		predicates_hash = create_predicates_hash(params)
		predicates_hash.each do |id, data|
			predicate = query_object.predicates.new
			data.each { |k, v| predicate[k.to_sym] = v }
			predicate.save
		end
	end

	# Example data we want from params hash
=begin
	"groupby_fields-follower"=>"who_id", "groupby_fields-message"=>"text", "groupby_fields-user"=>"", 
	"orderby_fields-follower"=>"whom_id", "orderby_fields-message"=>"author_id", "orderby_fields-user"=>"", 
	"orderby_direction"=>"DESC"
=end
	def add_groupby_and_orderby_fields(query_object, params)
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

		query_object.update_attributes(groupby_fields: groupby_fields, orderby_fields: orderby_fields, orderby_direction: orderby_direction)
	end

end