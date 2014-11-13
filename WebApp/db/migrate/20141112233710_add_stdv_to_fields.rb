class AddStdvToFields < ActiveRecord::Migration
  def change
    add_column :fields, :stdv, :integer
  end
end
