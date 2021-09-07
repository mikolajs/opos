
class Groups {
    constructor(){
        this._mkOTable();
    }

    _mkOTable(){
       var oTable = $();
       var oTablePrivate = $();
       oTable = $('#datatable').dataTable({
        			"sPaginationType": "two_button",
        			"bFilter": true,
        			"iDisplayLength": 50,
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
    }

}
