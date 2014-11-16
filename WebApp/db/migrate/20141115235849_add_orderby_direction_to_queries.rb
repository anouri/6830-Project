class AddOrderbyDirectionToQueries < ActiveRecord::Migration
  def change
    add_column :queries, :orderby_direction, :boolean
  end
end
