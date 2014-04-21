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

	# bounding box
    swne = [s,w,n,e]

    #locationtype = ("\"highway\"=\"bus_stop\"")

    if(params[:tag] == nil)

    puts "falling back to default tag"

    # set default tag
    locationtype = getDefaultTag()
    else
    locationtype = tag
    end

    @result = get_geojson(swne, locationtype)

  end

end
