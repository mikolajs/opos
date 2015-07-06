
function setSelectedIndexWithInner(select,toCompare){
    //console.log("select.options " + select.options);
	for(i in select.options) {
	    //console.log("option " + select.options[i].innerHTML);
        if(select.options[i].innerHTML == toCompare){
            //console.log("compared " + select.options[i].innerHTML);
            select.options[i].selected = true;
        }
	}
}

function setMultiSelect(select, classList){
   // console.log("start setMutiSelect");
	clearSelected(select);
	//console.log("setMuti : " + select.innerHTML + " " + classList);
	var array = classList.toString().split(",");
	for(i in array) {
		array[i] = jQuery.trim(array[i]);
	}
	console.log("array: " + array);
	for(j in array) {
		if(array[j] != '') setSelectedIndexWithInner(select, array[j]);
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

