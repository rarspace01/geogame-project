class GameboardController < ApplicationController
  include GameboardHelper
  include OverpassApiHelper
  def show

	

    location = [49.10,10.10]
    locationtype = ("\"amenity\"=\"parking\"")

    @result = get_geojson(location, locationtype)

    #@geojson = getJSONfromOverpass(params[:overpass])

  end
end
