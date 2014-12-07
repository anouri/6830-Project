require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")
QueryExecutorAll = JavaUtilities.get_proxy_class("QueryExecutorAll")
FastDataGenerator = JavaUtilities.get_proxy_class("FastDataGenerator")
GetDataValue = JavaUtilities.get_proxy_class("GetDataValue")
InsertData = JavaUtilities.get_proxy_class("InsertData")
#BarChart = JavaUtilities.get_proxy_class("BarChart")

class BenchmarkController < ApplicationController

	def schema
	end

  def column_distribution
    raw_schema = params[:schema].gsub(/\s+/, " ").strip
    schema_id = cookies[:schema_id]
    if schema_id.nil?
      @schema = Schema.create(raw_schema: raw_schema)
      cookies[:schema_id] = @schema.id
      SQLSchemaParser.new(raw_schema)
      schema_json = JSON.parse(SQLSchemaParser.getJSON().toString())
      create_models_from_schema_json(@schema, schema_json) # Use JSON to create Table and Field objects in Rails
      QueryExecutorAll.shema_creation_all(raw_schema) # [1] Use raw schema to create tables in the actual databases 
    else
      @schema = Schema.find(schema_id)
    end
  end

  def queries
    # @schema = Schema.find(params[:schema_id])
    @schema = Schema.find(cookies[:schema_id])
    update_models(params) # Update cardinality, distribution, distinct, mean, stdv, min, max
    @tables_to_fields = @schema.tables_to_fields
    @tables_to_fields_select = @schema.tables_to_fields_select  # Used in the view
    @tables_to_fields_update = @schema.tables_to_fields_update  # Used in the view for UPDATE (can't update primary key fields)

    schema_distribution_hash = create_schema_distribution_hash(@schema)

    # schema_distribution_json = JSON.generate(schema_distribution_hash) # Pass this JSON to the Data Generator
    
    # Hard-coded JSON. Do this to prevent having to enter data into the column distribution view in the UI.
    schema_distribution_json = '{ 
  "follower": {
    "cardinality": 3, 
    "fields": [{
      "category": "Integer", 
      "length": 4, 
      "name": "who_id", 
      "distribution": "autoincrement",
      "distinct": nil, 
      "mean": nil, 
      "stdv": nil, 
      "min": nil, 
      "max": nil
    }, {
      "category": "Integer", 
      "length": 4, 
      "name": "whom_id", 
      "distribution": "delta", 
      "distinct": nil, 
      "mean": nil, 
      "stdv": nil, 
      "min": 1, 
      "max": 10
    }]
  }, 
  "message": {
    "cardinality": 4, 
    "fields": [{
      "category": "Integer", 
      "length": 4, 
      "name": "message_id", 
      "distribution": "autoincrement", 
      "distinct": nil, 
      "mean": nil, 
      "stdv": nil, 
      "min": nil, 
      "max": nil
    }, {
      "category": "String", 
      "length": 128, 
      "name": "text", 
      "distribution": "uniform",
      "distinct": 2, 
      "mean": nil, 
      "stdv": nil, 
      "min": 3, 
      "max": 6
    }]
  }
}'  
    @schema.queries.destroy_all
    
    # [2] Run insert statements obtained from the Data Generator in the actual databases
    if cookies[:inserted_data].nil?
      fast_data_generator = FastDataGenerator.new(schema_distribution_json)
      @run_times = {}  # {"mongo"=>13, "cassandra"=>6, "mysql"=>5}
      data = nil
      while !(data = fast_data_generator.generateMoreData()).nil? do
        insert_statements = InsertData.InsertStatementFromJSON(data)
        insert_statements.each do |s| 
          result = QueryExecutorAll.run_all(s)
          result.each do |db, time|
            (@run_times.has_key? db) ? @run_times[db] += time : @run_times[db] = time
          end
        end
      end
      cookies[:inserted_data] = @run_times
    else
      @run_times = cookies[:inserted_data]
    end
  end

  def add_query
    @query = create_query(params)
    respond_to do |format|
      format.js
    end
  end

  def show_comparison
    # schema = Schema.find(params[:schema_id])
    schema = Schema.find(cookies[:schema_id])
    @run_times = run_benchmark(schema)
    # bar_chart = create_bar_chart(@run_times)
    # bar_chart.display()
  end

end
