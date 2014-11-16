class AddSchemaIdToQueries < ActiveRecord::Migration
  def change
    add_column :queries, :schema_id, :integer
  end
end
