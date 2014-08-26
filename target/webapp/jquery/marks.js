var Marks =  dejavu.Class.declare({
	
	initialize : function() {
		
	},
	
	redirectSubject : function(elem) {
		var id = $(elem).attr('id').substring(5);
		document.location = "http://" + location.host + "/teacher/marks/"+ id;
	}
	
	
});