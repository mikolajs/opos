
class ImportData {

constructor() {
  this.listDiv = document.getElementById('listOfSubjectsToChange');
  this.actual = document.getElementById('subjectNamesActual').value.split(';');
  this.toSave = document.getElementById('subjectNamesChange');
  this.createListToChange();
}

createListToChange(){
    let nr = 0;
    for(let i in this.actual){
       if(this.actual[i].trim() == '') continue;
       let d = '<label for="subject_' + i +  '">' + this.actual[i] + '</label>';
       d += '<input id="subject_';
       d += i.toString();
       d += '" type="text" value=""><br>';
       this.listDiv.innerHTML += d;
    }
    //console.log(this.listDiv.innerHTML);
}

save(){
    let arr = [];
    for(let i in this.actual){
        let id = 'subject_'+i;
        let e = document.getElementById(id);
        let t = e.options[e.selectedIndex].text;
        arr.push(this.imported[i] + ":" + t);
    }
    this.toSave.value = arr.join(';');
    document.getElementById('saveHidden').click();
}
}
