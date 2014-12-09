require 'java'

class Query < ActiveRecord::Base
	has_many :predicates
	belongs_to :schema

	# RAW QUERY METHODS: replace ? with actual GetDataValue queried from GetDataValuebase using GetGetDataValueValue.java
	def create_raw_select
		select_fields = self.select_fields.split(",")
		raw_select_fields = build_comma_separated_string("", select_fields)

		tables = self.tables.split(",")
		raw_tables = build_comma_separated_string("", tables)

		raw_predicates = create_raw_predicates(self.predicates, tables)

		raw_select = "SELECT #{raw_select_fields} FROM #{raw_tables} #{raw_predicates}"

		groupby_fields = self.groupby_fields.split(",")
		unless groupby_fields.empty?
			raw_select += " GROUP BY "
			raw_select = build_comma_separated_string(raw_select, groupby_fields)
		end

		orderby_fields = self.orderby_fields.split(",")
		unless orderby_fields.empty?
			raw_select += " ORDER BY "
			raw_select = build_comma_separated_string(raw_select, orderby_fields)
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
					if val.to_i.to_s == val 	# Integer in string quotes since GetDataValue returns strings
						basic = "#{p.field1} #{p.operator} #{val}"
					else
						basic = "#{p.field1} #{p.operator} " + "'#{val}'"	# Must adhere to raw SQL for strings
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
		select_fields = self.select_fields.split(",")
		format_select_fields = build_comma_separated_string("", select_fields)

		tables = self.tables.split(",")
		format_tables = build_comma_separated_string("", tables)

		format_predicates = create_format_predicates(self.predicates)

		format_select = "SELECT #{format_select_fields} FROM #{format_tables} #{format_predicates}"

		groupby_fields = self.groupby_fields.split(",")
		unless groupby_fields.empty?
			format_select += " GROUP BY "
			format_select = build_comma_separated_string(format_select, groupby_fields)
		end

		orderby_fields = self.orderby_fields.split(",")
		unless orderby_fields.empty?
			format_select += " ORDER BY "
			format_select = build_comma_separated_string(format_select, orderby_fields)
			self.orderby_direction ? format_select += " ASC" : format_select += " DESC"
		end

		return format_select += ";"
	end

	def create_format_update
		update_fields = self.update_fields.split(",")
		format_update_fields = build_comma_separated_string_update(update_fields)
		format_predicates = create_format_predicates(self.predicates)
		return "UPDATE #{self.tables} SET #{format_update_fields} #{format_predicates};"
	end

	def create_format_insert
		format_insert = "INSERT INTO #{self.tables} VALUES ("
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

	def build_comma_separated_string(str, list)
		list.delete ""
		if list.size == 1
			str += "#{list[0]}"
		elsif list.size > 1
			str += "#{list[0]}" 
			for i in 1...list.size
				str += ", #{list[i]}"
			end
		end
		return str
	end

	def build_comma_separated_string_update(list)
		str = ""
		if list.size == 1
			str += "#{list[0]} = ?"
		elsif list.size > 1
			str += "#{list[0]} = ?"
			for i in 1...list.size
				str += ", #{list[i]} = ?"
			end
		end
		return str
	end

end
