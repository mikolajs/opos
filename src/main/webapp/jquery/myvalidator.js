//element is a button in <form><div><button>
function isValid(elem){
	var massageName = "";
	var massagename = "";
	var massageEmail = "";
	var massagePass = "";
	var massagePassR = "";
	var isOk = true;
	var pass1 = "";
	$form= $(elem).parent().parent('form');
	$form.children('input').each(function(){
		$this = $(this);
		if ($this.hasClass('name')){
			var val = jQuery.trim($this.val());
			if(val.length < 3){
				massagename = "Nazwy muszą mieć co najmniej 3 litery";
				isOk = false;
			}
		} else if($this.hasClass('Name')) {
			var val = jQuery.trim($this.val());
			if(val.length < 3){
				massagename = "Nazwy muszą mieć co najmniej 3 litery";
				isOk = false;
			} else if(val[0].toLowerCase() == val[0] ) {
					massageName = "Pierwsza litera musi być wielka";
					isOk = false;
				}
			
		} else if($this.hasClass('email')){
			var val = jQuery.trim($this.val());
			var array = val.split('@');
		    if (array.length < 2) {
		        isOk = false;
		        massageEmail = "Nieprawidłowy email";
		    }
		    else {
		        if (array[0].length < 2 || array[1].length < 5) {
		            isOk = false;
		            massageEmail = "Nieprawidłowy email";
		        }
		        else {
		            var nextArray = array[1].split('.');
		            if(nextArray.length < 2){
		                isOk = false;
		                massageEmail = "Nieprawidłowy email";
		            }
		            else {
		                if(nextArray[0].length < 2 || nextArray[1].length < 2){
		                    isOk = false;
		                    massageEmail = "Nieprawidłowy email";
		                }
		            }
		        }
		    }
		} else if($this.hasClass('password')){
			var val = jQuery.trim($this.val());
			pass1 = val;
			if(val.length < 8){
				massagePass = "Hasło musi mieć co najmniej 8 liter";	
				isOk = false;
			}
		} else if($this.hasClass('passwordR')){
			var val = jQuery.trim($this.val());
			if(val != pass1){
				massagePassR = "Powtórzone hasło nie jest identyczne";
				isOk = false;
			}
		}
	});
	var massage = "";
	var $massage = $('#validationMassage');
	if(massageName != "") massage += massageName + "<br/>";
	if(massagename != "") massage += massagename + "<br/>";
	if(massagePass != "") massage += massagePass + "<br/>";
	if(massagePassR != "") massage += massagePassR + "<br/>";
	if(massageEmail != "") massage += massageEmail + "<br/>";
	$massage.get(0).innerHTML = massage;
	return isOk;
}
