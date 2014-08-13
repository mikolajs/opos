package pl.brosbit.snippet.teacher

import java.util.Date
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import pl.brosbit.model._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._
import net.liftweb.http.SHtml

class MarksSn extends BaseTeacher {
    val classId = ClassChoose.is
    val idSub = S.param("idS").openOr("-1")
    val subject = SubjectName.find(idSub) match {
        case Full(sub) => sub
        case _ => SubjectName.findAll.headOption.getOrElse(SubjectName.create)
    }

    def showMarks() = {
    	if(idSub == "-1") {
    	    "table" #> <h1>Wybierz przedmiot</h1>
    	}
    	else {
    	      "thead tr" #> <tr>
                        <th>Nr</th><th>Nazwisko i imię</th>
                        {}<th>Średnia</th><th>Semestr</th>
                    </tr> &
            "tbody tr" #> <tr></tr>
    	    
    	}
    	
      
    }
    
    def showSubjects() = {
        var i = 0
        "input" #> SubjectName.findAll(By(SubjectName.scratched, false)).map(s =>  {
            val checked = if(subject.id.is == s.id.is) "checked" else ""
            val radio = "radio" + s.id.is.toString
           <input type="radio" id={radio}  checked={checked} onclick="marks.redirectSubject(this)" name="radio"  />  ++ <label for={radio}>{s.name} </label>
        })
        
    }

    def saveOneMark() = {
        var marksLineId = ""
        var onemark = ""
        var columnNr = ""
        var isProp = false

        def save() {
            val mark = Mark(new Date().getTime, user.shortInfo, onemark.trim.slice(0, 2))
                MarkLine.find(marksLineId) match {
                    case Some(markLine) => {
                         if (isProp) 
                        MarkLine.update(("_id" -> markLine._id.toString), ("$addToSet" -> ("propMark" -> mark.mapString)))
                        else {
                            val markType = "marks." + columnNr.trim 
                             MarkLine.update(("_id" -> markLine._id.toString), ("$addToSet" -> (markType -> mark.mapString)))
                        }
                        Alert("Dodano")
                    }
                    case _ => Alert("Błąd, nie można dodać oceny!")
                }
        }

        val form = "#marksLineId" #> SHtml.text(marksLineId, marksLineId = _) &
            "#isProp" #> SHtml.checkbox(isProp, isProp = _) &
            "#onemark" #> SHtml.text(onemark, onemark = _) &
            "#columnNr" #> SHtml.text(columnNr, columnNr = _) &
            "#saveMark" #> SHtml.submit("OK", save) andThen SHtml.makeFormsAjax

        "#form" #> (in => form(in))
    }

    def saveSemestrMark() = {
        var semmark = ""
        var marksLineId = ""

        def save() {
            val mark = Mark(new Date().getTime, user.shortInfo, semmark)
            MarkLine.find(marksLineId) match {
                case Some(markLine) => {
                    MarkLine.update(("_id" -> markLine._id.toString), ("$addToSet" -> ("semMark" -> mark.mapString)))
                    Alert("Dodano")
                }
                case _ => Alert("Błąd, nie można dodać oceny!")
            }
        }
        val marks = (1 to 6).map(i => (i.toString, i.toString))

        val form = "#markslineId" #> SHtml.text(marksLineId, marksLineId = _) &
            "#semmark" #> SHtml.select(marks, Full(semmark), semmark = _) &
            "#saveMarkSem" #> SHtml.ajaxSubmit("OK", save) andThen SHtml.makeFormsAjax

        "form" #> (in => form(in))

    }

    def saveTableHeader() = {
        var subjectId = ""
        var columnId = ""
        var semestr = ""
        var symbol = ""
        var userId = ""
        var weight = "1"

        def save() {

        }

        "#subjectIdCol" #> SHtml.text(subjectId, subjectId = _) &
            "#columnNrCol" #> SHtml.text(columnId, columnId = _) &
            "#semestrCol" #> SHtml.text(semestr, semestr = _) &
            "#symbol" #> SHtml.text(symbol, symbol = _) &
            "#weight" #> SHtml.text(weight, weight = _) &
            "#userIdCol" #> SHtml.text(userId, userId = _) &
            "#saveHeader" #> SHtml.submit("OK", save)
    }
}
