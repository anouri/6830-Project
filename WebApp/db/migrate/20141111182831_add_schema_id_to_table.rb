class AddSchemaIdToTable < ActiveRecord::Migration
  def change
    add_column :tables, :schema_id, :integer
  end
end
