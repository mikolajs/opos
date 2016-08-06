class Marks {

    constructor() {
        var self = this;
        this.semester = 1;
        this.sem1Elem = document.getElementById('semester1');
        this.sem2Elem = document.getElementById('semester2');
        this.__switchInTable();
        this.__switchButtonSemesterDefault();
        this.sem1Elem.onclick = () => {
            this.semester = 1;
            self.changeSemester(self.sem1Elem);
        };
        this.sem2Elem.onclick = () => {
            this.semester = 2;
            self.changeSemester(self.sem2Elem);
        };
    }

    changeSemester(elem) {
       document.getElementById('semesterDisplay').innerHTML = "Semestr " + this.semester;
       this.__switchButtonSemesterDefault();
       this.__switchInTable();
    }

    openEditCol(elem) {
    }

    __switchButtonSemesterDefault(){
        if(this.semester == 1) {
            this.sem1Elem.className = 'btn btn-info';
            this.sem2Elem.className = 'btn btn-default';

        } else {
            this.sem2Elem.className = 'btn btn-info';
            this.sem1Elem.className = 'btn btn-default';
        }
    }

    __switchInTable() {
        if(this.semester == 1) {
          document.getElementById('tableSem1').style.display = "inline";
          document.getElementById('tableSem2').style.display = "none";
        }
        else {
          document.getElementById('tableSem1').style.display = "none";
          document.getElementById('tableSem2').style.display = "inline";
        }
    }

}