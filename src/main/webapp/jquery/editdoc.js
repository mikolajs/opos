

var Document =  dejavu.Class.declare({
	
	initialize : function(){
		$('#docEdit').html($('#docContent').val());
	},
	deleteDoc: function() 
	{
		return confirm("Jesteś pewien, że chcesz skasować cały dokument?");
	},
    saveDoc : function() {
    	$('#docContent').val($('#docEdit').html());
    	return true;
    }
	
});