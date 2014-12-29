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


    	$("#newMessageForm").dialog({
                    autoOpen: false,
                    height: 700,
                    width: 700,
                    modal: true
        });


        this.showTeacher();
    },

    deleteMessage : function(elem, idStr) {
        if(!confirm("Czy na pewno usunąć wiadomość?")) return;
        $hiddenInput = $("#deleteMessage");
        $hiddenInput.val(idStr);
        $(elem).parent().parent().remove();
        console.log("remove " + idStr);
        $hiddenInput.submit();
    },

    newMessage : function() {
        var $editForm = $("#editMessageForm");;
        $editForm.dialog("option", "title", "Dodaj wiadomość");
        $editForm.dialog("open");
    },
    answerMessage : function(elem, idStr) {
        var $editForm = $("#editMessageForm");
        $editForm.dialog("option", "title", "Odpowiedz na wiadomość");
        $editForm.dialog("open");
    },

    editAnnounce : function() {

    },

    addAnnounce : function() {
         var $editForm = $("#editMessageForm");;
         $editForm.dialog("option", "title", "Dodaj wiadomość");
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
        this._clearAddresses();
        $("#showTeacher").addClass("btn-info");
        this.addressKind = 't';
        $('#teacherMessageS').show();
    },

    showPupil : function() {
        this._hideAll();
        this._clearAddresses();
        $("#showPupil").addClass("btn-info");
        this.addressKind = 'p';
        $('#pupilMessageS').show();
        $('#classMessageS').show();
    },

     showClass : function() {
        this._hideAll();
        this._clearAddresses();
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
        if(this.curSize  > 4) {
            alert("Możesz wysłać tylko do pięciu wiadomości naraz.");
            return;
        }
        var opt = $('#teacherMessage').children('option:selected').get()[0];
        var li = "<li class='list-group-item' id='userId_"+ opt.value + "' >" +  opt.innerHTML +
                   "<span class='btn btn-sm btn-danger btn-right glyphicon glyphicon-remove-sign'" +
                   " onclick='infoTeacher.delAddress(this)'></span></li>";
        $("#peopleToSend").append(li);
        this.curSize++;
     },

     _addPupil : function() {
        if(this.curSize  > 4) {
                            alert("Możesz wysłać tylko do pięciu wiadomości na raz");
                            return;
        }
        var opt = $('#pupilMessage').children('option:selected').get()[0];
                var li = "<li class='list-group-item' id='userId_"+ opt.value + "' >" +  opt.innerHTML +
                                   "<span class='btn btn-sm btn-danger btn-right glyphicon glyphicon-remove-sign'" +
                                   " onclick='infoTeacher.delAddress(this)' ></span></li>";
        $("#peopleToSend").append(li);
        this.curSize++;
     },

     _addClass : function() {
        if(this.curSize  > 2) {
                    alert("Możesz wysłać wiadomość tylko trzech klas na raz");
                    return;
        }
        var opt = $('#classMessage').children('option:selected').get()[0];
        var li = "<li class='list-group-item' id='classId_"+ opt.value + "' >" +  opt.innerHTML +
                           "<span class='btn btn-sm btn-danger btn-right glyphicon glyphicon-remove-sign'" +
                           " onclick='infoTeacher.delAddress(this)' ></span></li>";
        $("#peopleToSend").append(li);
        this.curSize++;
     },

     _clearAddresses : function() {
        $("#peopleToSend").children("li").remove();
        this.curSize = 0;
     },

     delAddress : function(elem) {
        $(elem).parent('li').remove();
        this.curSize--;
     }


});