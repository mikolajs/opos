
class WorkCommon {
    constructor(){
        this.isOpen = false;
        let self = this;
        $('#hintWindow').dialog({
    			autoOpen : false,
    			height : 400,
    			width : 550,
    			modal : false,
    			close : function() {
    			    self.isOpen = false;
    			}
    	});
    }

    showHint(elem){
        if(!this.isOpen) {
         $('#hintWindow').dialog('open');
         this.isOpen = true;
        }
        let hint = $(elem).parent().children('.questHint').val();
        $('#hintWindowText').text(hint.trim());
    }
    closeHint(){
        $('#hintWindow').dialog('close');
        this.isOpen = false;
    }
}