

//need action resize tables, images in slideView

	var EditPresentation = dejavu.Class.declare({

		slideMaxNr : 6,
		$slidesHTML : null,
		$slideChoice : null,
		slideSize : 0,
		$listItem : null,
		editor: null,
		editedElem: null,

		initialize : function(maxSize) {
            CKEDITOR.disableAutoInline = true;
		//document.getElementById('docEdit').innerHTML = $('#extraText').val();
		    this.editor = CKEDITOR.inline( 'slideEdit', {
			format_tags : 'p;h1;h2;h3;h4;pre;address',
			allowedContent : true,
			removePlugins : 'dragdrop,basket',
			disableNativeSpellChecker : false,
			language : 'pl',
			toolbar: [
      		{ name: 'document', items: [ 'Sourcedialog' ] },
            { name: 'basicstyles', items: [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ]},
       		{ name: 'edit', items: [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
            { name: 'extraedit', items: [ 'Find','Replace','SelectAll' ]}, '/',
       		{ name: 'paragraph', items: [
       		 'NumberedList','BulletedList','-','Outdent','Indent','-',
       		 'Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock',
     		 'BidiLtr','BidiRtl' ] },
        	{ name: 'links', items: [ 'Link', 'Unlink', 'Anchor' ] },
           	{ name: 'insert', items: [ 'CodeSnippet', 'Mathjax', 'AddFile', 'AddImage', 'Table', 'Youtube','SpecialChar'] },
        	'/', { name: 'styles', items: [ 'Format', 'Styles', 'FontSize', 'Styles', 'TextColor','BGColor'] }
            ],
             extraPlugins: 'codesnippet,mathjax,youtube,addFile,addImage,sourcedialog,specialchar',
             mathJaxLib : 'https://cdn.mathjax.org/mathjax/2.6-latest/MathJax.js?config=TeX-AMS_HTML'
		});


			this.slideMaxNr = maxSize;

			this.$slidesHTML = jQuery($.parseHTML('<div>'+$('#slidesData').val()+'</div>'));
			//alert(this.$slidesHTML.html());
			this.slideSize = this.$slidesHTML.children('section').length;
			this.createPage();
			//alert(this.slideSize);
			var self = this;
			$('#save').click(function(){self.createData();});
			$('#saveSlideAction').click(function(){self.saveSlideAction();});
			$('#addSlideAction').click(function(){self.addSlideAction();});
			$('#delSlideAction').click(function(){self.delSlide();});
			$('#cancelSlideAction').click(function(){self.cancelSlideAction();});

			if(this.slideSize == 0) this.slideSize = 1;
			$('#slideEditor').hide();
		},

		createPage : function(){
			var self = this;
		    this.$slidesHTML.children('section').each(function(index){
				var $this = $(this);
				//alert($('#slideInfo_'+ (index + 1) +  ' input:first').attr('checked'));
				var node = $.parseHTML(
				'<div class="slide" onclick="editPresentation.editSlide(this)" ><section>' +
				$this.html() + '</section></div>');
				this.slideSize++;
			    $('#slidesView').append(node);
			});
		},

		addSlideAction : function(){
		    console.log("Add action " + this.slideMaxNr + " " + this.slideSize);
		    if(this.slideMaxNr >  this.slideSize) {
			    var node = $.parseHTML('<div class="slide"><section></section></div>');

                this.slideSize++;
                $('#slidesView').append(node);
            }
		},

		saveSlideAction : function(){
        		this.editedElem.html(this.editor.getData());
        		$('#slideEditor').hide();
        },

		delSlide : function(){
			if (this.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu, skasuj całą prezentację");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd?')){
				this.editedElem.remove();
				this.slideSize--;
			}
			$('#slideEditor').hide();
		},

		editSlide : function(elem){
		    this.editedElem = $(elem);
		    this.editor.setData($(elem).html());
		    $('#slideEditor').show();
		},

		cancelSlideAction : function(){
		    $('#slideEditor').hide();
		},

		createData :  function(){
			//if(!isValid(document.getElementById('save'))) return false;

			var dataSlides = "";
            $("#slidesView").children("div.slide").children('section').each(function(i){
                dataSlides += '<section id="slide-' + i + '" >' + $(this).html() + '</section>';
            });
			$('#slidesData').val(dataSlides);
			return true;
		}
	});







