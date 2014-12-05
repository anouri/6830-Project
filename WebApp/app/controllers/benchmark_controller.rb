require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")
QueryExecutorAll = JavaUtilities.get_proxy_class("QueryExecutorAll")
DataGenerator = JavaUtilities.get_proxy_class("DataGenerator")
FastDataGenerator = JavaUtilities.get_proxy_class("FastDataGenerator")
GetDataValue = JavaUtilities.get_proxy_class("GetDataValue")
InsertData = JavaUtilities.get_proxy_class("InsertData")
#BarChart = JavaUtilities.get_proxy_class("BarChart")

class BenchmarkController < ApplicationController

	def schema
	end

  def column_distribution
    @schema = Schema.create(raw_schema: params[:schema])
    SQLSchemaParser.new(params[:schema])
    #raise SQLSchemaParser.getRawSchema.inspect
    schema_json = JSON.parse(SQLSchemaParser.getJSON().toString())
    create_models_from_schema_json(@schema, schema_json) # Use JSON to create Table and Field objects in Rails
    QueryExecutorAll.shema_creation_all(params[:schema]) # [1] Use raw schema to create tables in the actual databases 
  end

  def queries
    update_models(params) # Update cardinality, distribution, distinct, mean, stdv, min, max
    @schema = Schema.find(params[:schema_id])
    @tables_to_fields = @schema.tables_to_fields  # Used in the view
    @tables_to_fields_exclude_primary_key = @schema.tables_to_fields_exclude_primary_key  # Used in the view for UPDATE (can't update primary key fields)
    schema_distribution_hash = create_schema_distribution_hash(@schema)

    # schema_distribution_json = JSON.generate(schema_distribution_hash) # Pass this JSON to the Data Generator
    
    # Hard-coded JSON. Do this to prevent having to enter data into the column distribution view in the UI.
    schema_distribution_json = "{follower: {cardinality:3, fields:"+
              "[{category:Integer,length:4,name:who_id,distribution:uniform,"+
                "distinct:3,mean:0,stdv:0,min:1,max:10},"+
              "{category:Integer,length:4,name:whom_id,distribution:delta,"+
                "distinct:0,mean:0,stdv:0,min:1,max:10}]},"+
            "message: {cardinality:4, fields:"+
              "[{category:Integer,length:4,name:message_id,distribution:normal,"+
                "distinct:0,mean:50,stdv:10,min:0,max:0},"+
              "{category:String,length:128,name:text,distribution:uniform,"+
                "distinct:2,mean:0,stdv:0,min:3,max:6},"+
              "]}}"
    
    # [2] Run insert statements obtained from the Data Generator in the actual databases

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

    # data_generated_json = DataGenerator.generateJsonData(schema_distribution_json)
    # insert_statements = InsertData.InsertStatementFromJSON(data_generated_json)
    
    # @run_times = {}  # {"mongo"=>-5670856046566, "cassandra"=>8, "mysql"=>169}
    # insert_statements.each do |s| 
    #   result = QueryExecutorAll.run_all(s)
    #   result.each do |db, time|
    #     (@run_times.has_key? db) ? @run_times[db] += time : @run_times[db] = time
    #   end
    # end
    
    # bar_chart = create_bar_chart(run_times)
    # bar_chart.display()

  end

  def add_query
    @query = create_query(params)
    respond_to do |format|
      format.js
    end
  end

  def show_comparison
    schema = Schema.find(params[:schema_id])
    # 2.times { |i| QueryExecutorAll.run_all(schema.next_query) }
  end

end
