class FixTypeColumnNameInField < ActiveRecord::Migration
  def change
  	rename_column :fields, :type, :category
  end
end
