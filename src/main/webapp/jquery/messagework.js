
class MessageWork {
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
    $("#comment").val(messageArea.val().replace(/\n/g, "<br />"));
    messageArea.val("");
    $("#link").val("");
    $("#send").trigger("click");
  }

  messageSuccess(){
    let link = $("#link").val();
    let comment = $("#comment").val();
    let messDiv = $("#messages").children(".questMessages");
    let mess = '<div class="bg bg-green" >';
    if(link == "") mess += "<p>" + comment + "</p>";
    else mess +=  '<p><a href=' + link + ' target="_blank" >PLIK</a></p>';
    mess += '<div class="messageSign">';
    mess += '<span class="glyphicon glyphicon-user"></span>';
    mess += '<span class="msg-name"> Ty </span>';
    mess += '<span class="glyphicon glyphicon-calendar"></span>';
    mess += '<span class="msg-date"> w tej sesji </span>';
    mess += "</div>";
    messDiv.append(mess);
    $("#link").val("");
    $("#comment").val("");
  }

  sendLink(button){
    let formDiv =  $(button).parent().parent();
    let messageArea = formDiv.children("textarea.writeMessage");
    $("#comment").val("");
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