package pl.edu.osp.snippet.register

import java.util.Date
import net.liftweb.util.Helpers._
import _root_.net.liftweb.http.{SHtml, S}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import pl.edu.osp.model._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._
import net.liftweb.http.SHtml
import pl.edu.osp.lib.Formater

class MarksSn extends BaseTeacher {
  val classId = ClassChoose.is
  val idSub = S.param("idS").openOr("-1")
  val subject = SubjectName.find(idSub) match {
    case Full(sub) => sub
    case _ => SubjectName.findAll.headOption.getOrElse(SubjectName.create)
  }
  val pupils = User.findAll(By(User.classId, classId), By(User.role, "u"), OrderBy(User.classNumber, Ascending))
  val pupilIDs = pupils.map(p => p.id.get)
  val marks = MarkLine.findAll(("subjectId" -> subject.id.get)~("pupilId" -> ( "$in" -> pupilIDs)))
  val data = pupils.map(p => {
    (p.classNumber, p.getFullNameReverse)
    val markLine = marks.find(m => m.pupilId == p.id.get).getOrElse(MarkLine.create)
    (p.classNumber.get, p.getFullNameReverse, markLine)
  })

  def showMarks() = {

     println("=============")
     println("pupils size: " + pupils.length.toString)
     println("marks size: " + pupils.length.toString)
      ".trHeadSem1" #> <tr><th>Nr</th> <th>Nazwisko i imię</th>{produceCols()}<th>Śred.</th> <th>Sem 1</th></tr> &
      ".trHeadSem2" #> <tr><th>Nr</th> <th>Nazwisko i imię</th>{produceCols()}<th>Śred.</th> <th>Sem 2</th> </tr> &
      ".trBodySem1" #> data.map(d =>
        <tr><td> {d._1} </td> <td> {d._2} </td>
        { d._3.marksSem1.map(ms => marksToHTML(ms) )}<td></td>
        {marksToHTML(d._3.sem1End)} </tr>) &
    ".trBodySem2" #> data.map(d =>
      <tr><td> {d._1} </td> <td> {d._2} </td>
        { d._3.marksSem2.map(ms => marksToHTML(ms) )}<td></td>
        {marksToHTML(d._3.sem2End)} </tr> )
}
  def showSubjects() = {
    "button" #> SubjectName.findAll(By(SubjectName.scratched, false)).map(s => {
      val setClass = if (subject.id.get == s.id.get) "btn btn-info" else "btn btn-default"
      <a href={"/register/marks/" + s.id.toString} class ={setClass}>{s.name}</a>
    })

  }

  def getInfo() = {
      "#infoClass *" #> ClassString.get &
      "#infoSubject *" #> subject.name
  }
/*
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
*/

  def editColumn() = {
    var subjectId = ""
    var columnId = ""
    var color = ""
    var symbol = ""
    var info = ""
    var userNr = ""
    var weight = "1"

    def save() {

    }

    "#subjectIdCol" #> SHtml.text(subjectId, subjectId = _) &
      "#columnNrCol" #> SHtml.text(columnId, columnId = _) &
      "#colorCol" #> SHtml.text(color, color = _) &
      "#infoCol" #> SHtml.text(info, info = _) &
      "#symbol" #> SHtml.text(symbol, symbol = _) &
      "#weight" #> SHtml.text(weight, weight = _) &
      "#userNrCol" #> SHtml.text(userNr, userNr = _) &
      "#saveHeader" #> SHtml.submit("OK", save)
  }


  private def marksToHTML(marks: List[Mark]) = {
    marks match {
      case Nil => <td></td>
      case ms => {
        <td title={marks.map(marksToHTMLHelps(_)).mkString("\n")} >{ms.head.mark}</td>
      }
    }
  }

  private def marksToHTMLHelps(mark:Mark) =
    mark.mark + "  nauczyciel: " + mark.teacher + " data: " + Formater.formatTime(new Date(mark.time))

  private def produceCols() = (1 to 25).toList.map(n => <th>{n.toString}</th>)
}
