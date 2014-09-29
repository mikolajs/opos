package pl.brosbit.snippet.view

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
        if(!canView) S.redirectTo("/view/courses")
        
        if (course.title != "") {
            "#subjectListLinks a" #> createLessonList &
            "#courseInfo" #> <div class="alert alert-success"><h2>{course.title}</h2><p>{course.descript}</p></div> &
                ".content *" #> this.showAsDocument(currentLesson, false)
        } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
    }

     
     private def canView = (course.authorId == user.id.is || course.classList.exists(x => x == user.classId.is))


}