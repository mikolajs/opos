class EditProblem {
    constructor(){
    		CKEDITOR.disableAutoInline = true;
        $('#descriptionProblemEdit').html($('#descriptionProblem').val());
    		//document.getElementById('docEdit').innerHTML = $('#extraText').val();
    		this.editor = CKEDITOR.inline( 'descriptionProblemEdit', {
    			format_tags : 'p;h2;h3;h4;h5;h6;pre;address',
    			allowedContent : true,
    			removePlugins : 'dragdrop,basket',
    			disableNativeSpellChecker : false,
    			language : 'pl',
    			 toolbar: [
          			{ name: 'document', items: [ 'Sourcedialog' ] },
                     { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ]},
           			{ name: 'edit', items: [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                     { name: 'extraedit', items: [ 'Find','Replace','SelectAll' ]},
           			{ name: 'paragraph', items:
           			 [ 'NumberedList','BulletedList','-','Outdent','Indent','-',
           			 'Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock',
         			 'BidiLtr','BidiRtl' ] },
            			'/',{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
               			{ name: 'insert', items: [ 'CodeSnippet', 'Mathjax', 'AddFile', 'AddImage', 'Table', 'Youtube','SpecialChar'] },
            			{ name: 'styles', items: [ 'Format', 'Styles', 'FontSize', 'Styles', 'TextColor','BGColor'] }
                        		],
                 extraPlugins: 'codesnippet,mathjax,youtube,addFile,addImage,sourcedialog,specialchar',
                 mathJaxLib : 'https://cdn.mathjax.org/mathjax/2.6-latest/MathJax.js?config=TeX-AMS_HTML'
    		});
        this.testsNumbs = 0;
        //console.log('Numbers tests before check: ' + this.testsNumbs);
        this._checkTestNumbs();
        let aCheck = $('#aHrefCheck');
        aCheck.attr('href', aCheck.attr('href') + $('#idProblem').val());
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
      $('#descriptionProblem').val($('#descriptionProblemEdit').html());
      $("#saveProblem").trigger('click');
    }
    deleteProblem(){
      if(confirm('Czy na pewno skasować ten problem?'))
        $('#deleteProblem').trigger('click');
    }
    addTest(){
      this._addTest("", "");
      this.testsNumbs++;
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
