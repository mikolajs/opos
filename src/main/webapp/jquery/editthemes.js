
 class EditThemes {
    constructor(){
     // $("#addTheme").click( () => {$("#formAdd").show();});
      this.editFormDiv = new EditFormDiv();
      this.editFormDiv.init(400,600);
      $("#discard").click(() => {this.editFormDiv.close();});
      $('#lessonDate').datetimepicker({ locale: 'pl', format: 'YYYY-MM-DD'});
    }
    deleteRow(){
        console.log("delete row ");
        this.editFormDiv.deleteRow(document.getElementById("themeId").value);
    }
    addRow(id){
        console.log("add Row");
        this.editFormDiv.insertNewRow(id);
        this.editFormDiv.close();
    }
    editRow(id){
        console.log("edit row");
        this.editFormDiv.insertRowAndClose(id);
    }
    validateForm(){
        console.log("Validate form");
        return false;
    }
 }

  EditFormDiv.prototype.putDataToForm = function(array){
      document.getElementById('themeId').value = array[0];
      document.getElementById('lessonDate').value = array[1];
      document.getElementById('theme').value = array[2];
      document.getElementById('directing').value = array[3] ;
      document.getElementById('description').value = array[4] ;
  }

  EditFormDiv.prototype.getData = function(){
    var array = new Array();
    array[0] = document.getElementById('themeId').value;
    array[1] = document.getElementById('lessonDate').value;
    array[2] =  document.getElementById('theme').value;
    array[3] = document.getElementById('directing').value;
    array[4] = document.getElementById('description').value;
    return array;
  }

  EditFormDiv.prototype.addNewItemInfo = "Dodawanie tematu";
  EditFormDiv.prototype.editItemInfo = 'Edycja tematów';

