class AddQueryIdToPredicates < ActiveRecord::Migration
  def change
    add_column :predicates, :query_id, :integer
  end
end
