class Table < ActiveRecord::Base
	belongs_to :schema
	has_many :fields
end
