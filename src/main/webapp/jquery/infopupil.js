var InfoPupil = dejavu.Class.declare({

    lastHideMessageButton: null,
    initialize : function() {

       CKEDITOR.replace( 'writeMessage',{
       	    width : 660,
       	    height: 200,
       	    extraPlugins : 'addImage,symbol,youTube,addFile',
       	    toolbar : [
               [ 'Link','Unlink','Anchor' ],
               [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ],
               [ 'Bold','Italic','Underline','Strike','Subscript','Superscript','-','RemoveFormat' ],
               [ 'Styles' ], [ 'TextColor','BGColor' ],
               [ 'NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ],
               [ 'AddImage', 'AddFile', 'YouTube', 'Table', 'HorizontalRule', 'Smiley', 'Symbol'] ]
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
        $editForm.dialog("option", "title", "Dodaj wiadomość");
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


    prepareSubmit : function() {
        var toAdd = "";
        var checked = $("#allTeacherMessage").prop("checked");
        if(checked) {
            $("#toWhoMessage").val("Ogłoszenie");
            return true;
        }
        $("#peopleToSend").children("li").each(function(){
            toAdd += $(this).text() + ";";
        });
        console.log("prepareSubmit toAdd: " + toAdd.length);
        if(toAdd.length == 0) return false;
        $("#toWhoMessage").val(toAdd);
        return true;
    },

     triggerRefreshPupils : function() {
         var classId = $('#classMessage option:selected').val()
         console.log(classId);
         $('#pupilsDataHidden').val(classId);
         document.getElementById("pupilsDataHidden").onblur();
     }


});