


var EditExam =  dejavu.Class.declare({

	initialize : function() {
		 $( "ol.groupsExam" ).sortable();
	},

	appendTest : function(elem) {
	    $ol = $("ol.groupsExam");
	    if($ol.children('li').length > 4) {
	        alert("Można utworzyc tylko cztery grupy");
	        return;
	    };
	    var $li = $(elem);
        var title = $li.children('span.titleQuiz').text();
        var id = $li.attr('id');
        var li = '<li class=""  id="' +  id  +
                 '"><span class="titleQuiz" >'+  title + '</span>' +
                 '<span class="glyphicon glyphicon-remove btn text-danger"' +
                 ' onclick="editExam.removeTest(this);"></span></li>';
        $ol.append(li);
        $li.remove();
	},

	removeTest : function(elem) {
        var $li = $(elem).parent();
        var title = $li.children('span.titleQuiz').text();
        var id = $li.attr('id');
        var li = '<li class="list-group-item btn" onclick="editExam.appendTest(this);"  id="' +  id  +
                 '"><span class="glyphicon glyphicon-plus addItem"></span>' +
                 '<span class="titleQuiz" >'+  title + '</span></li>';
        $("ul.examsList").append(li);
        $li.remove();
	},

	prepareData : function() {
	    var array = this._getTestArrayId()
	    $('#testsList').val(array.join(';'));
	    console.log($('#testsList').val());
	    var descr = $('#descriptionExam').val();
	    if(descr.length < 3 ) {
	        alert("Zbyt krótki opis (min 3 litery)");
            return false;
	    }

	    if(array.length < 1 || array.length > 4) {
	        alert("Grup może być od jednej do czterech");
	        return false;
	    }
	    return false;
	},


    _getTestArrayId : function() {
    	var array = [];
    	$('ol.groupsExam').children('li').each(function(){
    			array.push(this.id);
    		});
    	return array;
    }

});