function attack(id){

var url = '/flag/attack.json?id='+id;

	$.getJSON(url,
		function(data){
			//set prestige span to prestige result
			document.getElementById("span_prestige").innerHTML = data;
		});

}
