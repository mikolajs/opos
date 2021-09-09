


var EditQuiz =  dejavu.Class.declare({

    table : null,

	initialize : function() {
		 $( "ul.dropselected" ).sortable({
			 connectWith: "ul"
			 });
		this.table = jQuery('#choiceTable')
        						.dataTable(
        								{
        									"sPaginationType" : "two_button",
        									"bFilter" : true,
        									"iDisplayLength" : 50,
        									"bLengthChange" : true,
        									"oLanguage" : {
        										"sSearch" : "Filtruj wiersze: ",
        										"sZeroRecords" : "Brak danych do wyświetlenia",
        										"sInfoEmpty" : "Brak danych do wyświetlenia",
        										"sEmptyTable" : "Brak danych do wyświetlenia",
        										"sInfo" : "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
        										"oPaginate" : {
        											"sPrevious" : "Poprzednie",
        											"sNext" : "Następne",
        											"sFirst" : "Początek",
        											"sLast" : "Koniec",
        										},
        										"sInfoFiltered" : " - odfiltrowano z _MAX_ wierszy",
        										"sLengthMenu" : 'Pokaż <select>'
        												+ '<option value="20">20</option>'
        												+ '<option value="50">50</option>'
        												+ '<option value="100">100</option>'
        												+ '<option value="-1">całość</option>'
        												+ '</select> wierszy'
        									},
        									"aaSorting" : [ [ 0, "asc" ] ]
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
//??????????????????????? not work???
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

	refreshDataTable: function(){
	    var data = $('#jsonForDataTable').val();
    	this.table.fnClearTable();
    	var array = eval( data );
    	var itemHTMLArray = [""];
    	var lev = 0;
    	var dif = 0;
    	var levStr = ["podstawowy", "średni", "rozszerzony"];
    	var difStr = ["", "★", "★★"];
    	var rowIndex;
    	for(var i = 0; i < array.length; i++){
               lev = parseInt(array[i][4]);
               array[i][4] = levStr[lev - 1];
               dif = parseInt(array[i][5]);
               array[i][4] += " " + difStr[dif - 1];
    	      rowIndex = this.table.fnAddData(array[i].slice(1,5));
    	      var row = this.table.fnGetNodes(rowIndex);
              $(row).attr('id', array[i][0]);
    	}
    	this.table.fnDraw();
    	this._bindInsertData();
	},

	_bindInsertData : function() {
    				var self = this;
    				this.table.$('tr').each(function() {
    					$(this).click(function() {
    						self.createNewItem(this, self);
    					});
    				});
    },

    createNewItem: function(row, editQuizObj){
        var $row = $(row);
        var id = $row.attr('id');
        if(editQuizObj._checkExistsId(id)) return;
        var quest = "";
        var nr = "";
        var depart = "";
        var info = "";
        var lev = "";
        $row.children("td").each(function(index){
           switch(index){
            case 0:
              nr = this.innerHTML;
              break;
            case 1:
              quest = this.innerHTML;
              break;
            case 2:
              info = this.innerHTML;
              break;
            case 3:
              lev = this.innerHTML;
              break;
           }
        });
        depart = $("#departments").children('option[selected]').text();


        var htmlLi = '<li id="' +  id  + '">' +
         '<div class="infoText">[' + info +  ']</div>' +
         '<div class="question"><p>' + quest +
         '</p></div>' +
         '<div class="questInfo"><span class="nr">' + nr +
         '</span> | <span class="department">' + depart +
         '</span> | <span class="level">' + lev +
         '</span> | Punkty: <input class="points" type="number" value="1">' +
         '<span class="btn btn-sm btn-danger" onclick="editQuiz.removeLi(this);">' +
         '<span class="glyphicon glyphicon-remove"></span></span> </div> </li>';
         $('.dropselected').append(htmlLi);
    },

    _getQuizArrayId : function() {
    	var array = [];
    	$('ul.dropselected').children('li').each(function(){
    			array.push(this.id);
    		});
    	return array;
    },

    _checkExistsId : function(id){
      var ids = this._getQuizArrayId();
      for(i in ids){
        if(ids[i] == id) return true;
      }
      return false;
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
