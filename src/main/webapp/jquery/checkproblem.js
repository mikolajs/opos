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
        console.log(this.codeEdit.val());
        this.codeEditorJS = this.codeEdit.get(0);
        this.codeShowJS = this.codeShow.get(0);

        console.log(this.codeEditorJS.value);
        this.codeEdit.val($('#codeSolveProblemHidden').val());
        //console.log('Numbers tests before check: ' + this.testsNumbs);
        this.lang = 'cpp';
        this.refresh();
    }

    refresh(){
      let d = hljs.highlight(this.codeEditorJS.value, {language: this.lang}).value;
      console.log(d);
      this.codeShow.html(d);
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



    _checkTestNumbs(){
      let jsonStr = $('#inOutData').val().replace('\"', '"');
      //console.log(jsonStr);
      let json = JSON.parse(jsonStr);
      //console.log(json);
      this.testsNumbs = json.input.length;
      //console.log("After check numbers tests : " + this.testsNumbs);
      for(let i = 0; i < this.testsNumbs; i++){
        //console.log("mkTest");
        this._addTest(json.input[i], json.output[i]);
      }
    }
    _readInputOutputTests(){
      let dataTestDivs = $('#testsDataDiv').children();
      //console.log( $('#testsDataDiv').html());
      let inputArray = [];
      let outputArray = [];
      dataTestDivs.each(function(){
        let d = $(this);
        //console.log(d.length);
        //console.log(d.html());
        let inD = d.children().children('.inputData').val();
        let outD = d.children().children('.outputData').val();
        //console.log(inD);
        //console.log(outD);
        if(inD.length > 0) inputArray.push(inD);
        if(outD.length > 0) outputArray.push(outD);
      });
      //'{"tests": [' + ']}'
      //console.log(inputArray.join(',') +  outputArray.join(','));
      let json = {};
      json.input = inputArray;
      json.output = outputArray;
      return json;
      //return '{"tests":{"input":['+ inputArray.join(',') + '], "output":['+ outputArray.join(',') + ']}}';
    }
    saveProblem(){
      let json = this._readInputOutputTests();
      $('#inOutData').val(JSON.stringify(json));
      //console.log($('#inOutData').val());
      $("#saveProblem").trigger('click');
    }

    _addTest(inData, outData){
      let dataDiv = '<div class="row"><div class="col-lg-5"><textarea class="testsData inputData"';
      dataDiv += '>'+inData+'</textarea></div><div class="col-lg-5"><textarea class="testsData outputData';
      dataDiv += '">'+outData+'</textarea></div><div class="col-lg-2"><button class="btn btn-danger" ';
      dataDiv += 'onclick="editProblem.delTest(this);"><span class="glyphicon glyphicon-minus"></span></button>';
      dataDiv += '</div></div>';
      $('#testsDataDiv').append(dataDiv);
    }
    delTest(elem){
      if(confirm('Czy na pewno skasować test?')) {
        $(elem).parent().parent().remove();
      } else return;
    }
}
