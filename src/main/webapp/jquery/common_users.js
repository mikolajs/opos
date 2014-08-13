

///globals
//zapisuje aktualną informację o błędach
var info = ""

function validateName(name){
    if (name.length < 3) {
        info = "Za mało liter";
        return false;
    } 
    if (name[0].toLowerCase() == name[0]) {
        info = "Zacznij wielką literą"
        return false;
    }
    if (name[1].toUpperCase() == name[1]){
        info = "Druga litera powinna być mała";
        return false;
    }
    return true;
}
           
function validatePhone(phone){
    var isError = false;
    if(phone.length < 9) {
        info = "Numer zbyt krótki";
        isError = true;
    } 
    else {
        if (phone.match(/\D/)) {
            info = "Dozwolone tylko cyfry bez spacji"
            isError = true;
        }
    }
    return !isError;
}
           
function validateEmail(email) {
    var isError = false;
    var array = email.split('@');
    if (array.length < 2) {
        isError = true;
    }
    else {
        if (array[0].length < 2 || array[1].length < 5) {
            isError = true;
        }
        else {
            var nextArray = array[1].split('.');
            if(nextArray.length < 2){
                isError = true;
            }
            else {
                if(nextArray[0].length < 2 || nextArray[1].length < 2){
                    isError = true;
                }
            }
        }
    }
    info ="Błędny email";
    return !isError;
}
     
function validatePesel(pesel) {
    
    var peselStr = jQuery.trim(pesel);
    if (peselStr.match(/\D/)) {
        info = "Dozwolone tylko cyfry bez spacji";
        return false;
    }
    if (peselStr.length != 11) {
        info = "Tylko " + peselStr.length + " cyfr. Powinno być 13";
        return false;
    }
    var sum = 0;
    for (i = 0; i <11 ; i++){
        sum += parseInt(peselStr[i]);
    }
    if (sum % 2 == 1 || (peselStr[6] != '0' && peselStr[6] != '1')) {
        info = "Nieprawidłowy. Błędna cyfra?";
        return false;
    }
    return true;
}     


function mkPass(){
    return Math.floor(Math.random() * 89999999) + 10000000;
}

//Sprawdzanie numeru aktualnie edytowanej kolumny 
function columnNumber(){
    var nr = -1;
    $editedTd.parent('tr').children('td').each(function(index){
        if (this.id == 'EDITED') nr = index;
    });
    return nr;
}
   
//skreślanie i przywracanie użytkownika                  
function toggleScratch(elem){
    var $input = $(elem);
    var $tr =  $input.parent('td').parent('tr');
    var isScratched = $tr.hasClass('scratched');
    if (isScratched) {
        if(confirm('Chcesz przywrócić użytkownika?')) {
            $tr.removeClass('scratched');
            $input.attr('src','/style/images/delico.png');
            addDataToUsersArray($tr);
        } 
    }
    else {
        if(confirm('Chcesz skreślić użytkownika?')) {
            $tr.addClass('scratched'); 
            $input.attr('src','/style/images/addico.png'); 
            addDataToUsersArray($tr);
        }      
    }   
}


   
function incraseToSaveInfo(){
    var length = users.length;
    if (length == 1) $('#saveButton').show(300);
    if (length == 0) $('#saveButton').hide(300);
    $('#editRows').val(length.toString());
              
}
           
function clearFormsAdd(){
    var $td = $('#formAdd table tr td');
    $td.children().each (function(){
            $(this).val('');
    });
    clearFormsAddInfo();
}

function clearFormsAddInfo() {
    $('#formAdd table tr td').children('span').text('');
}