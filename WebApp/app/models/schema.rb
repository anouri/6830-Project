class Schema < ActiveRecord::Base
	has_many :tables
	has_many :queries

	def tables_to_fields
		result = {}
		self.tables.each do |table|
			fields = []
			table.fields.each do |field|
				fields << field.name
			end
			result[table.name] = fields
		end
		result
	end

	def raw_sample_queries
		result = []
		self.queries.each do |query|
			raw_query = ""
			if query.kind == "SELECT"
				raw_query = query.create_raw_select
			elsif query.kind == "UPDATE"
				raw_query = query.create_raw_update
			elsif query.kind == "INSERT"
				raw_query = query.create_raw_insert
			elsif query.kind == "DELETE"
				raw_query = query.create_raw_delete
			end
			result << [raw_query, query.percentage]
		end
		return result
	end

end
