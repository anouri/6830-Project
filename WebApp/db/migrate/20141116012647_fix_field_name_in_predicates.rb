class FixFieldNameInPredicates < ActiveRecord::Migration
  def change
  	rename_column :predicates, :field_name, :field1
  	add_column :predicates, :field2, :string
  end
end
