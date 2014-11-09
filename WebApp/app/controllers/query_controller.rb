require 'java'
import java.lang.System

class QueryController < ApplicationController

	def home
	end

  def insert
  	#@test = System.getProperties["java.runtime.version"]
  	schema = params[:schema]
  	percent_select = params[:select]
  	percent_update = params[:update]
  	percent_insert = params[:insert]
  	percent_delete = params[:delete]
  end

  def show
  end
end
