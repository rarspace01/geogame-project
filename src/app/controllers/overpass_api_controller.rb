require 'open-uri'

class OverpassApiController < ApplicationController
  def getLocation(position)

    response = open("http://overpass-api.de/api/interpreter?data=[out:json];node(around:2500.0,#(position.x),#(position.y))[\"highway\"=\"bus_stop\"];out body;")

    locations = JSON.parse(response)

  end

  def checkID
  end
end
