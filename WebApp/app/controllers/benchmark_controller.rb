require 'java'
require 'json'
SQLSchemaParser = JavaUtilities.get_proxy_class("SQLSchemaParser")
QueryExecutorAll = JavaUtilities.get_proxy_class("QueryExecutorAll")
DataGenerator = JavaUtilities.get_proxy_class("DataGenerator")
GetDataValue = JavaUtilities.get_proxy_class("GetDataValue")
InsertData = JavaUtilities.get_proxy_class("InsertData")

class BenchmarkController < ApplicationController

	def schema
	end

  def column_distribution
    @schema = Schema.create(raw_schema: params[:schema])
    SQLSchemaParser.new(params[:schema])
    schema_json = JSON.parse(SQLSchemaParser.getJSON().toString())
    create_models_from_schema_json(@schema, schema_json) # Use json to create Table and Field objects
    # 1) Use raw schema to create tables    
    create_database_tables()
  end

  def queries
    update_models(params) # Update cardinality, distribution, distinct, mean, stdv, min, max
    @schema = Schema.find(params[:schema_id])
    @tables_to_fields = @schema.tables_to_fields  # Used in the view
    
    schema_distribution_hash = create_schema_distribution_hash(@schema)

    # 2) Generate and run insert statements based on DataGenerator
    #json_string = JSON.generate(schema_distribution_hash) 
    json_string = "{follower: {cardinality:3, fields:"+
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
    json_data = DataGenerator.generateJsonData(json_string) # Kristin
    insert_statements = InsertData.InsertStatementFromJSON(json_data) # Tran
    insert_statements.each { |s| QueryExecutorAll.run_all(s) }
  end

  def add_query
    @query = create_query(params)
    respond_to do |format|
      format.js
    end
  end

  def show_comparison
    schema = Schema.find(params[:schema_id])
    5.times { |i| QueryExecutorAll.run_all(schema.next_query) }
  end

end
