// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.

var OSMData = function() {
	var Basemap = {
		//tileurl: 'http://www.openstreetmap.org/?lat={x}&lon={y}&zoom={z}&layers=M',
		tileurl: 'http://otile1.mqcdn.com/tiles/1.0.0/osm/{z}/{x}/{y}.png',
		options: {attribution: "bla",
			maxZoom: "18"}
	}

	function init_map(mapdom,lat,lon,zoom) {
alert('' + mapdom + ':' + lat + ':' + lon + ':' + zoom);
		var map = L.map(mapdom).setView([lat,lon],zoom);
alert(map);
		L.tileLayer(Basemap.tileurl, Basemap.options).addTo(map);
	}

	function add_geojson() {}

	function connect(mapclass) {
		var llm = $(mapclass || '.leafletmap');

		llm.each(function() {
			var map = this;
			var jqmap = $(map);
			init_map(this,
				jqmap.data('lat') || 49, jqmap.data('lon') || 10,
				jqmap.data('zoom') || 1);			
		});
	}

	function myspecialconnect() {
		
	}

	return {
		basemap : Basemap,
		init_map : init_map,
		addgeojson : add_geojson,
		connect : connect
	}
}();

$(document).on('ready page:change', OSMData.connect);


