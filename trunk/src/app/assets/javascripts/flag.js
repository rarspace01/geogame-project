function attack(id){

var url = '/flag/attack.json?id='+id;

	$.getJSON(url,
		function(data){
			//refresh site
			location.reload();
		});

}
