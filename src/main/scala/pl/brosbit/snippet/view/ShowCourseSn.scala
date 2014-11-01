package pl.brosbit.snippet.view

import java.util.{Date}
import scala.xml.{ Text}
import _root_.net.liftweb._
import http.{ S, SHtml }
import util._
import pl.brosbit.model._
import pl.brosbit.model.page.Comment
import json.JsonDSL._
import org.bson.types.ObjectId
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
      
      var message = ""
      def send() {
        if(canView) {
          val messageTeacher = Messages.findAll(("ownerId"->course.authorId)).headOption.getOrElse(Messages.create)
          if(messageTeacher.ownerId == 0L) {
            messageTeacher.ownerId = course.authorId
            messageTeacher.save
          }
          message = "<p>Widomość z kursu " + course.getInfo + " lekcja: " + currentLesson.title + "</p>"
          val comment = Comment(ObjectId.get(),user.getFullName, user.id.is.toString, Formater.formatTime(new Date()), message)
          Messages.update(("ownerId"->course.authorId), ("$addToSet"-> ("mes" -> comment.mapString)))
        }
      }
      
      "#writeMessage" #> SHtml.textarea(message, message = _) &
      "#sendMessage" #> SHtml.button(<span class="glyphicon glyphicon-send"></span> ++ Text("Wyślij"), send)
    }
     
     private def canView = (course.authorId == user.id.is || course.classList.exists(x => x == user.classId.is))

}