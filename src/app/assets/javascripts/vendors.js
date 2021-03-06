// map picker js code

$(document).ready(function(){

// get current location
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

function markerUpdated(e){

console.log("Marker moved");

newLat = e.target._latlng.lat;
newLng = e.target._latlng.lng;

document.getElementById('vendor_location_lat').value=newLat;
document.getElementById('vendor_location_lng').value=newLng;

//console.log(e.target._latlng);

}

// set the marker to thecurrent position 
function showPosition(position)
{
	console.log("show Position");
	location_lat = position.coords.latitude;
	location_lng = position.coords.longitude;

	var latlng = L.latLng(location_lat, location_lng);

	var curMarker = L.marker(latlng, {draggable:"true"});

	// add drag n drop listener to marker
	curMarker.on("dragend", markerUpdated);

	curMarker.addTo(map);

	map.setView(latlng, 16);

}

// called on show vendor
if(typeof vendor_map_read_only != 'undefined'){

$("#vendor_map_read_only").height($(window).height()*0.8).width($(window).width());
map.invalidateSize();

}

// called on edit
if(typeof vendor_map_edit != 'undefined'){

$("#vendor_map_edit").height($(window).height()*0.85).width($(window).width()*0.99);
map.invalidateSize();

oldLat = document.getElementById('vendor_location_lat').value;
oldLng = document.getElementById('vendor_location_lng').value;

	var latlng = L.latLng(oldLat, oldLng);

	var curMarker = L.marker(latlng, {draggable:"true"});

	curMarker.on("dragend", markerUpdated);

	curMarker.addTo(map);

	map.setView(latlng, 16);

}

// make sure the current map on the page is the vendor map not he game map as every JS asset code is called on all pages
if (typeof vendor_map != 'undefined') {
console.log("vendor map found");

$("#vendor_map").height($(window).height()*0.85).width($(window).width()*0.99);
map.invalidateSize();

getLocation();


}else{
console.log("vendor map not found");
}




});
