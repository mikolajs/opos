var InfoPupil = dejavu.Class.declare({

    lastHideMessageButton: null,
    initialize : function() {

       CKEDITOR.replace( 'writeMessage',{
       	    width : 660,
       	    height: 200,
       	    extraPlugins : 'youtube,addImage,addFile,codesnippet,mathjax,specialchar',
            toolbar : [ [ 'Sourcedialog' ],
                       [ 'Link','Unlink','Anchor' ],[ 'Maximize', 'ShowBlocks','-','About' ] ,
                       [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
                       [ 'Find','Replace','-','SelectAll' ],
                       [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
                       [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','BidiLtr','BidiRtl' ],
                       [ 'AddImage',  'Youtube', 'AddFile', 'Mathjax', 'Table','HorizontalRule','SpecialChar','Iframe' ] ,
                       [ 'Styles','Format','FontSize','Font' ],
                       [ 'TextColor','BGColor' ] ]
       	});


    	$("#addMessageForm").dialog({
                    autoOpen: false,
                    height: 500,
                    width: 700,
                    modal: true
        });

    },


    newMessage : function() {
        console.log("new Message");
        var $editForm = $("#addMessageForm");
        $editForm.dialog("option", "title", "Utwórz nową wiadomość");
        $editForm.dialog("open");
    },

    openAddComment : function(elem, id) {
        if(this.lastHideMessageButton != null) {
            this.lastHideMessageButton.show();
        }
        var $commentForm = $("#addCommentForm");
        $("#idMessage").val(id);
        var $elem = $(elem);
        $elem.hide();
        this.lastHideMessageButton = $elem;
        $elem.parent().parent().append($commentForm);
        $commentForm.show();
    },

    insertComment : function(userAndDate) {
        $("#addCommentForm").hide();
        if(this.lastHideMessageButton != null) {
              this.lastHideMessageButton.show();
        }
        $("#idMessage").val();
        var UandD = userAndDate.split(";");
        var comment = $("#writeComment").val();
        var commentCont = '<div class="row msg"><div class="messegeSign col-sm-2">' +
           '<span class="glyphicon glyphicon-user"></span><span class="msg-name">'+ UandD[0]+ '</span><br/>' +
           '<span class="glyphicon glyphicon-calendar">'  +
           '</span> <span class="msg-date">' + UandD[1] + '</span>' +
           '</div><div class="msg-cont  col-sm-10">' + comment + '</div></div>';
        $("#addCommentForm").parent().before(commentCont);
    },


});