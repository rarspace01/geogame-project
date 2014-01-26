class FlagController < ApplicationController
  #include GameboardHelper
  require 'rgeo'
  include OverpassApiHelper
  def show

	#Flag
	cid = params[:id]
	currentFlag = Flag.find_by_id(cid)
	if(currentFlag != nil)
	currentFlag = currentFlag.first
	end

	currentgeojson = get_geojson_byid(cid)

	@id = params[:id]
	@flaglat = currentgeojson["geometry"]["coordinates"][1]
	@flaglng = currentgeojson["geometry"]["coordinates"][0]
	@userlat = params[:lat]
	@userlng = params[:lng]

	# Geographic factory that projects to a world mercator projection.
	# Note the ellps and datum set to WGS84.
	factory = ::RGeo::Geographic.simple_mercator_factory()

	userlocation = factory.point(@userlng, @userlat)
	flaglocation = factory.point(@flaglng, @flaglat)

	@distance = userlocation.distance(flaglocation)


  end
end
