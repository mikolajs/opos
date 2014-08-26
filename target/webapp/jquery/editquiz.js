


var EditQuiz =  dejavu.Class.declare({
	
	initialize : function() {
		this._makeQuizList();
		 $( "ul.dropselected" ).sortable({
			 connectWith: "ul"
			 });
		 $( "ul.dropall" ).sortable({
			 connectWith: "ul"	
			 });
	},
	
	prepareToSave : function() {
		$('#questionsQuiz').val(this._getQuizArrayId());
		$('#saveQuiz').trigger('click');
		return false;
	},
	prepareToDelete : function() {
		if(confirm('Na pewno chcesz usunąć bezpowrotnie cały test?')) 
		{
			$('#deleteQuiz').trigger('click');
		}
		return false;
	},
	//find selected questions and add to selected list
	_makeQuizList : function() {
		var arrayQuestId = $('#questionsQuiz').val().split(';');
		var allQuests = $('ul.dropall').children('li').each(function(){
			for(i in arrayQuestId){
				if(this.id == arrayQuestId[i]) {
					$('ul.dropselected').append($(this));
					break;
				} 
			}
		});
		
	},
   
    
    _getQuizArrayId : function() {
    	var array = [];
    	$('ul.dropselected').children('li').each(function(){
    			array.push(this.id);
    		});
    	return array.join(';');
    }
    
	
});