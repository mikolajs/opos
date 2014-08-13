


function setSelectedIndexWithInner(select,toCompare){
	for(i in select.options) {
        if(select.options[i].innerHTML == toCompare){
            select.options[i].selected = true;
        }
	}
}

function setMultiSelect(select, data){
	clearSelected(select);
	var array = data.split(",");
	for(i in array) {
		array[i] = jQuery.trim(array[i]);
	}
	for(j in array) {
		setSelectedIndexWithInner(select, array[j]);
	}
}

function getArraySelectedInMulti(select){
	var array = new Array();
    for(i in select.options){
    	if(select.options[i].selected == true) array.push(select.options[i].innerHTML)
    }
    return array;
}

function clearSelected(select){
	for(i in select.options) {
        if(select.options[i].selected){
            select.options[i].selected = false;
        }
	}
}

