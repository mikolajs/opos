


var EditQuiz =  dejavu.Class.declare({
	
	initialize : function() {
		 $( "ul.dropselected" ).sortable({
			 connectWith: "ul"
			 });
		 $( "ul.dropall" ).sortable({
			 connectWith: "ul"	
			 });
	},

	prepareData : function() {
	    $('#questionsQuiz').val(this._getQuizArrayData());
	    console.log($('#questionsQuiz').val());
	    var title = $('#titleQuiz').val();
	    if($('#questionsQuiz').val().trim().length < 26 || title.length < 3) {
	        alert("Brak pytań lub zbyt krótki (min 3 litery) tytuł");
	        return false;
	    }
	    return true;
	},

	removeDuplicate : function() {
	    //console.log("REMOVE DUPLICATE");
		var ids = this._getQuizArrayId();
		$('ul.dropall').children('li').each(function(){
           for(i in ids) {
            //console.log(ids[i] + " ==? " + this.id);
            if(ids[i] == this.id) $(this).remove();
           }
        });
		
	},

	removeLi : function(elem) {
	 $(elem).parent().parent().remove();
	},
   
    
    _getQuizArrayId : function() {
    	var array = [];
    	$('ul.dropselected').children('li').each(function(){
    			array.push(this.id);
    		});
    	return array;
    },

    _getQuizArrayData : function() {
        var array = [];
        $('ul.dropselected').children('li').each(function(){
            var points = $(this).children("div.questInfo").children("input.points").val();
            array.push(this.id + "," + points);
        });
        return array.join(';');
    }
    
	
});