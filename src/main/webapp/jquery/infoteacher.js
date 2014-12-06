var InfoTeacher = dejavu.Class.declare({

    addressKind: 't',
    curSize: 0,
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

        this.showTeacher();
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
    },

    _hideAll : function() {
        $('#classMessageS').hide();
        $('#pupilMessageS').hide();
        $('#teacherMessageS').hide();
        $("#showClass").removeClass("btn-info");
        $("#showPupil").removeClass("btn-info");
        $("#showTeacher").removeClass("btn-info");
    },

    showTeacher : function() {
        this._hideAll();
        $("#showTeacher").addClass("btn-info");
        this.addressKind = 't';
        $('#teacherMessageS').show();
    },

    showPupil : function() {
        this._hideAll();
        $("#showPupil").addClass("btn-info");
        this.addressKind = 'p';
        $('#pupilMessageS').show();
        $('#classMessageS').show();
    },

     showClass : function() {
        this._hideAll();
        $("#showClass").addClass("btn-info");
        this.addressKind = 'c';
        $('#classMessageS').show();
     },

     addAddress : function() {
        if(this.addressKind == 't') {
            this._addTeacher();
        }
        else if(this.addressKind == 'p') {
            this._addPupil();
        }
        else {
            this._addClass();
        }
     },

     _addTeacher : function() {
        console.log("add teacher");
        var opt = $('#teacherMessage').children('option:selected').get()[0];
        console.log(opt.value + " - " + opt.innerHTML);
     },

     _addPupil : function() {
        console.log("add pupil");
     },

     _addClass : function() {
        console.log("add Class");
     },


});