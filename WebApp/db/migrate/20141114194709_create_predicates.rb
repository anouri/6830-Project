class CreatePredicates < ActiveRecord::Migration
  def change
    create_table :predicates do |t|
      t.string :prefix
      t.string :field_name
      t.string :operator

      t.timestamps
    end
  end
end
