require 'test_helper'

class BenchmarkControllerTest < ActionController::TestCase
  test "should get insert" do
    get :insert
    assert_response :success
  end

  test "should get show" do
    get :show
    assert_response :success
  end

end
