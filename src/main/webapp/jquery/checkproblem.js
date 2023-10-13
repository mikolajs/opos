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
        //console.log(this.codeEditor.val());
        this.codeEditorJS = this.codeEditor.get(0);
        this.codeShowJS = this.codeShow.get(0);

        //console.log(this.codeEditorJS.value);
        document.getElementById("showCodeEditor").addEventListener('click', (e) => {
          this.showHint(e);
        });
        //this.codeEditor.val(this.codeHidden.val());
        //console.log('Numbers tests before check: ' + this.testsNumbs);
        let ll = document.getElementById("lastLang").value;
        console.log('Loaded lastLang: ' + ll);
        if(ll == '') this.lang = 'cpp';
        else {
            this.lang = ll;
            document.getElementById('langChoice').value = ll;
        }
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
        if(!this.isOpen) {
         $('#hintWindow').dialog('open');
         e.stopPropagation();
         this.isOpen = true;
        }
    }

    closeHint(){
        $('#hintWindow').dialog('close');
        this.isOpen = false;
    }

    closeEditor(elem){
        if(this.isOpen){
            this.isOpen = false;
            this.refresh();
            $('#hintWindow').dialog('close');
        }
    }

    saveProblem(){
       $("#sendCode").val(this.codeEditorJS.value);
       $("#langCode").val(this.lang);
      $("#runCode").trigger('click');
    }

    insertCode(elem){
       let par = $(elem).parent();
       console.log('Start insert code ');
       let code = par.children('.code').text();
       this.lang = par.parent().children('.col4').text();
       document.getElementById('langChoice').value = this.lang;
       let d = hljs.highlight(code, {language: this.lang}).value;
       this.codeShow.html(d);
       this.codeEditorJS.value = code;
    }
}
