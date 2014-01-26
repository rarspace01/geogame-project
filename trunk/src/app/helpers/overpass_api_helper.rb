require 'open-uri'

module OverpassApiHelper

	def get_geojson(geolocation, locationtype)

		# build request url
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];(node(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];way(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];._;>;);out body;")
		
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
			geometry["coordinates"] = [element["lon"],element["lat"]]
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

	def get_geojson_byid(id)

		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];node(#{id});out;")
		
		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		result = ""
		feature = Hash.new

		jsondom["elements"].each do |element|
			if(element["type"] == "node")
			
			feature["type"] = "Feature"
			geometry = Hash.new
			geometry["type"] = "Point"
			geometry["coordinates"] = [element["lon"],element["lat"]]
			properties = Hash.new
			properties["popupContent"] = "Test"
			feature["geometry"] = geometry
			feature["properties"] = properties
			feature["id"] = element["id"]
			end
	       end
		# result = JSON.generate(featureList)
		result = feature

	end

end
