class ShowProblemResults {
  constructor(){
    let arrNames = document.getElementsByClassName('col1');
    let users = new Set();
    for(let i = 0; i < arrNames.length; i++){
      users.add(arrNames[i].innerHTML);
    }
    let select = document.getElementById('showOnlyUser');
    select.innerHTML += '<option value="all" selected>Wszyscy</option>';
    let s = '';
    for(let name of users.keys()) {
      s = '<option value="';
      s += name;
      s += '">';
      s += name;
      s += '</option>';
      select.innerHTML += s;
    }
    this.user = 'all';
    this.good = false;
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

  onlyGood(elem){
    if(elem.checked)  this.good = true;
    else this.good = false;
    this.showSelected();
  }

  onlyUser(elem){
    this.user = elem.options[elem.selectedIndex].value;
    this.showSelected();
  }

  showSelected(){
     let ar = document.getElementsByClassName('checkTr');
     for(let i = 0; i < ar.length; i++){
         ar[i].style.display = "table-row";
         let col1 = ar[i].firstChild
         let n = col1.innerHTML;
         if(this.user != 'all' && n != this.user) ar[i].style.display = "none";
         if(this.good && col1.id[0] != 'G') ar[i].style.display = "none";
     }
  }

  changeGroup(){
    let elem = document.getElementById('takeClass');
     let c = elem.options[elem.selectedIndex].value;
     console.log(c);
     let lok = window.location.toString().split('/');
     lok.pop();
     lok = lok.join('/');
     window.location.assign(lok+"/"+c)
  }

  showCode(elem, e){
    if(!this.isOpen){
      e.stopPropagation();
      let c = elem.parentElement.firstChild;
      if(c.nodeName.toLowerCase() != 'pre') c = c.nextSibling;
      //console.log(c.innerHTML);
      let d = hljs.highlight(c.value, {language: 'cpp'}).value;
      //console.log(d);
      document.getElementById('codeSolveProblemShow').innerHTML = d;
      $('#hintWindow').dialog('open');
      this.isOpen = true;
    }
  }

  closeEditor(elem){
    if(this.isOpen){
        this.isOpen = false;
        $('#hintWindow').dialog('close');
    }
  }
}
