class AddMeanToFields < ActiveRecord::Migration
  def change
    add_column :fields, :mean, :integer
  end
end
