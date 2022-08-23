
class ImportData {

constructor() {
  this.imported = document.getElementById('subjectNamesImported').value.split(',')
  this.actual = document.getElementById('subjectNamesActual').value.split(',');
  this.toSave = document.getElementById('subjectNamesToSave');
  this.div = document.getElementById('subjectsConnect');
  this.createConnect();
}

createConnect(){
    console.log(this.imported);
    for(let i in this.imported){
       let d = '<div><span>' + this.imported[i] + '</span>'
       d += this.createSelect(i);
       d += '</div>'
        this.div.innerHTML += d;
    }
}

createSelect(subject){
  let s = '<select id="subject_' + subject +  '">';
  for(let i in this.actual){
   s += '<option value="' + this.actual[i].trim() + '"';
   if(i == 0) s += ' selected';
   s += '>' + this.actual[i] + '</option>';
  }
  s += '</select>'
  return s;
}

save(){
    let arr = [];
    for(let i in this.imported){
        let id = 'subject_'+i;
        let e = document.getElementById(id);
        let t = e.options[e.selectedIndex].text;
        arr.push(this.imported[i] + ":" + t);
    }
    this.toSave.value = arr.join(',');
    document.getElementById('saveLoadedHidden').click();
}
}
