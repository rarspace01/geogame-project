require 'open-uri'

module OverpassApiHelper

	def get_geojson(geolocation, locationtype)

		# build request url
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];(node(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];way(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];._;>;);out body;")
		
		puts(access_url)

		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		result = ""
		
		featureList = Hash.new

		featureList["type"] = "FeatureCollection"
		featureList["features"] = Array.new 

		nodesList = Hash.new

		nodesBlacklist = Array.new

		# get nodes and save them in a hash
		jsondom["elements"].each do |element|
			# save nodes in nodesList
			if(element["type"] == "node")
			location = [element["lat"],element["lon"]]
			nodesList.store(element["id"],location)
			end
	      	end

		# get ways and bulid nodes
		jsondom["elements"].each do |element|
			# save nodes in nodesList
			if(element["type"] == "way")

			id = element["id"].to_i
			type = 1

			id = (id | type<<61)

			#id = 123456789
			#type = 1
			#coded = (id | type<<61)
			#idr = (coded ^ ((coded>>61)<<61))
			#typer = (coded>>61)

			nodes = element["nodes"]
			
			#minlat, maxlat, minlon, maxlon
			
			firstelement = nodesList[nodes.first]

			minlat=firstelement[0]
			maxlat=firstelement[0]
			minlon=firstelement[1]
			maxlon=firstelement[1]

			#binding.pry

			nodes.each do |node|
				currentnode = nodesList[node]
				#binding.pry
				if(currentnode != nil)
					if(currentnode[0]<minlat) then minlat=currentnode[0] end
					if(currentnode[0]>maxlat) then maxlat=currentnode[0] end
					if(currentnode[1]<minlon) then minlon=currentnode[1] end
					if(currentnode[1]>maxlon) then maxlon=currentnode[1] end
				end
			end

			# calc middle

			middlelat = (maxlat+minlat)/2
			middlelon = (maxlon+minlon)/2

			#round to 7 decimals
			middlelat = middlelat.round(7)
			middlelon = middlelon.round(7)

			# blacklist nodes from List
			nodes.each do |node|
				nodesBlacklist.push(node)
			end

			# create node with way id
			nodesList.store(id, [middlelat,middlelon])

			end
	      	end						

		# on each node
		nodesList.each do |nodeid,location|
                        if(!nodesBlacklist.include?(nodeid))
			feature = Hash.new
			feature["type"] = "Feature"
			geometry = Hash.new
			geometry["type"] = "Point"
			geometry["coordinates"] = [location[1],location[0]]
			properties = Hash.new
			properties["popupContent"] = "Test"
			properties["id"] = "#{nodeid}"
			feature["geometry"] = geometry
			feature["properties"] = properties
			featureList["features"].push(feature)
			end
	       end
		# bulid json dom

		# result = JSON.generate(featureList)
		result = JSON.parse(featureList.to_json)
	end

	def get_geojson_byid(id)

		# check if is coded way
		idr = ((id.to_i) ^ ((id.to_i)>>61)<<61)
		typer = (id.to_i>>61)

		if(typer == 0)
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];node(#{idr});out;")

		elsif (typer == 1)

		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];way(#{idr});>;out;")

		end
		
		puts(access_url)

		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		result = ""
		feature = Hash.new

		# build virtual or real node

		if(jsondom["elements"].length >1)

			firstelement = jsondom["elements"].first

			minlat=firstelement["lat"]
			maxlat=firstelement["lat"]
			minlon=firstelement["lon"]
			maxlon=firstelement["lon"]


			jsondom["elements"].each do |element|

					if(element != nil)
					if(element["lat"]<minlat) then minlat=element["lat"] end
					if(element["lat"]>maxlat) then maxlat=element["lat"] end
					if(element["lon"]<minlon) then minlon=element["lon"] end
					if(element["lon"]>maxlon) then maxlon=element["lon"] end
				end			

			end

			# calc middle
			middlelat = (maxlat+minlat)/2
			middlelon = (maxlon+minlon)/2

			#round to 7 decimals
			middlelat = middlelat.round(7)
			middlelon = middlelon.round(7)
			# bulid json

			feature["type"] = "Feature"
			geometry = Hash.new
			geometry["type"] = "Point"
			geometry["coordinates"] = [middlelon,middlelat]
			properties = Hash.new
			properties["popupContent"] = "Test"
			feature["geometry"] = geometry
			feature["properties"] = properties
			feature["id"] = id


		else	

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
				feature["id"] = id
				end
       			end
			

		end


		# result = JSON.generate(featureList)
		result = feature

	end

end
