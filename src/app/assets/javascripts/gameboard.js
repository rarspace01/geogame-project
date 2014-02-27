// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.
//history.navigationMode = 'compatible';

var debugvar;

var location_lat = 49;
var location_lng = 10;

function buy(vendorid, itemid){

var buyurl = '/vendors/buyItem/'+vendorid+'/'+itemid+'.json';

	$.getJSON(buyurl,
		function(data){
			//set prestige span to prestige result
			refreshData();
		});

}

function attack(id){

var url = '/flag/attack.json?id='+id+"&lat="+location_lat+"&lng="+location_lng;

	$.getJSON(url,
		function(data){
			//set prestige span to prestige result
			document.getElementById("flaginfoprestige").innerHTML = data;
			refreshData();
		});

}

$(document).ready(function(){

console.log("gamboard code executed");

var curMarker;
var lastUpdate=0;

var geoJsonList;
var geoJsonVendorList;;
var currentGeoJson;
var currentGeoJsonVendor;

function onEachFeature(feature, layer) {

		layer.bindPopup("Prestige: <span id='flaginfoprestige'>"+feature.properties.prestige+"</span><br/><a href='#' onClick='attack("+feature.properties.id+");' data-no-turbolink>Attack</a>");

    	layer.on('click', function (e) {
		//alert(feature.properties.id);

		//debugvar = feature

                //alert(feature.properties);

		//window.location = '/flag/show/'+feature.properties.id+"?lat="+location_lat+"&lng="+location_lng;
		//or
		//alert(feature.properties.id);
	});
	
}

function onEachFeatureVendor(feature, layer) {

	layer.bindPopup(feature.properties.popupContent+"<br/><div id='vendoritems'></div>");


    	layer.on('click', function (e) {
	// load vendoritems onto vendoritem div
        var vendorurl = '/vendors/'+feature.id+'.json';
	$.getJSON(vendorurl,
	function(data){
                console.log("got json:"+data);
                vendoritems = data['items'];
		//build html here
                innerHtml = "<ul>";
		$.each(vendoritems, function(index, item) {
                //innerHtml = innerHtml + "<li>"+item['name']+" - Price: "+item['price']+" - <a href='/vendors/buyItem/"+feature.id+"/"+item['id']+"'>Buy</a></li>";
		innerHtml = innerHtml + "<li>"+item['name']+" - Price: "+item['price']+" - <a href='#' onClick='buy("+feature.id+","+item['id']+");' data-no-turbolink>Buy</a></li>";
		console.log("ID: "+item['id']+" "+item['name']);
                
		});
                innerHtml = innerHtml + "</ul>";
		//replace div here
		document.getElementById("vendoritems").innerHTML = innerHtml;
	});

	});

}


function getLocation()
{
  console.log("getLocation()");
  if (navigator.geolocation)
    {
    console.log("setting callback function");
    //navigator.geolocation.getCurrentPosition(showPosition,null,{ maximumAge: 500, timeout: 6000, enableHighAccuracy: true});
    // enable permanent watching
    navigator.geolocation.watchPosition(showPosition,null, { maximumAge: 500, timeout: 6000, enableHighAccuracy: true});
    }
  else
  {
  console.log("no geolocation supported");
  x.innerHTML="Geolocation is not supported by this browser.";
  }
}

function showPosition(position)
{
	var minWaitSec = 10;
	if(lastUpdate + (minWaitSec*1000)< new Date().getTime())
	{
	
		console.log("show Position");
		location_lat = position.coords.latitude;
		location_lng = position.coords.longitude;

		var latlng = L.latLng(position.coords.latitude, position.coords.longitude);

		if(lastUpdate != 0)
		{
		map.removeLayer(curMarker);
		}
		
		curMarker = L.marker(latlng, {clickable: false}).addTo(map);

		map.setView(latlng, 16);

		refreshData();
	    lastUpdate = new Date().getTime();
	}	
	
}

function refreshData(){

	currentMapBounds = map.getBounds();

	//alert(currentMapBounds.toBBoxString());

	paddedMapBounds = currentMapBounds.pad(1);

	//alert(paddedMapBounds.toBBoxString());

	//The first is minimum latitude. The second is the minimum longitude. The third is the maximum latitude. The last is the maximum longitude

	var bburl = "/overpass_api/getLocation.json?s="+paddedMapBounds.getSouth()+"&w="+paddedMapBounds.getWest()+"&n="+paddedMapBounds.getNorth()+"&e="+paddedMapBounds.getEast();
        var bbvendorurl = "/vendors/getVendors.json?s="+paddedMapBounds.getSouth()+"&w="+paddedMapBounds.getWest()+"&n="+paddedMapBounds.getNorth()+"&e="+paddedMapBounds.getEast();

	//alert(bburl);

	//var url = "/overpass_api/getLocation.json?lat=" + location_lat + "&long=" + location_lng


	//load flag data
	$.getJSON(bburl,
		function(data){
			geoJsonList = data;
			loadGeoJsonData();
		});
        //load vendor data
	$.getJSON(bbvendorurl,
	function(data){
		geoJsonVendorList = data;
		loadGeoJsonVendorData();
	});

}

function loadGeoJsonData(){
		console.log("loading Flag Data");
		
		if(currentGeoJson != null)
		{
		map.removeLayer(currentGeoJson);
		}

		currentGeoJson = L.geoJson(geoJsonList, {

			style: function (feature) {
				return feature.properties && feature.properties.style;
			},

			onEachFeature: onEachFeature,

			pointToLayer: function (feature, latlng) {
				
				neutralMarker = L.circleMarker(latlng, {
					radius: 8,
					fillColor: "#696969",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8,
					riseOnHover: true
				});
						
				ownerMarker = L.circleMarker(latlng, {
					radius: 8,
					fillColor: "#00FF00",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8,
					riseOnHover: true
				});
				
				foeMarker = L.circleMarker(latlng, {
					radius: 8,
					fillColor: "#FF0000",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8,
					riseOnHover: true
				});
				
				switch(feature.properties.user_id){
					case 'owner': return ownerMarker;
					case 'foe': return foeMarker;
					case 'neutral': return neutralMarker;
				}
			}
		});
		currentGeoJson.addTo(map);
}

function loadGeoJsonVendorData(){
		console.log("loading Vendor Data");
		if(currentGeoJsonVendor != null)
		{
		map.removeLayer(currentGeoJsonVendor);
		}

		currentGeoJsonVendor = L.geoJson(geoJsonVendorList, {

			style: function (feature) {
				return feature.properties && feature.properties.style;
			},

			onEachFeature: onEachFeatureVendor,

			pointToLayer: function (feature, latlng) {
				return L.circleMarker(latlng, {
					radius: 8,
					fillColor: "#FF00FF",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8,
					riseOnHover: true
				});
			}
		});
		currentGeoJsonVendor.addTo(map);
}


if (typeof game_map != 'undefined') {
console.log("game map found");

$("#game_map").height($(window).height()*0.85).width($(window).width()*0.99);

map.invalidateSize();

getLocation();
console.log("got location");
/* setting the max/min zoom */

map._layersMinZoom=15;
map._layersMaxZoom=19;
map.on('moveend',function(){
//alert('test');
//check here if new bounds exeed the "safe zone"
refreshData();
});

}else{
console.log("no game map found");
}

});

