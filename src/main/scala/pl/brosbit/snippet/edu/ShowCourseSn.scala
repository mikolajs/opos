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

  override val basePath = "/educontent/course/"

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
    "li" #> course.chapters.map(ch => <li class="list-group-item">{ch}</li>)
  }
  
  def sortChapters() = {
    var sorted = ""
    def delete() {
      val allChapters = lessons.map(l => l.chapter).distinct
      course.chapters = course.chapters.filter(ch => allChapters.exists(all => all == ch))
      course.save
    }
    def save() {
      val chaps = sorted.split("\\|\\|").map(s => s.trim).filter(s => s.length < 1).toList
      //println("[AppInfo:::: " + chaps.length + " : " + chaps.mkString("||"))
      if(chaps.length == course.chapters.length){
        course.chapters = chaps 
        course.save
      }
    }
    "#sortedChaptersData" #> SHtml.text(sorted, sorted = _, "style"-> "display:none;") &
    "#saveSort" #> SHtml.submit("Zapisz", save) &
    "#deleteNotUsed" #> SHtml.submit("Czyść puste", delete)
  }
  
  def showComments() = {
    
  }
  
  def addComment() = {
    
  }
  
  def slideData = {

  }


}
