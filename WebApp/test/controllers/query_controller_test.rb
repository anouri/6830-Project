require 'test_helper'

class QueryControllerTest < ActionController::TestCase
  test "should get insert" do
    get :insert
    assert_response :success
  end

  test "should get show" do
    get :show
    assert_response :success
  end

end
