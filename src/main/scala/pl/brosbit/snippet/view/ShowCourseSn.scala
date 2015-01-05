package pl.brosbit.snippet.view

import java.util.{Date}
import scala.xml.{ Text}
import _root_.net.liftweb._
import http.{ S, SHtml }
import util._
import pl.brosbit.model._
import Helpers._
import pl.brosbit.snippet.BaseShowCourseSn
import pl.brosbit.lib.Formater

class ShowCourseSn extends BaseShowCourseSn {
  
   
    def show() = {
        if(!canView) S.redirectTo("/view/courses")
        
        if (course.title != "") {
            "#subjectListLinks a" #> createLessonList &
            "#courseInfo" #> <div class="alert alert-success"><h2>{course.title}</h2><p>{course.descript}</p></div> &
                ".content *" #> this.showAsDocument(currentLesson, false)
        } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
    }

    def sendMessage() = {
      
      var msg = ""
      def send() {
        if(canView) {
          val message = Message.create
          val body = "<p class=\"msq-body\">" + msg + "</p><p><small class=\"msgSource\">Widomość z kursu " +
              course.getInfo + " lekcja: " + currentLesson.title + "</small></p>"
          val d = new Date()
          val mc= MessageChunk(user.id.is.toString, user.getFullName, Formater.formatTime(d), body)
          message.lastDate = d.getTime
          message.who = List(course.authorId, user.id.is)
          message.body = List(mc)
          message.save
        }
      }
      
      "#writeMessage" #> SHtml.textarea(msg, msg = _) &
      "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text("Wyślij"), send)
    }
     
     private def canView = (course.authorId == user.id.is || course.classList.exists(x => x == user.classId.is))

}