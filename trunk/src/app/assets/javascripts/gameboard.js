// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.
//history.navigationMode = 'compatible';
$(document).ready(function(){

console.log("gamboard code executed");

var location_lat = 49;
var location_lng = 10;

var geoJsonList;
var currentGeoJson;

function onEachFeature(feature, layer) {

    	layer.on('click', function (e) {
		//alert(feature.properties.popupContent+" - "+feature.id);
		window.location = '/flag/show/'+feature.id+"?lat="+location_lat+"&lng="+location_lng;
		//or
		//alert(feature.properties.id);
	});
	
}


function getLocation()
{
  console.log("getLocation()");
  if (navigator.geolocation)
    {
    console.log("setting callback function");
    navigator.geolocation.getCurrentPosition(showPosition,null,{ maximumAge: 500, timeout: 6000, enableHighAccuracy: true});
    }
  else
  {
  console.log("no geolocation supported");
  x.innerHTML="Geolocation is not supported by this browser.";
  }
}

function showPosition(position)
{
	console.log("show Position");
	location_lat = position.coords.latitude;
	location_lng = position.coords.longitude;

	var latlng = L.latLng(position.coords.latitude, position.coords.longitude);

	var curMarker = L.marker(latlng).addTo(map);

	map.setView(latlng, 16);

	refreshData();
	
}

function refreshData(){

	currentMapBounds = map.getBounds();

	//alert(currentMapBounds.toBBoxString());

	paddedMapBounds = currentMapBounds.pad(1);

	//alert(paddedMapBounds.toBBoxString());

	//The first is minimum latitude. The second is the minimum longitude. The third is the maximum latitude. The last is the maximum longitude

	var bburl = "/overpass_api/getLocation.json?s="+paddedMapBounds.getSouth()+"&w="+paddedMapBounds.getWest()+"&n="+paddedMapBounds.getNorth()+"&e="+paddedMapBounds.getEast();

	//alert(bburl);

	//var url = "/overpass_api/getLocation.json?lat=" + location_lat + "&long=" + location_lng

	$.getJSON(bburl,
		function(data){
			geoJsonList = data;
			loadGeoJsonData();
		});

}

function loadGeoJsonData(){

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
				return L.circleMarker(latlng, {
					radius: 8,
					fillColor: "#ff7800",
					color: "#000",
					weight: 1,
					opacity: 1,
					fillOpacity: 0.8
				});
			}
		});
		currentGeoJson.addTo(map);



}

if (typeof map != 'undefined') {

console.log("running data functions");

$("#map").height($(window).height()*0.8).width($(window).width());

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

}

});

