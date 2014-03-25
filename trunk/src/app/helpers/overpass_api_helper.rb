require 'open-uri'

module OverpassApiHelper

	def getDefaultTag()
	
	return "public_transport=stop_area"
	
	end

	def get_geojson(geolocation, locationtype)

		# build request url
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];(node(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];way(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];._;>;rel(#{geolocation[0]},#{geolocation[1]},#{geolocation[2]},#{geolocation[3]})[#{locationtype}];._;>;);out body;")
		
		puts(access_url) #debug

		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		#transform overpass
        virtualNodesList= transformOverpass(jsondom,locationtype)

		# result = JSON.generate(featureList)
		result = JSON.parse(buildGeoJSON(virtualNodesList).to_json)
	end

	def get_geojson_byid(id)
	
	    locationtype = getDefaultTag()
	
	
		# check which coding
		typer = ((id.to_i>>51) & 1) #relation
		typew = ((id.to_i>>50) & 1) #way

		
		
		

		if(typer == 1 && typew == 0)
		# retrieve coded id
		idr = ((id.to_i) ^ ((id.to_i)>>51)<<51)
		
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];relation(#{idr});(._;>;);out;")

		elsif (typer == 0 && typew == 1)
		
		# retrieve coded id
		idr = ((id.to_i) ^ ((id.to_i)>>50)<<50)

		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];way(#{idr});>;out;")

		else
		
		access_url = "http://overpass-api.de/api/interpreter?data="+URI.escape("[out:json];node(#{idr});out;")

		end
		
		
		
		puts(access_url) #debug

		pageresult = open(access_url).read

		jsondom = JSON.parse(pageresult)

		#transform overpass
        virtualNodesList= transformOverpass(jsondom,locationtype)

		# result = JSON.generate(featureList)
		result = JSON.parse(buildGeoJSON(virtualNodesList).to_json)
		
		result = result["features"].first
		
	end

	# merges a given list of nodes
    def mergeNodeList(nodeList)
    
			firstelement = nodeList.first

			minlat=firstelement[0]
			maxlat=firstelement[0]
			minlon=firstelement[1]
			maxlon=firstelement[1]
    
		    nodeList.each do |node|
			currentnode = node
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
    
            return [middlelat,middlelon]
    end
    
    # handles the transformation on all objects
    def transformOverpass(jsondom,locationtype)
    
		nodesList = Hash.new
		waysList = Hash.new

		virtualNodesList = Hash.new

		# get nodes & ways and save them in a hash
		jsondom["elements"].each do |element|
			# save nodes in nodesList
			if(element["type"] == "node")
			location = [element["lat"],element["lon"]]
			nodesList.store(element["id"],location)
			end
			if(element["type"] == "way")
			waysList.store(element["id"],element["nodes"])
			end
		end

		# handle each type
		
		jsondom["elements"].each do |element|
		
		if(element["tags"])
		
			# handle a relation
			if(element["type"] == "relation" && element["tags"].include?(locationtype.split("=",2)[0]) && element["tags"][locationtype.split("=",2)[0]].include?(locationtype.split("=",2)[1]))
			localNodeList = Array.new
				#on each member element do:
				element["members"].each do |member|
				
					# if node
					if(member["type"] == "node")
					localNodeList.push(nodesList[member["ref"]])
					end
					
					# if way
					if(member["type"] == "way")
						waysList[member["ref"]].each do |nodeid|
						localNodeList.push(nodesList[nodeid])
						end
					end
				
				end
				
				#merge localNodelist
				relationNode = mergeNodeList(localNodeList)
				id = (element["id"] | 1<<51) # bit shift for identification
				virtualNodesList.store(id ,relationNode)
			end
			
			# handle a way
			if(element["type"] == "way" && element["tags"].include?(locationtype.split("=",2)[0]) && element["tags"][locationtype.split("=",2)[0]].include?(locationtype.split("=",2)[1]))
				localNodeList = Array.new
				
				#on each node element do:
				element["nodes"].each do |nodeid|
						localNodeList.push(nodesList[nodeid])
				end
				
				#merge localNodelist
				relationNode = mergeNodeList(localNodeList)
				id = (element["id"] | 1<<50) # bit shift for identification
				virtualNodesList.store(id ,relationNode)
			end
			
			
			# handle a node
			if(element["type"] == "node" && element["tags"].include?(locationtype.split("=",2)[0]) && element["tags"][locationtype.split("=",2)[0]].include?(locationtype.split("=",2)[1]))
			
			virtualNodesList.store(element["id"] ,[element["lat"],element["long"]])
			
			end
		
		end
		
		end
    
    return(virtualNodesList)
    
    end
    
    def buildGeoJSON(virtualNodesList)
    
    #retrieve matched flags
		matchedFlags = Flag.where(id: virtualNodesList.keys)

		matchedNodeList = Hash.new

		matchedFlags.each do |flag|
		
		matchedNodeList.store(flag.id, flag)

		end


		featureList = Hash.new

		featureList["type"] = "FeatureCollection"
		featureList["features"] = Array.new 

		# on each node - build json dom
		virtualNodesList.each do |nodeid,location|
			
			feature = Hash.new
			feature["type"] = "Feature"
			geometry = Hash.new
			geometry["type"] = "Point"
			geometry["coordinates"] = [location[1],location[0]]
			properties = Hash.new
			properties["popupContent"] = "Test"
			properties["id"] = "#{nodeid}"

			matchedFlag = matchedNodeList[nodeid]

			if(matchedFlag != nil)

				user_id =  matchedFlag.user_id
				prestige = matchedFlag.prestige

					if(current_user != nil)

						if(current_user.id == user_id)
						
							properties["user_id"] = "owner"
						
						else
						
							properties["user_id"] = "foe"
						
						end
					
					else
					
						properties["user_id"] = "foe"
								
					end
				
				properties["prestige"] = "#{prestige}"
			
			else
			
				properties["user_id"] = "neutral"
				properties["prestige"] = 0
			
			end

			
			feature["geometry"] = geometry
			feature["properties"] = properties
			featureList["features"].push(feature)
	       end
    
    return(featureList)
    
    end

end
