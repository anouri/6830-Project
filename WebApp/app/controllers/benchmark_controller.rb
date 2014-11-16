require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")

class BenchmarkController < ApplicationController

	def schema
	end

  def column_distribution
    @schema = Schema.create(raw_schema: params[:schema])
    session[:schema_id] = @schema.id
    sql_schema_parser = SQLSchemaParser.new(params[:schema])
    schema_json = JSON.parse(sql_schema_parser.getJSON().toString())
    
    @tables = create_models_from_schema_json(@schema, schema_json) # Use json to create Table and Field objects
  end

  def queries
    update_models(params) # Update cardinality, distribution, distinct, mean, stdv, min, max
    @schema = Schema.find(params[:schema_id])
    schema_distribution_hash = create_schema_distribution_hash(@schema)
    @json = JSON.generate(schema_distribution_hash) # Give this to Kristin to generate data set
    
    @tables_to_fields = @schema.tables_to_fields
  end

  def add_query
    @query = create_query(params)
    respond_to do |format|
      format.js
    end
  end

  def show_comparison
    schema = Schema.find(params[:schema_id])
    @json = JSON.generate(schema.raw_sample_queries) # Give this to the query random generator
  end

end
