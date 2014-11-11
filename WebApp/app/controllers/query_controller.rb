require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")

class QueryController < ApplicationController

	def home
	end

  def insert
    @schema = Schema.create(raw_schema: params[:schema])

    sql_schema_parser = SQLSchemaParser.new(params[:schema])
    schema_json = JSON.parse(sql_schema_parser.getJSON().toString())
    
    @tables = parse_schema_json(@schema, schema_json)
    
  end

  def show
    update_tables_and_fields(params)
    schema = Schema.find(params[:schema_id])
    schema_distribution_hash = create_schema_distribution_hash(schema)
    @json = JSON.generate(schema_distribution_hash)
  end
end
