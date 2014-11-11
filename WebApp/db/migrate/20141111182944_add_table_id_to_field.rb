class AddTableIdToField < ActiveRecord::Migration
  def change
    add_column :fields, :table_id, :integer
  end
end
