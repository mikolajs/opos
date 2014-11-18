var InfoTeacher = dejavu.Class.declare({


    initialize : function() {

       CKEDITOR.replace( 'writeMessage',{
       	    width : 660,
       	    height: 200,
       	    extraPlugins : 'addImage,symbol',
       	    toolbar : [ 
               [ 'Link','Unlink','Anchor' ],
               [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
               [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
               [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ],
               [ 'AddImage', 'Table', 'HorizontalRule', 'Smiley', 'Symbol'] ,
               [ 'Styles' ], [ 'TextColor','BGColor' ] ]
       	});

    	$("#editMessageForm").dialog({
                    autoOpen: false,
                    height: 750,
                    width: 700,
                    modal: true
          });
    },

    deleteMessage : function(elem, idStr) {
        if(!confirm("Czy na pewno usunąć wiadomość?")) return;
        $(elem).parent().parent().remove();
        cosole.log("remove " + idStr);
    },

    newMessage : function() {
        var $editForm = $("#editMessageForm");;
        $editForm.dialog("option", "title", "Dodaj wiadomość");
        $editForm.dialog("open");
    },
    editMessage : function(elem, idStr) {
        var $editForm = $("#editMessageForm");
        $editForm.dialog("option", "title", "Edytuj wiadomość");
        $editForm.dialog("open");
    }

});