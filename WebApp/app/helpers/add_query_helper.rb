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

end