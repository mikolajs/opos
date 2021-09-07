
class PerformWork {
  constructor(){
    var self = this;
    this.isOpen = false;
    $(".mboxr").click(function() {self.sendMessage(this);});
    $(".mboxl").click(function() {self.sendLink(this);});
    $("#linkWindow").dialog({
    			autoOpen : false,
    			title: "Dodawanie załącznika",
    			height : 200,
    			width : 600,
    			modal : false,
    			close : function() {
    				self.isOpen = false;
    			}
    		});
  }

  sendMessage(button){
    let formDiv =  $(button).parent().parent();
    let messageArea = formDiv.children("textarea.writeMessage");
    let com = messageArea.val().trim();
    if(com.length < 1) {
        alert("Wiadomość nie może być pusta");
        return;
    }
    $("#comment").val(com);
    messageArea.val("");
    $("#quizId").val(formDiv.parent().parent().get(0).id);
    $("#link").val("");
    $("#send").trigger("click");
  }

  messageSuccess(){
    let id =$("#quizId").val();
    let link = $("#link").val();
    let comment = $("#comment").val();
    let messDiv = $("#" + id).children(".panel").children(".questMessages");
    let mess = '<div class="bg bg-green" >';
    if(link == "") mess += "<p>" + comment + "</p>";
    else mess +=  '<p><a href=' + link + ' target="_blank" >PLIK</a></p>';
    mess += '<div class="messegeSign">';
    mess += '<span class="glyphicon glyphicon-user"></span>';
    mess += '<span class="msg-name"> Ty </span>';
    mess += '<span class="glyphicon glyphicon-calendar"></span>';
    mess += '<span class="msg-date"> w tej sesji </span>';
    mess += "</div>";
    messDiv.append(mess);
    $("#quizId").val("");
    $("#link").val("");
    $("#comment").val("");
  }

  sendLink(button){
    let formDiv =  $(button).parent().parent();
    let messageArea = formDiv.children("textarea.writeMessage");
    $("#comment").val("");
    $("#quizId").val(formDiv.parent().parent().get(0).id);
    $("#link").val("");
    if(!this.isOpen) {
        $("#linkWindow").dialog("open");
        this.isOpen = true;
    }
  }
  _setLink(url){
    $("#link").val(url);
    this.isOpen = false;
    $("#linkWindow").dialog("close");
    $("#send").trigger("click");
  }

  getURLfromIFrame(elem){
     var innerDoc = elem.contentDocument || elem.contentWindow.document;
     var url  = innerDoc.getElementById('linkpath').innerHTML.trim();
     console.log(url);
     if(url.length > 0)
        this._setLink(url);
   }
}
