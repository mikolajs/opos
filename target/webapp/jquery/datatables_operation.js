var $dTable = $();

$.fn.dataTableExt.oApi.fnGetAdjacentTr  = function ( oSettings, nTr, bNext )
{
    /* Find the node's position in the aoData store */
    var iCurrent = oSettings.oApi._fnNodeToDataIndex( oSettings, nTr );
      
    /* Convert that to a position in the display array */
    var iDisplayIndex = $.inArray( iCurrent, oSettings.aiDisplay );
    if ( iDisplayIndex == -1 )
    {
        /* Not in the current display */
        return null;
    }
      
    /* Move along the display array as needed */
    iDisplayIndex += (typeof bNext=='undefined' || bNext) ? 1 : -1;
      
    /* Check that it within bounds */
    if ( iDisplayIndex < 0 || iDisplayIndex >= oSettings.aiDisplay.length )
    {
        /* There is no next/previous element */
        return null;
    }
      
    /* Return the target node from the aoData store */
    return oSettings.aoData[ oSettings.aiDisplay[ iDisplayIndex ] ].nTr;
};


function init_dataTable(teacher){
	if(teacher){
		$dTable = $('#fullList').dataTable({
			"sPaginationType": "two_button",
			"bFilter": false,
			"iDisplayLength": 50,
			"bLengthChange": false,
		    "oLanguage": {
		        "sSearch": "Filtruj wiersze: ",
		        "sZeroRecords": "Brak danych do wyświetlenia",
		        "sInfoEmpty": "Brak danych do wyświetlenia",
		        "sEmptyTable": "Brak danych do wyświetlenia",
		        "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
		        "oPaginate": {
		        	"sPrevious": "<<",
			        "sNext": ">>",
			        "sFirst": "Początek",
			        "sLast": "Koniec",
		        },
		        "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
		        "sLengthMenu": 'Pokaż <select>'+
		        '<option value="40">40</option>'+
		        '<option value="80">80</option>'+
		        '<option value="120">120</option>'+
		        '<option value="-1">całość</option>'+
		        '</select> wierszy'
		        
		      }
		    });
	}
	else {
		$dTable = $('#fullList').dataTable({
			"sPaginationType": "two_button",
			"bFilter": true,
		    "oLanguage": {
		        "sSearch": "Filtruj wiersze: ",
		        "sZeroRecords": "Brak danych do wyświetlenia",
		        "sInfoEmpty": "Brak danych do wyświetlenia",
		        "sEmptyTable": "Brak danych do wyświetlenia",
		        "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
		        "oPaginate": {
		        	"sPrevious": "<<",
			        "sNext": ">>",
			        "sFirst": "Początek",
			        "sLast": "Koniec",
		        },
		        "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
		        "sLengthMenu": 'Pokaż <select>'+
		        '<option value="40">40</option>'+
		        '<option value="80">80</option>'+
		        '<option value="120">120</option>'+
		        '<option value="-1">całość</option>'+
		        '</select> wierszy'
		        
		      }
		    });
	}
	
	
	
	$dTable.setClickRow = function(){
		$dTable.children('tbody').children('tr').dblclick(function() {
			var data =  $dTable.fnGetData(this);
			$('#formAdd').dialog('open');
			$dTable.insertData(data);
		});
	}
	
	$dTable.refreshClickRow = function(){
		$dTable.children('tbody').children('tr').unbind('dblclick').dblclick(function() {
			var data =  $dTable.fnGetData(this);
			$('#formAdd').dialog('open');
			$dTable.insertData(data);
		});
	}
	
	$dTable.tableSize = $dTable.fnGetNodes().length;
	
	$dTable.getNext = function() {
		var id = $dTable.idInput.value;
		var actualTr = $dTable.getTrNodeContainsId(id);
		var newTr = $dTable.fnGetAdjacentTr( actualTr);
		if(newTr) {
			var data = $dTable.fnGetData(newTr);
			$dTable.insertData(data);
		}
		
	}
	
	$dTable.getPrevious = function() {
		var id = $dTable.idInput.value;
		var actualTr = $dTable.getTrNodeContainsId(id);
			var newTr = $dTable.fnGetAdjacentTr( actualTr,false);
	    if(newTr) {
			var data = $dTable.fnGetData(newTr);
			$dTable.insertData(data);
		}	
	}
	
	$dTable.getPosition = function(id) {
		var tr = $dTable.getTrNodeContainsId(id);
		if (tr) {
			return $dTable.fnGetPosition(tr);
		} else
			return -1;
	}
	
	$dTable.getTrNodeContainsId = function (id) {
		var trNodes = $dTable.fnGetNodes();

		for (i in trNodes) {
			var idNode = $(trNodes[i]).children('td').first();
			if (idNode.text() == id)
				return trNodes[i];
		}
	}	
	
	 $dTable.deleteRow = function(id) {
         var tr = $dTable.getTrNodeContainsId(id);
         if (tr) {
             tr.setAttribute('class', 'scratched');
         }
     }
	 
	 $dTable.insertRow =  function(id) {   	
             var position = $dTable.getPosition(id);
             var data = $dTable.getDataArray(id);
             if (position >= 0 && position < $dTable.tableSize) {
            	 //$dTable.showAll();
                 $dTable.fnUpdate(data, position, 0, false);
                //$dTable.setBeginShow();
                 var tr = $dTable.getTrNodeContainsId(id);
                 alert(tr + " id  = " + id);
                 if (tr) {
                     $(tr).removeClass('scratched');
                 }
             } else {
                 var tr = $dTable.fnAddData(data);
                 $dTable.tableSize++;
                 $dTable.refreshClickRow();
             }
      }
	$dTable.editForm = document.getElementById('formAdd');
	$dTable.setClickRow();
	$dTable.idInput = document.getElementById('id');
}



function resetForm() {document.getElementsByTagName('form')[0].reset();}

function showAddRow() { 
    resetForm();
    $('#formAdd').dialog('open');
}       


	
	
