class EditExam {

 constructor(){
   this.keysNum = 48;
   this.table = null;
   this.groupsNmb = 1;
   this.currentGroup =  1;
   $("ol.groupsExam").sortable();
   this.table = jQuery('#choiceTable')
      .dataTable({
        "sPaginationType": "two_button",
        "bFilter": true,
        "iDisplayLength": 50,
        "bLengthChange": true,
        "oLanguage": {
          "sSearch": "Filtruj wiersze: ",
          "sZeroRecords": "Brak danych do wyświetlenia",
          "sInfoEmpty": "Brak danych do wyświetlenia",
          "sEmptyTable": "Brak danych do wyświetlenia",
          "sInfo": "Widzisz wiersze od _START_ do _END_  z wszystkich _TOTAL_",
          "oPaginate": {
            "sPrevious": "Poprzednie",
            "sNext": "Następne",
            "sFirst": "Początek",
            "sLast": "Koniec",
          },
          "sInfoFiltered": " - odfiltrowano z _MAX_ wierszy",
          "sLengthMenu": 'Pokaż <select>' +
            '<option value="20">20</option>' +
            '<option value="50">50</option>' +
            '<option value="100">100</option>' +
            '<option value="-1">całość</option>' +
            '</select> wierszy'
        },
        "aaSorting": [
          [0, "asc"]
        ]
      });

    this._insertKeysOnStart();
    this._insertQuestionsOnStart();
  }

  prepareData() {
    var array = this._getGroupQuestionsArrayId();
    let eData = array.map((a) => a.join(';')).join('|');
    //console.log(array);
    console.log(eData);
    $('#testsList').val(eData);
    //console.log($('#testsList').val());
    var descr = $('#descriptionExam').val();
    if (descr.length < 3) {
      alert("Zbyt krótki opis (min 3 litery)");
      return false;
    } else {
      $('#saveExam').trigger('click');
    }
    return true;
  }

  genRandomStrings() {
    if ($('#keysList').val().length > 8 && !confirm("Generując nowe klucze kasujesz stare! Kasować?")) return;
    //var groups = $('ol.groupsExam').children('li').length;
    if (this.groupsNmb < 2) {
      alert("Kody generuje się dla większej ilości grup");
      return;
    }
    var $keysDiv = $('#keysPanel').children('div.panel-body');
    $keysDiv.empty();
    var array = [];
    var letters = "ABCD";

    var onGroups = this.keysNum / this.groupsNmb;
    var letter = '';
    var code = "";
    var tmpArray = [];
    for (let g = 0; g < this.groupsNmb; g++) {
      letter = letters.charAt(g);
      for (let j = 0; j < onGroups; j++) {
        do {
          code = this._randomString();
        }
        while (!this._notExists(tmpArray, code));
        code = letter + code;
        tmpArray.push(code);
        $keysDiv.append('<span class="keysShow">' + code + '</span> ');
        if (j % 3 == 2) $keysDiv.append('<br/>');
      }
      array = array.concat(tmpArray);
      tmpArray = [];
      $keysDiv.append('<hr/>');
    }
    console.log(array.join('\n'));
    $('#keysList').val(array.join(';'));
  }

  print() {
    window.print();
  }

  refreshDataTable(){
    var data = $('#jsonForDataTable').val();
    this.table.fnClearTable();
    var array = eval( data );
    var itemHTMLArray = [""];
    var lev = 0;
    var dif = 0;
    var levStr = ["podstawowy", "średni", "rozszerzony"];
    var difStr = ["", "★", "★★"];
    var rowIndex;
    for(let i = 0; i < array.length; i++){
             lev = parseInt(array[i][4]);
             array[i][4] = levStr[lev - 1];
             dif = parseInt(array[i][5]);
             array[i][4] += " " + difStr[dif - 1];
          rowIndex = this.table.fnAddData(array[i].slice(1,5));
          var row = this.table.fnGetNodes(rowIndex);
            $(row).attr('id', array[i][0]);
    }
    this.table.fnDraw();
    this._bindInsertData();
  }

  showGroup(grLetter){
    document.getElementById('groupA').style.display = 'none';
    document.getElementById('groupB').style.display = 'none';
    document.getElementById('groupC').style.display = 'none';
    document.getElementById('groupD').style.display = 'none';
    document.getElementById('keysPanel').style.display = 'none';
    let nodes = document.getElementById('groups-buttons').childNodes;
    for(let n of nodes){
      if(n.classList) {
        n.classList.remove('btn-info');
        n.classList.add('btn-default');
        //console.log(n.firstChild.nodeValue);
        if(n.id == 'groupButton' + grLetter){
          n.classList.add('btn-info');
        }
      }
    }
    this.currentGroup = grLetter.charCodeAt(0) - 64;
    console.log(this.currentGroup);
    document.getElementById('group'+grLetter).style.display = 'inline';
  }

  addNewGroup(){
    if(this.groupsNmb >= 4) return;
    this.groupsNmb++;
    let grLetter  = String.fromCharCode(this.groupsNmb + 64);
    let tag = document.createElement("button");
    tag.classList.add('btn');
    tag.classList.add('btn-default');
    tag.setAttribute('onclick', "editExam.showGroup('" + grLetter + "');");
    tag.setAttribute('id', 'groupButton'+grLetter);
    let text = document.createTextNode('Grupa ' + grLetter);
    tag.appendChild(text);
    //let b = '<button type="button" class="">Grupa ' + grLetter +  '</button>';
    let groupsDiv = document.getElementById('groups-buttons');
    groupsDiv.appendChild(tag);
  }

