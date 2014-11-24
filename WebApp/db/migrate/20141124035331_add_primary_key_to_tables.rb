class AddPrimaryKeyToTables < ActiveRecord::Migration
  def change
    add_column :tables, :primary_key, :string
  end
end
