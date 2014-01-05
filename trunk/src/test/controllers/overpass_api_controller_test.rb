require 'test_helper'

class OverpassApiControllerTest < ActionController::TestCase
  test "should get getLocation" do
    get :getLocation
    assert_response :success
  end

  test "should get checkID" do
    get :checkID
    assert_response :success
  end

end
