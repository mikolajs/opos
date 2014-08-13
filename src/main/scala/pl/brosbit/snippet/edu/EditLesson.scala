package pl.brosbit.snippet.edu

import java.util.Date
import scala.xml.{Text,XML,Unparsed, NodeSeq}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import pl.brosbit.model.edu._
import pl.brosbit.lib.DataTableOption._
import pl.brosbit.lib.{DataTable, Formater}
import mapper.By
import json.DefaultFormats
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser._
import http.js.JsCmds.Run
import org.bson.types.ObjectId
import Helpers._

case class TestJ(l:String, t:String)

class EditLesson extends BaseResourceSn {
     
 var idPar = S.param("id").openOr("0")
 val lesson = LessonCourse.find(idPar).getOrElse(LessonCourse.create)
        
    def editLesson() = {
        var id = ""
        var json = ""
        var title = ""
        var extraText = ""
        var descript = ""
        var courseId = ""
        var nr = ""
            
        id = idPar
        
        title = lesson.title
        courseId = if(lesson.courseId.toString != "000000000000000000000000") lesson.courseId.toString else ""
        nr = lesson.nr.toString
        descript = lesson.descript
        extraText = lesson.extraText
        json = "[" + lesson.contents.map(cont => cont.forJSONStr).mkString(", ") + "]"
        
        val userId = user.id.is   
        
        def save() {
            
            if(lesson.authorId == 0L || lesson.authorId == userId){
                 lesson.title = title
                 lesson.authorId = userId
                 lesson.nr = tryo(nr.toInt).openOr(0)
                 lesson.extraText = extraText
                 lesson.descript = descript
                 Course.find(courseId) match {
                     case Some(cour) => {
                         lesson.courseId = cour._id                      
                         }
                     case _ => println("BŁĄD nieznaleziono kursu") 
                 }
                lesson.contents = createLessonContentsList(json)
                lesson.save 
            }   
            S.redirectTo("/resources/course/" + lesson.courseId.toString)
        }
        
        def delete() {
           if(isLessonOwner(lesson)) lesson.delete
           S.redirectTo("/resources/course/" + lesson.courseId.toString)
        }
        
      
        
        val publics = List(("TAK","TAK"),("NIE","NIE"))
        val courses = Course.findAll("authorId" -> userId).map(c => (c._id.toString, c.getInfo))
         /* def getData(what:String) = {
            if(what == "quest") {
                 val  str = QuizQuestion.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.question + "', '"  +  h.departmentInfo  + "']").
                 mkString(",")
                 "[" + str + "]"
            } 
            else {
                 val  str = HeadWord.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.title + "', '"  +  h.departmentInfo  + "']").
                 mkString(",")
                 "[" + str + "]"
            }
        }*/
        "#ID" #> SHtml.text(id, id = _) &
        "#title" #> SHtml.text(title, x => title = x.trim) &
        "#nr" #> SHtml.text(nr, nr = _) &
        "#courses" #> SHtml.select(courses, Full(courseId), courseId = _) &
        "#extraText" #> SHtml.text(extraText, extraText = _) &
        //"#forDataTable" #> SHtml.text(getData("word"), _ => Unit) &
        "#description" #> SHtml.text(descript, descript = _) &
        "#json" #> SHtml.text(json, json = _) &
        "#save" #> SHtml.submit("Zapisz", save ) &
        "#delete" #> SHtml.submit("Usuń!", delete, "onclick"->"return confirm('Na pewno usunąć całą lekcję?')" ) 
    }
    
    def ajaxText =  {
          def getData(what:String) = what match {
              case "quest" => {
                  val  str = QuizQuestion.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.question + "', '"  +  h.department + "']").
                 mkString(",")
                 "[" + str + "]"
              }
              case "word" => {
                  val  str = HeadWord.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.title +  "']").
                 mkString(",")
                 "[" + str + "]"
              }
              case "video" => {
                  val  str = Video.findAll.map(h => "[ '" + h._id.toString + "',  '" + h.title +  "']").
                 mkString(",")
                 "[" + str + "]"
              }
              case "doc" => {
                  val str = Document.findAll.map(d => "['" + d._id.toString + "', '" + d.title +  "']"  ).
                  mkString(",")
                  "[" + str + "]"
              }
              case _ => "error"
          }
        "#hiddenAjaxText" #> SHtml.ajaxText("",  what => {
            	println("Ajax Hidden text refresh")
            	Run("refreshTab(\"" +  getData(what) +  "\");"  )})
    }
            
     def renderLinkAndScript(html:NodeSeq) = DataTable.mergeSources(html)
    
    def dataTableScript(xhtml: NodeSeq) :NodeSeq = {
        val col = List("Id", "Tytul", "Dzial")
    DataTable("#choiceTable",
            LanguageOption("pl"),  
            ExtraOptions(Map("sPaginationType" -> "two_button")),
            DataOption(col, Nil),
            SortingOption(Map( 1 -> Sorting.ASC)), 
            DisplayLengthOption(4, List(4,10, 20)),
           ColumnNotSearchAndHidden(List(0), List(0))
            )
  }
    
    
    private def isLessonOwner(lesson:LessonCourse) = user.id.is == lesson.authorId
    
    
    private def createLessonContentsList(jsonStr:String) = {
        implicit val formats = DefaultFormats
        val json = parse(jsonStr)
        json.extract[List[LessonContent]]
    }
    
    
}