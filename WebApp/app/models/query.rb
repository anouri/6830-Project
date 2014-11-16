class Query < ActiveRecord::Base
	has_many :predicates
	belongs_to :schema

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

		raw_predicates = create_raw_predicates(self.predicates)

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
				(f == orderby_fields.first) ? raw_select += "#{f}" : orderby_fields += ", #{f}"
			end
			self.orderby_direction ? raw_select += " ASC" : raw_select += " DESC"
		end

		return raw_select += ";"
	end

	def create_raw_update
		raw_update_fields = ""
		update_fields = self.update_fields.split(",")
		update_fields.each do |f|
			basic = "#{f} = ?"
			(f == update_fields.first) ? raw_update_fields += "#{basic}" : raw_update_fields += ", #{basic}"
		end

		raw_predicates = create_raw_predicates(self.predicates)

		return "UPDATE #{self.tables} SET #{raw_update_fields} #{raw_predicates};"
	end

	def create_raw_insert
		raw_insert = "INSERT INTO #{self.tables} VALUES ("
		table = Table.find_by_name(self.tables)
		num_fields = table.fields.size
		num_fields.times { |i| (i == num_fields-1) ? raw_insert += "?);" : raw_insert += "?, " }
		return raw_insert
	end

	def create_raw_delete
		raw_predicates = create_raw_predicates(self.predicates)
		return "DELETE FROM #{self.tables} #{raw_predicates};"
	end

	def create_raw_predicates(predicates)
		if predicates.size > 0
			raw_predicates = "WHERE "
			predicates.each do |p|
				basic = "#{p.field1} #{p.operator} #{p.field2}"
				(p.prefix.nil?) ? raw_predicates += basic : raw_predicates += " #{p.prefix} #{basic}"
			end
			return raw_predicates
		else
			return ""
		end
	end

end
