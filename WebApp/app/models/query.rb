require 'java'

class Query < ActiveRecord::Base
	has_many :predicates
	belongs_to :schema

	# RAW QUERY METHODS: replace ? with actual GetDataValue queried from GetDataValuebase using GetGetDataValueValue.java
	def create_raw_select
		raw_select_fields = ""
		select_fields = self.select_fields.split(",")
		select_fields.each do |f|
			(f == select_fields.first) ? raw_select_fields += "#{f}" : raw_select_fields += ", #{f}"
		end

		raw_tables = ""
		tables = self.tables.split(",")
		tables.each do |t|
			(t == tables.first) ? raw_tables += "#{t}" : raw_tables += ", #{t}"
		end

		raw_predicates = create_raw_predicates(self.predicates, tables)

		raw_select = "SELECT #{raw_select_fields} FROM #{raw_tables} #{raw_predicates}"

		groupby_fields = self.groupby_fields.split(",")
		unless groupby_fields.empty?
			raw_select += " GROUP BY "
			groupby_fields.each do |f|
				(f == groupby_fields.first) ? raw_select += "#{f}" : raw_select += ", #{f}"
			end
		end

		orderby_fields = self.orderby_fields.split(",")
		unless orderby_fields.empty?
			raw_select += " ORDER BY "
			orderby_fields.each do |f|
				(f == orderby_fields.first) ? raw_select += "#{f}" : raw_select += ", #{f}"
			end
			self.orderby_direction ? raw_select += " ASC" : raw_select += " DESC"
		end

		return raw_select += ";"
	end

	def create_raw_update
		raw_update_fields = ""
		update_fields = self.update_fields.split(",")
		update_fields.each do |f|
			val = GetDataValue.getValueFromColumnTable(f, self.tables)
			if val.is_a? String
				basic = "#{f} = " + "'#{val}'"	# Must adhere to raw SQL for strings
			else
				basic = "#{f} = #{val}"
			end
			(f == update_fields.first) ? raw_update_fields += "#{basic}" : raw_update_fields += ", #{basic}"
		end

		raw_predicates = create_raw_predicates(self.predicates, [self.tables])

		return "UPDATE #{self.tables} SET #{raw_update_fields} #{raw_predicates};"
	end

	def create_raw_insert
		result = GetDataValue.getRowFromTable(self.tables)
		column_names = result[0]
		values = result[1]
		return "INSERT INTO #{self.tables} #{column_names} VALUES #{values};"		
	end

	def create_raw_delete
		raw_predicates = create_raw_predicates(self.predicates, [self.tables])
		return "DELETE FROM #{self.tables} #{raw_predicates};"
	end

	def create_raw_predicates(predicates, tables)
		if predicates.size > 0
			raw_predicates = "WHERE "
			predicates.each do |p|
				if p.field2 == "?"
					val = getPredicateVal(p.field1, tables)
					if val.is_a? String
						basic = "#{p.field1} #{p.operator} " + "'#{val}'"	# Must adhere to raw SQL for strings
					else
						basic = "#{p.field1} #{p.operator} #{val}"
					end
				else
					basic = "#{p.field1} #{p.operator} #{p.field2}"
				end
				(p.prefix.nil?) ? raw_predicates += basic : raw_predicates += " #{p.prefix} #{basic}"
			end
			return raw_predicates

		else
			return ""
		end
	end

	def getPredicateVal(field, tables)
		if tables.size == 1 # We know that the field belongs to the table (UPDATE, DELETE)
			return GetDataValue.getValueFromColumnTable(field, tables[0])
		else	# We must find the table that the field belongs to (SELECT)
			# If field = "name" and tables = ["employee", "building"] and both tables have the field "name"
			# the user has to specify either Employee.name or Building.name. Currently not supporting this.
			# Assume unique field names across tables.
			field_obj = Field.find_by_name(field)
			tables.each do |t_name|
				t_obj = Table.find_by_name(t_name)
				if field_obj.table_id == t_obj.id
					return GetDataValue.getValueFromColumnTable(field, t_name)
				end
			end
		end
	end

	# FORMAT QUERY METHODS: insert ? where GetDataValue is to be inputted
	def create_format_select
		format_select_fields = ""
		select_fields = self.select_fields.split(",")
		select_fields.each do |f|
			(f == select_fields.first) ? format_select_fields += "#{f}" : format_select_fields += ", #{f}"
		end

		format_tables = ""
		tables = self.tables.split(",")
		tables.each do |t|
			(t == tables.first) ? format_tables += "#{t}" : format_tables += ", #{t}"
		end
		
		format_predicates = create_format_predicates(self.predicates)

		format_select = "SELECT #{format_select_fields} FROM #{format_tables} #{format_predicates}"

		groupby_fields = self.groupby_fields.split(",")
		unless groupby_fields.empty?
			format_select += " GROUP BY "
			groupby_fields.each do |f|
				(f == groupby_fields.first) ? format_select += "#{f}" : format_select += ", #{f}"
			end
		end

		orderby_fields = self.orderby_fields.split(",")
		unless orderby_fields.empty?
			format_select += " ORDER BY "
			orderby_fields.each do |f|
				(f == orderby_fields.first) ? format_select += "#{f}" : format_select += ", #{f}"
			end
			self.orderby_direction ? format_select += " ASC" : format_select += " DESC"
		end

		return format_select += ";"
	end

	def create_format_update
		format_update_fields = ""
		update_fields = self.update_fields.split(",")
		update_fields.each do |f|
			basic = "#{f} = ?"
			(f == update_fields.first) ? format_update_fields += "#{basic}" : format_update_fields += ", #{basic}"
		end

		format_predicates = create_format_predicates(self.predicates)

		return "UPDATE #{self.tables} SET #{format_update_fields} #{format_predicates};"
	end

	def create_format_insert
		format_insert = "INSERT INTO #{self.tables} VALUES ( "
		table = Table.find_by_name(self.tables)
		num_fields = table.fields.size
		num_fields.times { |i| (i == num_fields-1) ? format_insert += "?);" : format_insert += "?, " }
		return format_insert
	end

	def create_format_delete
		format_predicates = create_format_predicates(self.predicates)
		return "DELETE FROM #{self.tables} #{format_predicates};"
	end

	def create_format_predicates(predicates)
		if predicates.size > 0
			format_predicates = "WHERE "
			predicates.each do |p|
				basic = "#{p.field1} #{p.operator} #{p.field2}"
				(p.prefix.nil?) ? format_predicates += basic : format_predicates += " #{p.prefix} #{basic}"
			end
			return format_predicates
		else
			return ""
		end
	end

end
