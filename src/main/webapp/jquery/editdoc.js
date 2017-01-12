

var Document =  dejavu.Class.declare({
    department: "",
	
	initialize : function(){
		$('#docEdit').html($('#docContent').val());
		CKEDITOR.disableAutoInline = true;

		//document.getElementById('docEdit').innerHTML = $('#extraText').val();
		this.editor = CKEDITOR.inline( 'docEdit', {
			format_tags : 'p;h2;h3;h4;h5;h6;pre;address',
			allowedContent : true,
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
		 this.department = $('#subjectAndDepartment').children('big').text().trim();
         $('#subjectChoice').val(this.department);
	},
	deleteDoc: function() 
	{
		return confirm("Jesteś pewien, że chcesz skasować cały dokument?");
	},
    saveDoc : function() {
    	$('#docContent').val($('#docEdit').html());
    	return true;
    }
	
});