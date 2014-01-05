require 'open-uri'

module OverpassApiHelper

	def get_geojson(geolocation, locationtype)

		# build request url
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];(node(around:5000.0,49.90383,10.86108)[\"amenity\"=\"parking\"];way(around:5000.0,49.90383,10.86108)[\"amenity\"=\"parking\"];._;>;);out body;")
		
		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		result = ""

		jsondom["elements"].each do |element|
			if(element["type"] == "node")
			  location = [element["lat"],element["lon"]]
			  result = result + location.inspect	
			end
	       end
		result
	end

end
