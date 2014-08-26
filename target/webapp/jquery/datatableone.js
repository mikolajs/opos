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



function DataTableOne(){
	 var self = this;
	 this.dTable = $();
	 this.tableSize = 0; 
	 this.init = function(){
		 this.dTable = $('#fullList').dataTable({
				"bFilter": true,
				"iDisplayLength": 40,
				"aoColumnDefs": [
			                        { "bVisible": false, "aTargets": [ 0 ] }
			                    ] ,
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
			        '<option value="40">40</option>'+
			        '<option value="80">80</option>'+
			        '<option value="120">120</option>'+
			        '<option value="-1">całość</option>'+
			        '</select> wierszy'
			        
			      }
			    });	
	 }
	
	 this.getTrNodeContainsId = function (id) {
			var trNodes = self.dTable.fnGetNodes();
			for (i in trNodes) {
				var data = self.dTable.fnGetData(trNodes[i]);
				if (data[0] == id)
					return trNodes[i];
			}
		}	
	 
	 this.deleteRow = function(id) {
         var tr = this.getTrNodeContainsId(id);
         this.dTable.fnDeleteRow(tr);
     }
	 
	 this.scratchRow = function(id){
		 var tr = this.getTrNodeContainsId(id);
		 $(tr).addClass('scratched');
	 }
	 
	 this.insertNewRow = function(array){
		 var indexes = this.dTable.fnAddData(array);
         return this.dTable.fnGetNodes(indexes[0]);
	 }
	 
	 
	 this.editRow =  function(array) {  
		 var id = array[0];
         var tr = self.getTrNodeContainsId(id);
         $(tr).removeClass('scratched');
         if (tr) self.dTable.fnUpdate(array, tr);  
         
	 }
	 
 
}

     