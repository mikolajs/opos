//stange error first time not show question in ckeditor when click edit
 class EditQuest {
   constructor(){
    this.isOpen = false;
    this.department = "";
    this.oTable = $();
    this.editor = null;
    this.initialize();
   }
   initialize() {
		let self = this;
		$('#questEditor').dialog({
			autoOpen : false,
			height : 650,
			width : 950,
			modal : false,
			close : function() {
				self.isOpen = false;
			}
		});
	    $('#showUsing').dialog({
        	autoOpen : false,
        	height : 450,
        	width : 600,
        	modal : false,
        	close : function() {
        	self.isOpen = false;
        	}
        });
		this.editor = CKEDITOR.replace('questionQuest', {
			width : 500,
			height : 335,
			allowedContent : true,
			language : 'pl',
            toolbar: [
               { name: 'document', items: [ 'Sourcedialog' ] },
          { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ]},
           { name: 'edit', items: [ 'PasteText','PasteFromWord','Undo','Redo' ] },
            { name: 'extraedit', items: [ 'Find','Replace','SelectAll' ]},
                   			{ name: 'paragraph', items:
                   			 [ 'NumberedList','BulletedList','-','Outdent','Indent','-',
                   			 'Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock',
                 			 'BidiLtr','BidiRtl' ] },
                    			{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
                       			{ name: 'insert', items: [ 'CodeSnippet', 'Mathjax', 'AddFile', 'AddImage', 'Table', 'Youtube','SpecialChar'] },
                    			{ name: 'styles', items: [ 'Format', 'Styles', 'FontSize', 'Styles', 'TextColor','BGColor'] }
             ],
             extraPlugins: 'codesnippet,mathjax,youtube,addFile,addImage,sourcedialog,specialchar',
             mathJaxLib : 'https://cdn.mathjax.org/mathjax/2.6-latest/MathJax.js?config=TeX-AMS_HTML'
		});
		CKEDITOR.config.disableNativeSpellChecker = false;

		this.oTable = $('#datatable').dataTable({
							"sPaginationType" : "two_button",
							"bFilter" : true,
							"iDisplayLength" : 20,
							"bLengthChange" : true,
							"oLanguage" : {
								"sSearch" : "Filtruj wiersze: ",
								"sZeroRecords" : "Brak danych do wyświetlenia",
								"sInfoEmpty" : "Brak danych do wyświetlenia",
								"sEmptyTable" : "Brak danych do wyświetlenia",
								"sInfo" : "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
								"oPaginate" : {
									"sPrevious" : "Poprzednie",
									"sNext" : "Następne",
									"sFirst" : "Początek",
									"sLast" : "Koniec",
								},
								"sInfoFiltered" : " - odfiltrowano z _MAX_ wierszy",
								"sLengthMenu" : 'Pokaż <select>'
										+ '<option value="20">20</option>'
										+ '<option value="50">50</option>'
										+ '<option value="100">100</option>'
										+ '<option value="-1">całość</option>'
										+ '</select> wierszy'
							}
						});
        this.department = $('#subjectAndDepartment').children('small').text().trim();
        $('#subjectChoice').val(this.department);

	}

	deleteQuestion(id) {
		this._deleteRow(id);
		this.isOpen = false;
		$('#questEditor').dialog('close');
	}
	
	insertQuestion(id) {
		//alert("insertQuestion:" + id);
		let array = this._getArrayFromForm();
		let idForm = $('#idQuest').val();
		console.log("id: " + id);
		if(idForm == "0" || jQuery.trim(idForm) == "") {
			let nodeTr = this._insertNewRow(array, id);
			nodeTr.setAttribute('id', id);
			console.log(nodeTr.innerHTML +  " id: " + id );
		}
		else {
			this._editRow(array, id);
		}
		this.isOpen = false;
		$('#questEditor').dialog('close');
	}
	
	prepareToSave() {
		this._insertDataFromCKEditorToTextarea();
		let goodStr = this._getGoodAnswerStringFromInputs();
		$("#answerQuest").val(goodStr);
		let fakeStr = this._getFakeAnswerStringFromInputs();
		$('#wrongQuest').val(fakeStr);
		$('#saveQuest').trigger('click');
		return false;
	}
	
    addFakeAnswer() {
    	let fake = $('#fakeAdd').val();
    	fake = jQuery.trim(fake);
    	if(fake.length > 0) {
    		$('#fakeAnswerList').append('<li>'+ 
    				'<button class="btn btn-danger" onclick="editQuest.delFakeAnswer(this)">'+ 
    				'<span class="glyphicon glyphicon-remove-sign"></span></button>' + fake + '</li>');
    		$('#fakeAdd').val("");
    	}
    }
    
    addGoodAnswer(){
    	let good = $('#goodAdd').val();
    	good = jQuery.trim(good);
    	if(good.length > 0) {
    		$('#goodAnswerList').append('<li>'+ 
    				'<button class="btn btn-danger" onclick="editQuest.delFakeAnswer(this)">'+ 
    				'<span class="glyphicon glyphicon-remove-sign"></span></button>' + good + '</li>');
    		$('#goodAdd').val("");
    	}
    }
    
    delFakeAnswer(elem) {
    	$(elem).parent('li').remove();
    }
    
    startNewQuest() {
    	if(this.isOpen) return;
    	this.isOpen = true;
    	this._resetFormEdit();
    	CKEDITOR.instances.questionQuest.setData();
    	$('#questEditor').dialog('open');
    }
    
    editQuestion(elem) {
    	if(this.isOpen) return;
    	this.isOpen = true;
    	this._resetFormEdit();
    	let $tr = $(elem).parent('td').parent('tr');
    	let id = $tr.attr('id');
    	$('#idQuest').val(id);
    	
    	$tr.children('td').each(function(index){
    		let $td = $(this);
    		let array = new Array();
    		let $ul;
    		switch (index) {
    		case 0:
    		    $("#nrQuest").val($td.text());
    		    break;
    		case 1:
                CKEDITOR.instances.questionQuest.setData($td.html().toString());
                break;
    		case 2:
    		    $("#infoQuest").val($td.text().trim());
    		    break;

    		case 3:
    			array = [];
    			$ul = $('#goodAnswerList');
    			$td.children('span.good').each(function(){
    				array.push($(this).text());
    			});
    			for(let i in array){
    				$ul.append('<li>'+ 
    	    				'<button class="btn btn-danger" onclick="editQuest.delFakeAnswer(this)">' + 
    	    				'<span class="glyphicon glyphicon-remove-sign"></span></button>' + array[i] + '</li>');
    			}
    			$('#answerQuest').val("");
    			break;
    		case 4:
    		   array = [];
    			$ul = $('#fakeAnswerList');
    			$td.children('span.wrong').each(function(){
    				array.push($(this).text());
    			});
    			for(let i in array){
    				$ul.append('<li>' + 
    				'<button class="btn btn-danger" onclick="editQuest.delFakeAnswer(this)">' + 
    				'<span class="glyphicon glyphicon-remove-sign"></span></button>' + array[i] + '</li>');
    			}
    			$("#fakeQuest").val("");
    			break;
    		case 5:
                $('#hintQuest').val($td.children('small').text().trim());
    		    break;
    		case 6:
    			$('#levelQuest option').each(function(index){
    				if(this.innerHTML == $td.text().trim() ) {
    				$('#levelQuest').val ((index + 1).toString());
    				}
    			});
    			break;
    		case 7:
    			$('#dificultQuest').val($td.text().trim());
    			break;
    		default:
    			break;
    		}
    	});
    	$('#questEditor').dialog('open');
    }

    addHintChange() {
        $('#hintQuestDiv').toggle();
        $('#questionQuestDiv').toggle();
        $('#saveQuestVisible').toggle();
        $('#deleteQuest').toggle();
    }

    showUsing(elem) {
        let v = $(elem).parent('tr').attr('id');
        document.getElementById('nrOfQuest').value = v;
        document.getElementById('usingQuestInfo').innerHTML = "";
        $('#showUsing').dialog('open');
        $('#buttonQuest').trigger('click');
    }
    
    _insertDataFromCKEditorToTextarea() {
    	$('#questionQuest').val(CKEDITOR.instances.questionQuest.getData());
    }
    
    _getFakeAnswerStringFromInputs() {
    	let fakeList = [];
		let fakes = $('#fakeAnswerList').children('li').each(function(){
			fakeList.push($(this).text());
		});
		
		let inInput = $('#fakeAdd').val();
		inInput = jQuery.trim(inInput);
		if(inInput.length > 0) fakeList.push(inInput);
		
		let fakeStr = fakeList.join(';#;;#;');
		return fakeStr;
    }
    
    _getGoodAnswerStringFromInputs() {
    	let goodList = [];
		let goods = $('#goodAnswerList').children('li').each(function(){
			goodList.push($(this).text());
		});
		
		let inInput = $('#goodAdd').val();
		inInput = jQuery.trim(inInput);
		if(inInput.length > 0) goodList.push(inInput);
		
	    let goodStr = goodList.join(';#;;#;');
		return goodStr;
    }
    
    _getArrayFromForm() {
    	let array = new Array();
    	array.push($('#nrQuest').val());
    	array.push($('#questionQuest').val());
    	array.push($('#infoQuest').val());
    	let goodHTML = "";
    	let goods = this._getGoodAnswerStringFromInputs().split(';#;;#;');
    	for(let i in goods) goodHTML += '<span class="good">' + goods[i] + '</span>';
    	array.push(goodHTML);
    	let fakeHTML = "";
    	let fakes = this._getFakeAnswerStringFromInputs().split(';#;;#;');
    	for(let i in fakes) fakeHTML += '<span class="wrong">' + fakes[i] + '</span>';
    	array.push(fakeHTML);
    	let h = $('#hintQuest').val();
    	let s = "";
    	if(h.trim().length > 0) s = "TAK"; else s = "NIE";
    	array.push(s+'<small style="word-break: break-word;">'+h+'</small>');
    	array.push($('#levelQuest option:selected').text());
    	array.push($('#dificultQuest option:selected').val());
    	array.push('<button class="btn btn-success" onclick="editQuest.editQuestion(this);"><span class="glyphicon glyphicon-edit"></span></button>');
    	return array;
    }
    
    _resetFormEdit() {
    	$('#questEditor').children('form').get(0).reset();
    	$('#fakeAnswerList').empty();
    	$('#goodAnswerList').empty();
    }
    
    _getTrNodeContainsId(id) {
		let trNodes = this.oTable.fnGetNodes();
		for(let i in trNodes) {
			if (trNodes[i].id == id)
				return trNodes[i];
		}
	}
 
    _deleteRow(id) {
		let tr = this._getTrNodeContainsId(id);
		this.oTable.fnDeleteRow(tr);
	}
 
	_insertNewRow(array, id){
		let indexes = this.oTable.fnAddData(array);
		return this.oTable.fnGetNodes(indexes[0]);
	}

	_editRow(array, id) {
		let tr = this._getTrNodeContainsId(id);
		if (tr) this.oTable.fnUpdate(array, tr);  
	}
}