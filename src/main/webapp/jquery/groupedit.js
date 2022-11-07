class GroupEdit {
    constructor(){
        this._mkTableLeft();
        this._mkTableRight();
        this._readStudentClasses();
        this.setStartedGroupStudents();
        this.reloadClassStudents();
        this.currentClass = $('#getClasses option:selected').text().trim();

    }

    reloadClassStudents(){
       this.currentClass = $('#getClasses option:selected').text().trim();
       const jsonString = document.getElementById("studentsList").innerHTML;
       this.tableRight.fnClearTable();
       let json = JSON.parse(jsonString);
       for(let i = 0; i < json.length; i++) {
          if(this._foundInLeftTable(json[i].id.toString())) continue;
          let array = [];
          array[0] = json[i].fname;
          array[1] = json[i].lname;
          array[2] = json[i].email;
          array[3] = this._addButton('right');
          let trNode = this._insertNewRow(array, 'right');
          trNode.setAttribute('id', 'u_' + json[i].id);
       }
    }

    setStartedGroupStudents(){
        const jsonString = document.getElementById("jsonGroupList").value;
        let json = JSON.parse(jsonString);
        for(let i = 0; i < json.length; i++) {
            let array = [];
            array[0] = json[i].fname;
            array[1] = json[i].lname;
            array[2] = json[i].email;
            array[3] = this._findClassesForId(json[i].id);
            array[4] = this._addButton('left');
            let trNode = this._insertNewRow(array, 'left');
            trNode.setAttribute('id', 'u_' + json[i].id);
        }
    }

    addStudent(elem) {
        let $tr = $(elem).parent().parent();
        let idU = $tr.attr('id');
        let tdArray = $tr.children().toArray();
        let array = new Array();
        //console.log(tdArray);
        for(let i = 0; i < 3; i++) array.push(tdArray[i].innerHTML);
        array.push(this.currentClass);
        array.push(this._addButton('left'));
        this._deleteRow(idU, 'right');
        let trNode = this._insertNewRow(array, 'left');
        trNode.setAttribute('id', idU);
    }

    removeStudent(elem){
        let $tr = $(elem).parent().parent();
        let idU = $tr.attr('id');
        let tdArray = $tr.children().toArray();
        if(this.currentClass == tdArray[3].innerHTML.trim() && this._notFoundInRightArray(idU)){
           let array = new Array();
           for(let i = 0; i < 3; i++) array.push(tdArray[i].innerHTML);
           array.push(this._addButton('right'));
           this._deleteRow(idU, 'left');
           let trNode = this._insertNewRow(array, 'right');
           trNode.setAttribute('id', idU);
        } else {
           this._deleteRow(idU, 'left');
        }
    }

    cancelAndRedirect(){
        if(window.confirm('Powrócić do listy grup bez zapisu?'))
        window.location = window.location.protocol + '//' + window.location.host + '/educontent/groups';
    }

    prepareData(){
        let arrTrs = $('#datatableleft tbody').children().toArray();
           let trs = arrTrs.map(function(tr){
            if(tr.childNodes.length < 3) return "";
            else return tr.id.substr(2) + ";" + tr.childNodes[3].innerHTML;});
           document.getElementById('groupListIds').value = trs.join(',');
        //console.log(document.getElementById('groupListIds').value);
        $('#saveGroup').click();
    }

    _mkTableLeft(){
       this.tableLeft = $();
       this.tableLeft = $('#datatableleft').dataTable({
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
    _mkTableRight(){
           this.tableRight = $();
           this.tableRight = $('#datatableright').dataTable({
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

    _readStudentClasses(){
        this.studentsClasses = [];
         let a = document.getElementById('groupListIds').value.split(',');
         if(a.length > 0) this.studentClasses = a;
    }

    _findClassesForId(idU){
        for(let i = 0; i < this.studentClasses.length; i++){
            let a = this.studentClasses[i].split(';');
            if(idU.toString() == a[0]) return a[1];
        }
        return "";
    }

    _insertNewRow(array, tab){
		let indexes;
		if(tab == 'left') {
		  indexes =  this.tableLeft.fnAddData(array);
		  return this.tableLeft.fnGetNodes(indexes[0]);
		}
		else {
		  indexes = this.tableRight.fnAddData(array);
		  return this.tableRight.fnGetNodes(indexes[0]);
		}
	}
    _addButton(tab){
     if(tab == 'right') {
       const editButton = '<button class="btn btn-success" onclick="groupEdit.addStudent(this);">' +
                         '<span class="glyphicon glyphicon-plus"></span></button>';
       return editButton;
     } else {
       const editButton = '<button class="btn btn-danger" onclick="groupEdit.removeStudent(this);">' +
                                '<span class="glyphicon glyphicon-minus"></span></button>';
       return editButton;
     }
    }
    _deleteRow(id, tab) {
        let table;
        let tr;
        if(tab == 'right') table = this.tableRight; else table = this.tableLeft;
        let trNodes = table.fnGetNodes();
    	for (let i in trNodes) {
    		if (trNodes[i].id == id){
    		    tr = trNodes[i];
    		    break;
    		}
    	}
        table.fnDeleteRow(tr);
    }
    _foundInLeftTable(id){
       let trNodes = this.tableLeft.fnGetNodes();
       for (let i in trNodes) {
            if(trNodes[i] == undefined) continue;
       	    if (trNodes[i].id.substr(2) == id) return true;
       }
       return false;
    }
    _notFoundInRightArray(id){
       let trNodes = this.tableRight.fnGetNodes();
       for (let i in trNodes) {
            if(trNodes[i] != undefined) continue;
       	    if (trNodes[i].id == id) return false;
       }
      return true;
    }

}
