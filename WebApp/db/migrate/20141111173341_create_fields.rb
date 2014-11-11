class CreateFields < ActiveRecord::Migration
  def change
    create_table :fields do |t|
      t.string :type
      t.string :name
      t.string :distribution
      t.integer :distinct
      t.integer :min
      t.integer :max

      t.timestamps
    end
  end
end
