package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{ Text, XML, Unparsed, NodeSeq }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import pl.brosbit.model.edu._
import pl.brosbit.lib.DataTableOption._
import pl.brosbit.lib.{ DataTable, Formater }
import mapper.By
import json.DefaultFormats
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser._
import http.js.JsCmds.Run
import org.bson.types.ObjectId
import Helpers._

case class TestJ(l: String, t: String)

class EditLesson extends BaseResourceSn {

  var idPar = S.param("id").openOr("0")
  //parametr id jest _id lekcji gdy już była utworzona. Gdy mamy nową lekcję parametr ten jest id kursu
  val lesson = LessonCourse.find(idPar).getOrElse(LessonCourse.create)
  val notFoundLesson = (lesson.courseId.toString == "000000000000000000000000" || lesson.courseId.toString.length() < 20 ) 
  val courseOption = if(notFoundLesson) None else Course.find(lesson.courseId.toString());
  //println(">>>>>>>>>>>> lessonID + " + lesson._id.toString + " idPar = " + idPar + "  courseId = " + lesson.courseId.toString );
  
  def showCourseInfo() = {
    if(courseOption.isEmpty) {
      "h2" #>  (<h2>Nie znaleziono kursu!</h2> ++ <p>Utwórz najpierw kurs, a następnie kliknij na edycję i dodaj lekcję. 
    Dopierwo wetedy możesz ją edytować</p>)
    } else  {
      var course = courseOption.get
      "h2" #> 
     (<h2>{course.title} + <span class="mutted"> - klasy: {course.classInfo}</span></h2> ++ <p>{course.descript}</p>)
    }

  }

  def editLesson() = {
    var id = ""
    var json = ""
    var title = ""
    var extraText = ""
    var descript = ""
    var nr = ""
    var department = ""

    id = idPar

    title = lesson.title
    nr = lesson.nr.toString
    descript = lesson.descript
    extraText = lesson.extraText
    department = lesson.department
    json = "[" + lesson.contents.map(cont => cont.forJSONStr).mkString(", ") + "]"

    val userId = user.id.is

    def save() {
      if (!notFoundLesson && (lesson.authorId == 0L || lesson.authorId == userId)) {
        lesson.title = title
        lesson.authorId = userId
        lesson.nr = tryo(nr.toInt).openOr(0)
        lesson.extraText = extraText
        lesson.descript = descript
        lesson.department = department
        lesson.contents = createLessonContentsList(json)
        lesson.save
      }
      S.redirectTo("/educontent/course/" + lesson.courseId.toString)
    }

    def delete() {
      if (isLessonOwner(lesson)) lesson.delete
      S.redirectTo("/educontent/course/" + lesson.courseId.toString)
    }

    val publics = List(("TAK", "TAK"), ("NIE", "NIE"))
    
    "#ID" #> SHtml.text(id, id = _) &
      "#title" #> SHtml.text(title, x => title = x.trim) &
      "#nr" #> SHtml.text(nr, nr = _) &
      "#extraText" #> SHtml.text(extraText, extraText = _) &
      "#department" #> SHtml.text(department, department = _) &
      "#description" #> SHtml.textarea(descript, descript = _) &
      "#json" #> SHtml.text(json, json = _) &
      "#save" #> SHtml.button(<span class="glyphicon glyphicon-plus-sign"></span> ++ Text("Zapisz"), save) &
      "#delete" #> SHtml.button(<span class="glyphicon glyphicon-remove-sign"></span> ++ Text("Usuń!"), 
          delete, "onclick" -> "return confirm('Na pewno usunąć całą lekcję?')")
  }

  def ajaxText = {
    def getData(what: String) = what match {
      case "quest" => {
        val str = QuizQuestion.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.question + "', '" + h.department + "']").
          mkString(",")
        "[" + str + "]"
      }
      case "word" => {
        val str = HeadWord.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.title +  "', '" + "']").
          mkString(",")
        "[" + str + "]"
      }
      case "video" => {
        val str = Video.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.title +  "', '" + h.descript + "']").
          mkString(",")
        "[" + str + "]"
      }
      case "doc" => {
        val str = Document.findAll.map(d => "['" + d._id.toString + "', '" + d.title + "', '" + d.descript + "']").
          mkString(",")
        "[" + str + "]"
      }
      case _ => "error"
    }
    "#hiddenAjaxText" #> SHtml.ajaxText("", what => {
      println("Ajax Hidden text refresh")
      Run("refreshTab(\"" + getData(what) + "\");")
    })
  }

  def renderLinkAndScript(html: NodeSeq) = DataTable.mergeSources(html)

  def dataTableScript(xhtml: NodeSeq): NodeSeq = {
    val col = List("Id", "Tytul", "Dzial")
    DataTable("#choiceTable",
      LanguageOption("pl"),
      ExtraOptions(Map("sPaginationType" -> "two_button")),
      DataOption(col, Nil),
      SortingOption(Map(1 -> Sorting.ASC)),
      DisplayLengthOption(4, List(4, 10, 20)),
      ColumnNotSearchAndHidden(List(0), List(0)))
  }
  
 // override def autocompliteScript(in:NodeSeq) = super.autocompliteScript(in)

  private def isLessonOwner(lesson: LessonCourse) = user.id.is == lesson.authorId

  private def createLessonContentsList(jsonStr: String) = {
    implicit val formats = DefaultFormats
    val json = parse(jsonStr)
    json.extract[List[LessonContent]]
  }

}
