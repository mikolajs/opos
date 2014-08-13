


function EditFormDiv(){
	var self = this;
	this.editDiv = $();
	this.dataTable = $();
	
	this.init = function(heightForm, widthForm){
		this.editDiv = $('#formAdd');
		this.editDiv.dialog({
			autoOpen : false,
			height : heightForm,
			width : widthForm,
			modal : true
		});
		this.dataTable = new DataTableOne();
		this.dataTable.init();

		$('#newadd').click(function(){
			self.openNew();
		});	
		var trNodes = this.dataTable.dTable.fnGetNodes();
		for(i in trNodes){
			var tr = trNodes[i];
			$(trNodes[i]).dblclick(function(){
				self.openDblClick(this);
			});
		}
	}
	this.clearCKEditor = function() { }

	this.reset = function(){
		this.editDiv.children('form').each(function(){
			this.reset();
		});
	 self.clearCKEditor();
	}
   //button add 
	this.openNew = function(){
		self.editDiv.dialog({title: self.addNewItemInfo}); 
		self.editDiv.dialog('open');
		self.reset();
		$('#delete').hide();
	}
	//click on table
	this.openDblClick = function(elem){
		self.editDiv.dialog({title: self.editItemInfo}); 
		self.editDiv.dialog('open');
		var array = self.dataTable.dTable.fnGetData(elem);
		self.putDataToForm(array);
		$('#delete').show();
	}
	
	this.close = function(){ 
		this.editDiv.dialog('close');
	}


	//called by ajax after edit item
	this.insertRowAndClose =  function(id){
		self.editRow(id);
		self.close();
	}
	//called by ajax after save new
	this.insertRowAndClear = function(id){
		self.insertNewRow(id);
		self.reset();
	}
	//called by ajax after delete action
	this.scratchRow = function(id){
		self.dataTable.scratchRow(id);
		self.close();
	}

	this.insertNewRow = function(id){
		var array = self.getData();
		array[0] = id;
		var tr = self.dataTable.insertNewRow(array);		
		$(tr).bind('dblclick',function(){
			self.openDblClick(this);
		});
	}
	
	this.editRow = function(id){
		var array = self.getData();
		if(array[0] != id) {
			alert("Błąd. Próba zapisu nieprawidłowego id.");
			return;
		}
		self.dataTable.editRow(array);
	}

	this.getNext = function() {
		var id = self.idInput.value;
		var actualTr = self.dataTable.getTrNodeContainsId(id);
		var newTr = self.dataTable.dTable.fnGetAdjacentTr( actualTr);
		if(newTr) {
			var data = self.dataTable.dTable.fnGetData(newTr);
			self.putDataToForm(data);
		}
		
	}
	
	this.getPrevious = function() {
		var id = self.idInput.value;
		var actualTr = self.dataTable.getTrNodeContainsId(id);
			var newTr = self.dataTable.dTable.fnGetAdjacentTr( actualTr,false);
	    if(newTr) {
			var data = self.dataTable.dTable.fnGetData(newTr);
			self.putDataToForm(data);
		}	
	}

	//this.mkAlert = function() {alert("alert");}

}
