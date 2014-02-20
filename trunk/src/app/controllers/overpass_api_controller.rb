require 'open-uri'

class OverpassApiController < ApplicationController
  include GameboardHelper
  include OverpassApiHelper
  def getLocation

    s = params[:s]
    w = params[:w]
    n = params[:n]
    e = params[:e]
    swne = [s,w,n,e]

    #locationtype = ("\"highway\"=\"bus_stop\"")

    locationtype = "shop"

    @result = get_geojson(swne, locationtype)

  end

  def checkID
  end
end
