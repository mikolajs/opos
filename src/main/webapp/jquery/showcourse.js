
var ShowCourse = dejavu.Class.declare({
	isHideSidebar : false,
	isOpenForm : false,
	$panelBody : null,
	initialize : function() {
		var self = this;
        $("#subjectsList").css('height', $(window).height());
	},
	
	hideSidebar : function() {
		if (this.isHideSidebar) {
			$("#subjectsList").show();
			this.isHideSidebar = false;
			$("#sidebar").removeClass("col-lg-1").addClass("col-lg-4");
			$(".main").removeClass("col-lg-10").addClass("col-lg-8");

		} else {
			$("#subjectsList").hide();
			this.isHideSidebar = true;
			$("#sidebar").removeClass("col-lg-4").addClass("col-lg-1");
			$(".main").removeClass("col-lg-8").addClass("col-lg-10");
		}
	},

	checkAnswer : function(elem) {
		this.$panelBody = $(elem).parent();
		var corrects = this.$panelBody.children("input.correct").val().split(';;;');
		var questType = this.$panelBody.children("input.questType").val();
		console.log('prawidłowe: ' + corrects.join(', ') + " questType " + questType );
		var info = "";
		if(questType[0] == 's') {
            this._checkSingle(corrects);
		}
		else if(questType[0] == 'i') {
        	this._checkInput(corrects);
		}
		else if(questType[0] == 'm') {
            this._checkMulti(corrects);
		}
	},

	_checkMulti : function(corrects) {
         var $answers = this.$panelBody.children('ul').children("li").children('input:checked');
         if($answers.length != corrects.length) {
         this._setInfo(false);
         return false;
         }
         var foundAll = true;
         $answers.each(function(){
            var found = false;
            for(i in corrects) if(corrects[i] == this.value)  found = true;
            foundAll = foundAll && found;
         });
         if(foundAll) {
             this._setInfo( true);
             return true;
         }
         else {
             this._setInfo( false);
             return false;
         }
	},

	_checkSingle : function(corrects) {
	     var answer = this.$panelBody.children('ul').children("li").children('input:checked').first().val();
         var answer = jQuery.trim(answer);
         var correct = corrects[0];
         if(correct== answer)  {
         	 this._setInfo(true);
         	 return true;
         }
         else {
         	 this._setInfo(false);
         	 return false;
         	     }
	},

	_checkInput : function( corrects) {
	    var answer = this.$panelBody.children('div').children('input[type="text"]').first().val();
	    var answer = jQuery.trim(answer);
	    console.log('answer = ' + answer + " prawidłowe: " + corrects.join(', '));
	    for(i in corrects) {
	        if(corrects[i] == answer)  {
	            this._setInfo(true);
	            return true;
	        }
	    }
	    this._setInfo( false);
	     return false;
	},

	_setInfo : function( good) {
	    var $infoElem = this.$panelBody.children(".alertWell");
	    if(good) {
	    $infoElem.css("color", "green");
	    $infoElem.text("Bardzo dobrze!");
	    }
	    else {
	    $infoElem.css("color", "red");
	     $infoElem.text("Błędna odpowiedź!");
	    }
	}
	
	
});