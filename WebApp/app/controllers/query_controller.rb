require 'java'
import java.lang.System

class QueryController < ApplicationController
  def insert
  	@test = System.getProperties["java.runtime.version"]
  end

  def show
  end
end
