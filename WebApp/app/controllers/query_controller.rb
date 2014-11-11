require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")

class QueryController < ApplicationController

	def home
	end

  def insert
  	percent_select = params[:select]
  	percent_update = params[:update]
  	percent_insert = params[:insert]
  	percent_delete = params[:delete]
    sql_schema_parser = SQLSchemaParser.new(params[:schema])
    schema_json = JSON.parse(sql_schema_parser.getJSON().toString())
    @formatted_schema_json = format_schema_json(schema_json)
  end

  def show
  end
end
