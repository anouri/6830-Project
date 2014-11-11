class CreateSchemas < ActiveRecord::Migration
  def change
    create_table :schemas do |t|
      t.text :raw_schema

      t.timestamps
    end
  end
end
