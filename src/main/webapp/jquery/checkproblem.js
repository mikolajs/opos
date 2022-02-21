class CheckProblem {
    constructor(){
      this.isOpen = false;
      let self = this;
      $('#hintWindow').dialog({
        autoOpen : false,
        height : 400,
        width : 550,
        modal : false,
        close : function() {
            self.isOpen = false;
        }
     });
        this.codeEdit = $('#codeSolveProblemEdit');
        this.codeShow = $('#codeSolveProblemShow');
        this.codeHidden = $('#codeSolveProblemHidden');
        console.log(this.codeEdit.val());
        this.codeEditorJS = this.codeEdit.get(0);
        this.codeShowJS = this.codeShow.get(0);

        console.log(this.codeEditorJS.value);
        this.codeEdit.val(this.codeHidden.val());
        //console.log('Numbers tests before check: ' + this.testsNumbs);
        this.lang = 'cpp';
        this.refresh();
    }

    refresh(){
      let d = hljs.highlight(this.codeEditorJS.value, {language: this.lang}).value;
      console.log(d);
      this.codeShow.html(d);
      this.codeHidden.val(d);
    }

    selectedLang(){
      this.lang = document.getElementById('langChoice').value;
    }

    showHint(elem){
        if(!this.isOpen) {
         $('#hintWindow').dialog('open');
         this.isOpen = true;
        }
    }

    closeHint(){
        $('#hintWindow').dialog('close');
        this.isOpen = false;
    }

    saveProblem(){
       console.log($("#idP").val());
       console.log(this.codeHidden.val());
      //$("#runCode").trigger('click');
    }
}
