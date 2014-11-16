class CreateQueries < ActiveRecord::Migration
  def change
    create_table :queries do |t|
      t.integer :percentage
      t.string :kind
      t.string :tables
      t.string :select_fields
      t.string :update_fields
      t.string :groupby_fields
      t.string :orderby_fields

      t.timestamps
    end
  end
end
