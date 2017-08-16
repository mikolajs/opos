


var CheckExam =  dejavu.Class.declare({

    saved: true,

	initialize : function() {
	    //this._getJsonData();
        this._reCountData();
        $("input[type=number]").each(function(){
            $(this).change(function(){
                $('#fixedButton').removeClass('btn-success').addClass('btn-danger');
            });
        });
	},

	pressedSave : function() {
        if(this.saved) return;
        var arrayPoints = [];
        $('section').each(function(){
            var $section = $(this);
            var $input = $section.children('div.panel').children('div.panel-heading').children('input');
            arrayPoints.push({'q': $section.attr('id'), "a": "", "p" : parseInt($input.val())});
        });
         $('#fixedButton').removeClass('btn-danger').addClass('btn-success');
         $('#pointsGain').val(JSON.stringify(arrayPoints));

         window.onbeforeunload = null;
         document.getElementById("pointsGain").onblur();
         this.saved = true;
         $('#fixedButton').removeClass('btn-danger').addClass('btn-success');
	},

	reCountData : function() {
	    this._reCountData();
	    window.onbeforeunload = beforeUnload;
	    $('#fixedButton').removeClass('btn-success').addClass('btn-danger');
	    this.saved = false;
	},

	_reCountData : function() {
	    var points = 0.0;
	     $('section').each(function(){
               var $input = $(this).children('div.panel').children('div.panel-heading').children('input');
               var max = parseFloat($input.attr('max'));
               var val = parseFloat($input.val());
               if(isNaN(val)) $input.val(0);
               else if(val > max) $input.val(max);
               else if(val < 0.0) $input.val(0);
               points += parseFloat($input.val());
         });

         $('#sum').val(points);
         var proc = Math.round( points * 100.0 / parseInt($("#max").val()));
         this._setCanvas(proc);
	},

	_setCanvas : function(proc) {
	     var example = document.getElementById('procent');
         var context = example.getContext('2d');
         context.fillStyle = "rgb(255,0,0)";
         context.fillRect(0, 0, 300, 30);
         context.fillStyle = "rgb(0,255,0)";
         context.fillRect(0,0, (300.0*proc)/100.0, 30);
         context.font="30px bold";
         context.fillStyle="rgb(255,255,255)";
         context.fillText(proc + "%" , 10, 25);
	},

	_getJsonData : function(){
	//not work!!!!!!!!!!!
        var json = $('#pointsGain').val();
        console.log("JSON answers: " + json);
        var data = JSON.parse(json);
        var qi = {};
        for(i in data) {
            qi = data[i];
            var $sec = $('#' + qi.q);
            $sec.find('input').first().val(qi.p);
        }
	},

	checkQuest : function() {
	    var points = 0.0;
	     $('section').each(function(){
	         var $section = $(this);
	         var $div = $section.children('div.panel').children('div.panel-body').children('div.answerBox').first();
	         if($div.length > 0) {
	             var $input = $(this).children('div.panel').children('div.panel-heading').children('input');
                 var max = parseFloat($input.attr('max'));
                 var name = $div.attr('name');
                 //console.log("Attr name: " + name);
                 var p = 0.0;
                 if(name == 'one'){
                   if($div.hasClass('alert-danger')) $input.val(0);
                   else $input.val(max);
                 }
                 else if(name == 'test') {
                    var isBad = false;
                    $div.children('ul').children('li').each(function() {
                        if($(this).hasClass('list-group-item-danger')) isBad = true;
                    });
                    if(isBad) $input.val(0);
                    else $input.val(max);

                 }
                 else {console.log("imposible - error in name in checkQuest");}

	         }

          });
          this.reCountData();
	}



});