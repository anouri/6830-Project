class Schema < ActiveRecord::Base
	has_many :tables
	has_many :queries

	def tables_to_fields
		result = {}
		self.tables.each do |table|
			field_names = []
			table.fields.each { |field| field_names << field.name }
			result[table.name] = field_names
		end
		result
	end

	def tables_to_fields_exclude_primary_key
		result = {}
		self.tables.each do |table|
			field_names = []
			table.fields.each { |field| field_names << field.name if field.name != table.primary_key }
			result[table.name] = field_names
		end
		result
	end

	def next_query
		p = Random.rand(100.0)
		cumulative = 0.0
		self.queries.each do |query|
			cumulative += query.percentage
			if cumulative >= p
				case query.kind
					when "SELECT" 
						return query.create_raw_select
					when "UPDATE" 
						return query.create_raw_update
					when "INSERT" 
						return query.create_raw_insert
					when "DELETE" 
						return query.create_raw_delete
					else 
						return query.create_raw_select
				end
			end
		end
	end

end
