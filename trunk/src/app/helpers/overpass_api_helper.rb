require 'open-uri'

module OverpassApiHelper

	def get_geojson(geolocation, locationtype)

		# build request url
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];(node(around:5000.0,49.90383,10.86108)[\"amenity\"=\"parking\"];way(around:5000.0,49.90383,10.86108)[\"amenity\"=\"parking\"];._;>;);out body;")
		
		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		result = ""
		
		featureList = Hash.new

		featureList["type"] = "FeatureCollection"
		featureList["features"] = Array.new 

		jsondom["elements"].each do |element|
			if(element["type"] == "node")
			feature = Hash.new
			feature["type"] = "Feature"
			geometry = Hash.new
			geometry["type"] = "Point"
			geometry["coordinates"] = [element["lat"],element["lon"]]
			properties = Hash.new
			properties["popupContent"] = "Test"
			feature["geometry"] = geometry
			feature["properties"] = properties
			feature["id"] = element["id"]
			featureList["features"].push(feature)
			end
	       end
		# result = JSON.generate(featureList)
		result = JSON.parse(featureList.to_json)
	end

end
