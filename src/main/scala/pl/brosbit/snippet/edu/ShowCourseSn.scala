package pl.brosbit.snippet.edu

import java.util.Date
import java.util.Random
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import pl.brosbit.snippet.BaseShowCourseSn

class ShowCourseSn extends BaseShowCourseSn {


  def show() = {
    if (course.authorId != user.id.is) S.redirectTo("/educontent/index")

    if (course.title != "") {
      "#subjectListLinks a" #> super.createLessonList &
      "#addLessonButton [href]" #> ("/educontent/editlesson/0?c=" +  course._id.toString()) &
        "#courseInfo" #> <div class="alert alert-success"><h2>{ course.title }</h2><p>{ course.descript }</p></div> &
        ".content *" #> super.showAsDocument(currentLesson, true)
    } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
  }


  def sortedChapters() = {
    course.chapters.map(ch => "li" #> <li class="list-group-item">{ch}</li>)
  }
  
  def sortChapters() = {
    "#n" #> ""
  }
  
  def slideData = {

  }


}
