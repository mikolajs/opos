
function colorTables(){
	$('tbody').children('tr').each(function(index){
		if(index % 2 == 1) this.className = "even_row";
	});
}
