class AddLengthToField < ActiveRecord::Migration
  def change
    add_column :fields, :length, :integer
  end
end
