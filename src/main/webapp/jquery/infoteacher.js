var InfoTeacher = dejavu.Class.declare({

    addressKind: 't',
    curSize: 0,
    lastHideMessageButton: null,
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


    	$("#addMessageForm").dialog({
                    autoOpen: false,
                    height: 700,
                    width: 700,
                    modal: true
        });


        this.showTeacher();
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

    insertComment : function() {
        alert("Insert comment");
    },

    switchAnnounce : function() {
        var checked = $("#allTeacherMessage").prop("checked");
        if(checked) {
            this._clearAddresses();
            var li = "<li class='list-group-item'> Wszyscy nauczyciele </li>";
            $("#peopleToSend").append(li);
            $('#teacherMessageS').hide();
            $("#addAddress").hide();
        } else {
            this._clearAddresses();
            $('#teacherMessageS').show();
            $("#addAddress").show();
        }
    },

    _hideAll : function() {
        $('#classMessageS').hide();
        $('#pupilMessageS').hide();
        $('#teacherMessageS').hide();
        $("#showClass").removeClass("btn-info");
        $("#showPupil").removeClass("btn-info");
        $("#showTeacher").removeClass("btn-info");
        $("#allTeacherMessage").prop("checked", false);
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

        }
     },

     _addTeacher : function() {
        if(this.curSize  > 4) {
            alert("Możesz wysłać tylko do pięciu wiadomości naraz.");
            return;
        }
        var opt = $('#teacherMessage').children('option:selected').get()[0];
        var li = "<li class='list-group-item' id='userId_"+ opt.value + "' >" +
                   " <span class='btn btn-sm btn-danger btn-right glyphicon glyphicon-remove-sign'" +
                   " onclick='infoTeacher.delAddress(this)'></span> "+ opt.innerHTML +"</li>";
        $("#peopleToSend").append(li);
        this.curSize++;
     },

     _addPupil : function() {
        if(this.curSize  > 4) {
                            alert("Możesz wysłać tylko do pięciu wiadomości na raz");
                            return;
        }
        var opt = $('#pupilMessage').children('option:selected').get()[0];
                var li = "<li class='list-group-item' id='userId_"+ opt.value + "' >"  +
                          "<span class='btn btn-sm btn-danger btn-right glyphicon glyphicon-remove-sign'" +
                          " onclick='infoTeacher.delAddress(this)' ></span>" +  opt.innerHTML + "</li>";
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