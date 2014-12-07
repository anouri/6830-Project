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
    # @schema = Schema.find(1)
    raw_schema = params[:schema].gsub(/\s+/, " ").strip
    @schema = Schema.create(raw_schema: raw_schema)
    SQLSchemaParser.new(raw_schema)
    schema_json = JSON.parse(SQLSchemaParser.getJSON().toString())
    create_models_from_schema_json(@schema, schema_json) # Use JSON to create Table and Field objects in Rails
    # QueryExecutorAll.dropTables(["patients", "chartitems", "labitems", "meditems", "icustayevents", "chartevents", "labevents", "meddurations", "medevents"])
    QueryExecutorAll.shema_creation_all(raw_schema) # [1] Use raw schema to create tables in the actual databases 
  end

  def queries
    # @schema = Schema.find(1)
    @schema = Schema.find(params[:schema_id])
    update_models(params) # Update cardinality, distribution, distinct, mean, stdv, min, max
    @tables_to_fields = @schema.tables_to_fields
    @tables_to_fields_select = @schema.tables_to_fields_select  # Used in the view
    @tables_to_fields_update = @schema.tables_to_fields_update  # Used in the view for UPDATE (can't update primary key fields)

    schema_distribution_hash = create_schema_distribution_hash(@schema)

    # schema_distribution_json = JSON.generate(schema_distribution_hash) # Pass this JSON to the Data Generator
    
    # Hard-coded JSON. Do this to prevent having to enter data into the column distribution view in the UI.
    schema_distribution_json = '{patients:{cardinality:100,fields:[{category:Integer,length:4,name:subject_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:sex,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1},{category:Integer,length:4,name:dob,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:dod,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:hospital_expire_flg,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:1}]},icustayevents:{cardinality:1000,fields:[{category:Integer,length:4,name:icustay_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:intime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:outtime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000}]},chartevents:{cardinality:300,fields:[{category:Integer,length:4,name:chartevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:999},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:elemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100},{category:String,length:4,name:resultstatus,distribution:normal,distinct:null,mean:7,stdv:3,min:null,max:null},{category:String,length:4,name:annotation,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},chartitems:{cardinality:100,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:label,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:description,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},labitems:{cardinality:50,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:test_name,distribution:normal,distinct:null,mean:15,stdv:5,min:null,max:null},{category:String,length:4,name:fluid,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:10},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:description,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},labevents:{cardinality:200,fields:[{category:Integer,length:4,name:labevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:999},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:49},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:String,length:4,name:labvalue,distribution:uniform,distinct:null,mean:null,stdv:null,min:10,max:50}]},medevents:{cardinality:250,fields:[{category:Integer,length:4,name:medevent_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:999},{category:Integer,length:4,name:subject_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:medduration,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:charttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:volume,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:10000},{category:Integer,length:4,name:dose,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:10000},{category:String,length:4,name:solution,distribution:uniform,distinct:null,mean:null,stdv:null,min:10,max:20},{category:String,length:4,name:route,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3},{category:String,length:4,name:stopped,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:site,distribution:normal,distinct:null,mean:10,stdv:3,min:null,max:null},{category:String,length:4,name:annotation,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:200}]},meditems:{cardinality:100,fields:[{category:Integer,length:4,name:itemid,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:String,length:4,name:label,distribution:uniform,distinct:null,mean:null,stdv:null,min:5,max:15},{category:String,length:4,name:category,distribution:delta,distinct:null,mean:null,stdv:null,min:3,max:3}]},meddurations:{cardinality:100,fields:[{category:Integer,length:4,name:meddurations_id,distribution:autoincrement,distinct:null,mean:null,stdv:null,min:null,max:null},{category:Integer,length:4,name:icustay_id,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:999},{category:Integer,length:4,name:itemid,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:99},{category:Integer,length:4,name:medevent,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:249},{category:Integer,length:4,name:starttime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:endtime,distribution:uniform,distinct:null,mean:null,stdv:null,min:-2000000,max:1500000},{category:Integer,length:4,name:duration,distribution:uniform,distinct:null,mean:null,stdv:null,min:0,max:100000}]}}'

    # [2] Run insert statements obtained from the Data Generator in the actual databases
    # fast_data_generator = FastDataGenerator.new(schema_distribution_json)
    # @run_times = {}  # {"mongo"=>13, "cassandra"=>6, "mysql"=>5}
    # data = nil
    # while !(data = fast_data_generator.generateMoreData()).nil? do
    #   insert_statements = InsertData.InsertStatementFromJSON(data)
    #   insert_statements.each do |s| 
    #     result = QueryExecutorAll.run_all(s)
    #     result.each do |db, time|
    #       (@run_times.has_key? db) ? @run_times[db] += time : @run_times[db] = time
    #     end
    #   end
    # end
  end

  def add_query
    @query = create_query(params)
    respond_to do |format|
      format.js
    end
  end

  def show_comparison
    # schema = Schema.find(1)
    schema = Schema.find(params[:schema_id])
    @run_times = run_benchmark(schema)
    # bar_chart = create_bar_chart(@run_times)
    # bar_chart.display()
  end

end
