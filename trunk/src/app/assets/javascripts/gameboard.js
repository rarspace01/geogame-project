// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.

$(document).ready(function(){

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

	var latlng = L.latLng(position.coords.latitude, position.coords.longitude);

	var curMarker = L.marker(latlng).addTo(map);

	map.setView(latlng, 16)
	
}

function loadGeoJsonData(){

L.geoJson(geoJsonList, {
    filter: function(feature, layer) {
        return feature.properties.show_on_map;
    }
}).addTo(map);

}

getLocation();

$("#map").height($(window).height()).width($(window).width());

map.invalidateSize();

loadGeoJsonData();

});