  delExistsGroup() {
    if(this.groupsNmb < 2) return;
    let grLetter = String.fromCharCode(this.groupsNmb+64);
    let grId = '#group'+grLetter;
    this.groupsNmb--;
    if(this.currentGroup > this.groupsNmb){
      this.currentGroup--;
      this.showGroup('A');
    }
    $(grId).children('ul').empty();
    let grInfo = 'groupButton' + grLetter;
    document.getElementById(grInfo).remove();
  }

  showKeys(){
    document.getElementById('groupA').style.display = 'none';
    document.getElementById('groupB').style.display = 'none';
    document.getElementById('groupC').style.display = 'none';
    document.getElementById('groupD').style.display = 'none';
    document.getElementById('keysPanel').style.display = 'inline';
  }

  _bindInsertData() {
    				var self = this;
    				this.table.$('tr').each(function() {
    					$(this).click(function() {
    						self.createNewItem(this, self);
    					});
    				});
    }

  createNewItem(row, editQuizObj){
        var $row = $(row);
        var id = $row.attr('id');
        if(editQuizObj._checkExistsId(id)) return;
        var quest = "";
        var nr = "";
        var depart = "";
        var info = "";
        var lev = "";
        $row.children("td").each(function(index){
           switch(index){
            case 0:
              nr = this.innerHTML;
              break;
            case 1:
              quest = this.innerHTML;
              break;
            case 2:
              info = this.innerHTML;
              break;
            case 3:
              lev = this.innerHTML;
              break;
           }
        });
        depart = $("#departments").children('option:selected').text();

        let htmlLi = this._mkQuestItem(id, info, quest, nr, depart, lev, 1);
       let groupId = '#group'+String.fromCharCode(this.currentGroup + 64);
         $(groupId).children('ul.dropselected').append(htmlLi);
    }

    _mkQuestItem(id, info, quest, nr, depart, lev, p){
       let htmlLi = '<li id="' +  id  + '" class="quizElement">' +
       '<div class="infoText">[' + info +  ']</div>' +
       '<div class="question"><p>' + quest +
       '</p></div>' +
       '<div class="questInfo"><span class="nr">' + nr +
       '</span> | <span class="department">' + depart +
       '</span> | <span class="level">' + lev +
       '</span> | Punkty: <input class="points" type="number" value="' + p + '" min="1">' +
       '<span class="btn btn-sm btn-danger" onclick="editExam.removeLi(this);">' +
       '<span class="glyphicon glyphicon-remove"></span></span> </div> </li>';
       return htmlLi;
    }

    removeLi(elem) {
      $(elem).parent().parent().remove();
    }

    _checkExistsId(id){
      var ids = this._getQuizArrayId();
      for(let i in ids){
        if(ids[i] == id) return true;
      }
      return false;
    }

    _getQuizArrayId() {
    	var array = [];
    	$('ul.dropselected').children('li').each(function(){
    			array.push(this.id);
    		});
    	return array;
    }

  _getGroupQuestionsArrayId() {
    let letters = ['A', 'B', 'C', 'D'];
    let array = [];
    for(let i = 0; i < letters.length; i++){
      let a = []; let points = 0;
      $('#group'+letters[i]).children('ul').children('li').each(function(index){
        points = 0;
        $(this).children('div.questInfo').children('input.points').each(function(index){
          console.log(this.value);
          points += parseInt(this.value);
        });
        console.log(points);
        a.push(this.id+','+points);
      });
      array.push(a);
    }
    return array;
  }

  _randomString() {
    var code = "";
    while (code.length < 8) code = Math.floor((10000000000000 * Math.random())).toString(36);
    return code.toLowerCase().substring(0, 8)
  }

  _notExists(array, code) {
    for (let i in array) {
      if (array[i] === code) return false;
    }
    return true;
  }

  _insertKeysOnStart() {
    var array = $("#keysList").val().split(";");
    var lastLetter = "A";
    var code = "";
    var html = "";
    var $keysDiv = $('#keysPanel').children('div.panel-body');
    $keysDiv.empty();
    for (let i in array) {
      code = array[i];
      if (lastLetter != code.charAt(0)) {
        lastLetter = code.charAt(0);
        $keysDiv.append('<hr />');
      }
      $keysDiv.append('<span class="keysShow">' + code + '</span> ');
    }
  }

  ///// TODO: load questions to groups and show it from json
  _insertQuestionsOnStart(){
    let strQuest = $('#questsJson').val();
    console.log(strQuest);
    let jsonQuest = JSON.parse(strQuest+ '}'); //Poprawić w LIFT }
    for(let group of jsonQuest.groups){
      console.log(group.gr);
      if(group.gr != 'A'){
        this.addNewGroup();
      }
      let grHTML = $('#group' + group.gr);
      for(let quest of group.q){
        let questItem = this._mkQuestItem(quest.id, quest.info, quest.q, quest.nr, quest.depart, quest.lev, quest.p);
        console.log(questItem);
        grHTML.children('ul').append(questItem);
      }
    }
  }
}
