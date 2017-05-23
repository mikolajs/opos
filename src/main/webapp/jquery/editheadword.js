	
	var EditHeadWord = dejavu.Class.declare({
		
		slideMaxNr : 6,
		$slidesHTML : null,
		$slideChoice : null,
		slideSize : 0,
		$listItem : null,
		editor: null,
		
		initialize : function(maxSize) {
		    this.editor = CKEDITOR.on( 'instanceCreated', function( event ) {
                                      var editor = event.editor,
                                          element = editor.element;
                                          //editor.dataProcessor.writer.selfClosingEnd = '/>';
                                          editor. config.format_tags = 'p;h2;h3;h4;h5;h6;pre;address';
                                          editor.config.disableNativeSpellChecker = false;
                                          editor.config.language = 'pl';
                                          editor.config.allowedContent = true;
                                          editor.config.toolbar = [
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
                          		];
                              editor.config.extraPlugins = 'codesnippet,mathjax,youtube,addFile,addImage,sourcedialog,specialchar';
                              editor.config.mathJaxLib = 'https://cdn.mathjax.org/mathjax/2.6-latest/MathJax.js?config=TeX-AMS_HTML';
                                  });

			this.slideMaxNr = maxSize;
			this.$slidesHTML = jQuery($.parseHTML('<div>'+$('#headWordsData').val()+'</div>'));
			//alert(this.$slidesHTML.html());
			this.slideSize = this.$slidesHTML.children('section').length;
			this.createPage();
			//alert(this.slideSize);
			var self = this;
			$('#save').click(function(){self.createData();});
			$('#addSlideAction').click(function(){self.addSlideAction();});
			$('#delSlideAction').click(function(){self.delSlide();});	
			
			if(this.slideSize == 0) this.slideSize = 1;
			for( i = this.slideSize +1;  i <  this.slideMaxNr + 1;  i++){
		    	$('#section_'+ i).hide();
		    }
		},
		
		createPage : function(){	
			var self = this;
		    this.$slidesHTML.children('section').each(function(index){
				var $this = $(this);
				//alert($('#slideInfo_'+ (index + 1) +  ' input:first').attr('checked'));
				var html = $this.html();
				//alert(html + "index: " + (index +1));
			    $('#section_'+ (index + 1)).html(html);
			}); 	
		},
		
		addSlideAction : function(){
			if(this.slideMaxNr <=  this.slideSize) {
				alert("Maksmalna ilość slajdów to: " + this.slideMaxNr);
				return;
			}
			this.slideSize++;
			$("#section_"+ this.slideSize ).show();
		},
		
		delSlide : function(){
			if (this.slideSize == 1) {
				alert("Nie można usunąć ostatniego slajdu, skasuj całe hasło.");
				return;
			}
			if(confirm('Na pewno chcesz usunąć slajd nr ' + this.slideSize + ' ?')){
				$("#section_"+ this.slideSize ).html("").hide();
				this.slideSize--;
			}
		},
			
		createData :  function(){
			//if(!isValid(document.getElementById('save'))) return false;
			var dataSlides = "";
			for(i = 1; i <= this.slideSize; i++){
				var $section = $('#section_'+i);
				var $checkbox = $('#slideInfo_'+ i + ' input:first');
				if($section.height() > 600 && $checkbox.prop('checked') ) {
					$checkbox.removeAttr('checked');
					alert("Element za długi na slajd");
				}
				var html = $section.html();
				
				var isSlide = $checkbox.prop('checked');
				dataSlides += '<section  class="slide">' + html.toString() + '</section>';
			}
			$('#headWordsData').val(dataSlides);
		    
			//alert(dataSlides);
			return true;
		}
		
	});
	
	
	
	
	
		
					
		