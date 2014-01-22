// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.

$(document).ready(function(){

var location_lat = 49;
var location_lng = 10;

var geoJsonList;

function onEachFeature(feature, layer) {

    	layer.on('click', function (e) {
		alert(feature.properties.popupContent+" - "+feature.id);
		//or
		//alert(feature.properties.id);
	});
	
	/*
	var popupContent = "<p>I started out as a GeoJSON " +
			feature.geometry.type + ", but now I'm a Leaflet vector!</p>";

	if (feature.properties && feature.properties.popupContent) {
		popupContent += feature.properties.popupContent;
	}

	layer.bindPopup(popupContent);*/
}


function getLocation()
{
  if (navigator.geolocation)
    {
    navigator.geolocation.getCurrentPosition(showPosition);
    }
  else{x.innerHTML="Geolocation is not supported by this browser.";}
}

function showPosition(position)
{

	location_lat = position.coords.latitude;
	location_lng = position.coords.longitude;

	var latlng = L.latLng(position.coords.latitude, position.coords.longitude);

	var curMarker = L.marker(latlng).addTo(map);

	map.setView(latlng, 16)

	var url = "/overpass_api/getLocation.json?lat=" + location_lat + "&long=" + location_lng

	$.getJSON(url,
		function(data){
			geoJsonList = data;
			loadGeoJsonData();
		});
	
}

function loadGeoJsonData(){

		L.geoJson(geoJsonList, {

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
		}).addTo(map);



}

getLocation();

$("#map").height($(window).height()*0.8).width($(window).width());

map.invalidateSize();

/* setting the max/min zoom */

map._layersMinZoom=15;
map._layersMaxZoom=19;

});

