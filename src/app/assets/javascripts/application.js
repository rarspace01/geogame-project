// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// compiled file.
//
// Read Sprockets README (https://github.com/sstephenson/sprockets#sprockets-directives) for details
// about supported directives.
//
//= require jquery
//= require jquery.turbolinks
//= require jquery_ujs
//= require_tree .
//= require leaflet
//= require jquery.ui.all
// Loads all Bootstrap javascripts
//= require bootstrap
//= require turbolinks

// Item Dialog
function showItems(){

$( "#dialog-items" ).dialog({
      height: 140,
      modal: true
    });
}

// use a given Item
function useItem(itemid){
console.log("used item:"+itemid);

var useItemurl = "/items/useItem/"+itemid+".json";

$.getJSON(useItemurl,
		function(data){
			//set prestige span to prestige result
			$( "#dialog-items" ).dialog("close");
		});

}


