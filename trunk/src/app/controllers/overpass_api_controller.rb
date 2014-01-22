require 'open-uri'

class OverpassApiController < ApplicationController
  include GameboardHelper
  include OverpassApiHelper
  def getLocation

    lat = params[:lat]
    long = params[:long]

    if(lat == nil)
      lat = "49"
    end
    if(long == nil)
      lat = "10"
    end	

    location = [lat,long]
    locationtype = ("\"highway\"=\"bus_stop\"")

    @result = get_geojson(location, locationtype)

  end

  def checkID
  end
end
