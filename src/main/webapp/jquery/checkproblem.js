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
        this.codeEditor = $('#codeSolveProblemEdit');
        this.codeShow = $('#codeSolveProblemShow');
        console.log(this.codeEditor.val());
        this.codeEditorJS = this.codeEditor.get(0);
        this.codeShowJS = this.codeShow.get(0);

        console.log(this.codeEditorJS.value);
        document.getElementById("showCodeEditor").addEventListener('click', (e) => {
          this.showHint(e);
        });
        //this.codeEditor.val(this.codeHidden.val());
        //console.log('Numbers tests before check: ' + this.testsNumbs);
        this.lang = 'cpp';
        this.refresh();
    }

    refresh(){
      let d = hljs.highlight(this.codeEditorJS.value, {language: this.lang}).value;
      this.codeShow.html(d);
    }

    selectedLang(){
      this.lang = document.getElementById('langChoice').value;
      this.refresh();
    }

    showHint(e){
        console.log("Open HINT");
        if(!this.isOpen) {
         $('#hintWindow').dialog('open');
         e.stopPropagation();
         this.isOpen = true;
        }
    }

    closeHint(){
      console.log("start close hint");
        $('#hintWindow').dialog('close');
        this.isOpen = false;
    }

    closeEditor(elem){
      console.log("Start close Editor");
        if(this.isOpen){
            this.isOpen = false;
            this.refresh();
            $('#hintWindow').dialog('close');
        }
    }

    saveProblem(){
       console.log($("#idP").val());
       console.log(this.lang);
       console.log(this.codeEditorJS.value);
       $("#sendCode").val(this.codeEditorJS.value);
       $("#langCode").val(this.lang);
      $("#runCode").trigger('click');
    }
}
