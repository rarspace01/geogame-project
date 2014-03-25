require 'open-uri'

class OverpassApiController < ApplicationController
  include GameboardHelper
  include OverpassApiHelper
  def getLocation

    s = params[:s]
    w = params[:w]
    n = params[:n]
    e = params[:e]

    tag = params[:tag]

    swne = [s,w,n,e]

    #locationtype = ("\"highway\"=\"bus_stop\"")

    if(params[:tag] == nil)

    puts "falling back to default tag"

    #locationtype = "building=garage"
    locationtype = "highway=bus_stop"
    else
    locationtype = tag
    end

    @result = get_geojson(swne, locationtype)

  end

  def checkID
  end
end
