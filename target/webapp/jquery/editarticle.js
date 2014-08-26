


var EditArticle =  dejavu.Class.declare({
    ckEditor : null,
    test : "test text",
	
	initialize : function() {
    var self = this;
		this.ckEditor = CKEDITOR.replace( 'editor',{
        width : 714, 
        height: 600,
        extraPlugins : 'youTube,addImage,addFile,formula',
        toolbar : [ [ 'Source' ],
                    [ 'Link','Unlink','Anchor' ],[ 'Maximize', 'ShowBlocks','-','About' ] ,
        [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
        [ 'Find','Replace','-','SelectAll' ],
        [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
        [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ],
        [ 'AddImage',  'YouTube', 'AddFile', 'Formula', 'Table','HorizontalRule','SpecialChar','Iframe' ] ,
        [ 'Styles','Format','FontSize','Font' ],
        [ 'TextColor','BGColor' ] ]
    });  
    
    CKEDITOR.config.forcePasteAsPlainText = true;
       
    $('#tagContainer').multiToButtons({'max':2});
    $('#departContainer').multiToButtons({'max':1});
 
    this.insertThumbnailPreviewSource();
    
    $( "#typePage" ).buttonset();

    $("#helpBox").dialog({
    	autoOpen: false,
    	height: 500,
    	width: 400,
    	modal: true
    	});
    
    $( "#helpButton" ).click( function() {
    	$("#helpBox").dialog("open");
    	});

    $("#typePage input:checked").each(function (){
      self.switchTagsDepart(this);        
    });
    $("#refreshButton").click(function() { self.refreshThumbnail();});

	},

    beforeSubmit : function() {
      this.countIntroduction();
      return this._isValid();
    },
	
	_isValid : function() {
	  var title = document.getElementById('title').value;
	  var valid = true;
	  var info = 'Uzupełnij najpierw:';
	  title = jQuery.trim(title);
	  if (title == '') {
	    valid = false;
	    info = info + ' tytuł.';
	  }
	  if (!valid) {
	    alert(info);
	    return false;
	  } else {
	    return true;
	  }
	},

    insertThumbnailPreviewSource : function(){
		var src = document.getElementById("thumbnail").value;
	    if(src.length > 20) document.getElementById("thumbPreview").setAttribute("src",src);
	},

    switchTagsDepart : function(elem) {
		var $tags = $("#newsContainer");
		var $depart = $("#departContainer");
		if(elem.value =="Aktualności") {
			$tags.show();
			$depart.hide();
		} else {
			$tags.hide();
	        $depart.show();
		}
	},

    refreshThumbnail : function() {
      this.ckEditor.updateElement();
	  var $html = $($("#editor").val());
      var $img = $html.find("img").first();
      var src = $img.attr("src");
      if(!src || src.length < 10) src = "/style/images/nothumb.png";
      document.getElementById("thumbnail").value = src;
      document.getElementById("thumbPreview").setAttribute("src",src);
      return false;
	},

     countIntroduction : function(){
      this.ckEditor.updateElement();
	  var $html = $('<div>' + $("#editor").val() + '</div>');
      var introduction = "";
      var lines = 0;
      var self = this;
      $html.find("p").each( function() {
            if(lines < 3) {
                var inner = this.innerHTML;
                //alert(inner);
                if( self._isEmptyLine(inner)){
                  if(lines > 0) lines = 4; 
                }
                else {
                    $(this).find('img').remove().find('iframe').remove();
                    var tmp = document.createElement("div");
                    tmp.appendChild(this);
                    introduction += tmp.innerHTML;
                    if(introduction.length > 400) lines = 4;
                    else lines++;
                }
            }
        });
      document.getElementById("introduction").value = introduction;
    },

    _isEmptyLine :  function(html) {
        var str = jQuery.trim(html.toString().replace('&nbsp;', ''));
        if(str.length == 0) return true;       
        else return false;
    }
	
});

 function getImageURLfromIFrame(elem){
	var innerDoc = elem.contentDocument || elem.contentWindow.document;
	var url  = innerDoc.getElementById('linkpath').innerHTML;
	$('.cke_dialog_ui_input_text').val(url);
    }

