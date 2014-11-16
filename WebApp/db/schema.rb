# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20141116012647) do

  create_table "fields", force: true do |t|
    t.string   "category"
    t.string   "name"
    t.string   "distribution"
    t.integer  "distinct"
    t.integer  "min"
    t.integer  "max"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "length"
    t.integer  "table_id"
    t.integer  "mean"
    t.integer  "stdv"
  end

  create_table "predicates", force: true do |t|
    t.string   "prefix"
    t.string   "field1"
    t.string   "operator"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "query_id"
    t.string   "field2"
  end

  create_table "queries", force: true do |t|
    t.integer  "percentage"
    t.string   "kind"
    t.string   "tables"
    t.string   "select_fields"
    t.string   "update_fields"
    t.string   "groupby_fields"
    t.string   "orderby_fields"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "schema_id"
    t.boolean  "orderby_direction"
  end

  create_table "schemas", force: true do |t|
    t.text     "raw_schema"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "tables", force: true do |t|
    t.integer  "cardinality"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "schema_id"
    t.string   "name"
  end

end
