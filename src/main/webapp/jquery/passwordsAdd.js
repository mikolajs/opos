class PasswordsAdd {
    constructor(){
       this.array = [0, 0, '', ''];
        this.dTable = $('#fullList').dataTable({
            "bFilter": true,
            "iDisplayLength": 40,
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
                "sLengthMenu": 'Pokaż <select>'+
                '<option value="40">40</option>'+
                '<option value="80">80</option>'+
                '<option value="120">120</option>'+
                '<option value="-1">całość</option>'+
                '</select> wierszy'

            }
        });
    }
    run(elem){
        let tr = $(elem).parent().parent();
        this.array[0] = tr.children('.id').text();
        this.array[1] = tr.children('.reversefullname').text();
        this.array[2] = tr.children('.pesel').text();
        this.array[3] = tr.children('.password').text();
        document.getElementById('id').value = this.array[0];
        document.getElementById('reversefullname').value = this.array[1];
        document.getElementById('pesel').value = this.array[2] ;
        document.getElementById('password').value = this.array[3] ;
        $('#save').trigger('click');
    }
    setNewPass(id, pass){
        console.log('set new pass ajax ' + id + ' Password: ' + pass);
        this.dTable
        var trNodes = this.dTable.fnGetNodes();
        for (let i in trNodes) {
            var data = this.dTable.fnGetData(trNodes[i]);
            if (data[0] == id) {
                data[3] = pass;
                this.dTable.fnUpdate(data, trNodes[i]);
                return;
            }
        }
    }
}