class CreateTables < ActiveRecord::Migration
  def change
    create_table :tables do |t|
      t.integer :cardinality

      t.timestamps
    end
  end
end
