var EditExam = dejavu.Class.declare({

  keysNum: 48,

  initialize: function() {
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
    this._removeExistsTests();
    this._insertKeysOnStart();
  },



  appendTest: function(elem) {
    $ol = $("ol.groupsExam");

    if ($ol.children('li').length >= 4) {
      alert("Można utworzyc tylko cztery grupy");
      return;
    };
    var $li = $(elem);
    var title = $li.children('span.titleQuiz').text();
    var id = $li.attr('id');
    var li = '<li class=""  id="' + id +
      '"><span class="titleQuiz" >' + title + '</span>' +
      '<span class="glyphicon glyphicon-remove btn text-danger"' +
      ' onclick="editExam.removeTest(this);"></span></li>';
    $ol.append(li);
    $li.remove();
  },

  removeTest: function(elem) {
    var $li = $(elem).parent();
    var title = $li.children('span.titleQuiz').text();
    var id = $li.attr('id');
    var li = '<li class="list-group-item btn" onclick="editExam.appendTest(this);"  id="' + id +
      '"><span class="glyphicon glyphicon-plus addItem"></span>' +
      '<span class="titleQuiz" >' + title + '</span></li>';
    $("ul.examsList").append(li);
    $li.remove();
  },

  prepareData: function() {
    var array = this._getTestArrayId()
    $('#testsList').val(array.join(';'));
    //console.log($('#testsList').val());
    var descr = $('#descriptionExam').val();
    if (descr.length < 3) {
      alert("Zbyt krótki opis (min 3 litery)");
      return false;
    }

    if (array.length < 1 || array.length > 4) {
      alert("Grup może być od jednej do czterech");
      return false;
    }
    return true;
  },


  genRandomStrings: function() {
    if ($('#keysList').val().length > 8 && !confirm("Generując nowe klucze kasujesz stare! Kasować?")) return;
    var groups = $('ol.groupsExam').children('li').length;
    if (groups < 2) {
      alert("Kody generuje się dla większej ilości grup");
      return;
    }
    var $keysDiv = $('#keysPanel').children('div.panel-body');
    $keysDiv.empty();
    var array = [];
    var letters = "ABCD";

    var onGroups = this.keysNum / groups;
    var letter = '';
    var code = "";
    var tmpArray = [];
    for (var g = 0; g < groups; g++) {
      letter = letters.charAt(g);
      for (var j = 0; j < onGroups; j++) {
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
  },

  print: function() {
    window.print();
  },

  _getTestArrayId: function() {
    var array = [];
    $('ol.groupsExam').children('li').each(function() {
      array.push(this.id);
    });
    return array;
  },

  _randomString: function() {
    var code = "";
    while (code.length < 8) code = Math.floor((10000000000000 * Math.random())).toString(36);
    return code.toLowerCase().substring(0, 8)
  },

  _notExists: function(array, code) {
    for (i in array) {
      if (array[i] === code) return false;
    }
    return true;
  },

  _removeExistsTests: function() {
    var array = this._getTestArrayId();
    $("ul.examsList").children("li").each(function() {
      for (i in array)
        if (array[i] == this.id) {
          $(this).remove();
        }
    });
  },

  _insertKeysOnStart: function() {
    var array = $("#keysList").val().split(";");
    var lastLetter = "A";
    var code = "";
    var html = "";
    var $keysDiv = $('#keysPanel').children('div.panel-body');
    $keysDiv.empty();
    for (i in array) {
      code = array[i];
      if (lastLetter != code.charAt(0)) {
        lastLetter = code.charAt(0);
        $keysDiv.append('<hr />');
      }
      $keysDiv.append('<span class="keysShow">' + code + '</span> ');
    }

  }

});
