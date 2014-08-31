
var EditVideo = dejavu.Class.declare({
	
	oTable : null, 
	
	initialize  : function() {
		
		$('#addNewItem').dialog({
            autoOpen:false,
            modal:true,
            width:600,
            height:450
        });
		
		$('#addTrigger').click(function(){
			$('form')[0].reset();
			$('#addNewItem').dialog('open');
		});
		
		$("#onserver").change(function() {
        	if(this.checked) {
        		 $("#linkTubeOption").hide();
        	}
        	else {
        		 $("#linkTubeOption").show();
        	}
        });
		
		this.oTable = $('#dataTable').dataTable({
            "sPaginationType": "two_button",
            "bFilter": true,
            "iDisplayLength": 20,
            "bLengthChange": true,
            "oLanguage": {
                "sSearch": "Filtruj wiersze: ",
                "sZeroRecords": "Brak danych do wyświetlenia",
                "sInfoEmpty": "Brak danych do wyświetlenia",
                "sEmptyTable": "Brak danych do wyświetlenia",
                "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
                "oPaginate": {
                    "sPrevious": "Poprzednie",
                    "sNext": "Następne",
                    "sFirst": "Początek",
                    "sLast": "Koniec",
                },
                "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
                "sLengthMenu": 'Pokaż <select>'+
                '<option value="20">20</option>'+
                '<option value="50">50</option>'+
                '<option value="100">100</option>'+
                '<option value="-1">całość</option>'+
                '</select> wierszy'
            }
              });
	}, 
	
	cutLink : function (){
        var $this = $("#linkTube");
        var str = $this.val();
        var array = str.split("/");
        $this.val(array[array.length - 1]);
    },
    
    _getIdFromHref : function(str) {
    	 var array = str.split("/");
         var toInsert = array[array.length - 1];
         return toInsert;
    }, 
    
    edit : function(elem) {
    	$('form')[0].reset();
    	var self = this;
    	var $tr = $(elem).parent().parent();
    	document.getElementById('videoId').value = $tr.attr('id');
    	var link = "";
    	$tr.children('td').each(function(index){
    		switch(index){
    		case 0:
    			document.getElementById('title').value = $(this).text();
    			link = self._getIdFromHref($(this).children('a').attr('href'));
    			break;
    		case 1: 
    			var srcA = $(this).children('img').attr('src').split("/");
    			var isServer = true;
    			if(srcA[srcA.length -1] == 'youtube.png') isServer = false;
    			document.getElementById('onserver').checked = isServer;
    			if(!isServer) {
    				document.getElementById('linkTube').value = link;
                    $("#linkTubeOption").show();
    			}
    			else {
    				 $("#linkTubeOption").hide();
    			}
    			break;
    		case 2: 
    			document.getElementById('descript').value = $(this).text();
    			break;
    		case 3: 
    			self._setSelectOption(this.innerHTML);
    			break;
    		case 4: 
    			document.getElementById('department').value = this.innerHTML;
    		default: 
    			break;
    		}
    	});
    	
    	$('#addNewItem').dialog('open');
    },
    
    sureDel : function(){
    	return confirm("Na pewno usunąć film?");
    },
    
    _setSelectOption :  function(innerOption) {
		$('#subjects option').each(function() {
			if(innerOption != this.innerHTML) this.removeAttribute('selected');
			else {this.setAttribute('selected', true);}
		});
	}
	
});

