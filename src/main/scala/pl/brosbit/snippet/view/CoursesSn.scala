package pl.brosbit.snippet.view

import scala.xml.{Text,XML,Unparsed}
import _root_.net.liftweb._
import http.{S,SHtml}
import common._
import util._
import mapper.{OrderBy,Descending}
import pl.brosbit.model._
import edu._
import pl.brosbit.model.edu._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


//only for resources
class CoursesSn {
  
   val user = User.currentUser.openOrThrowException("Uczeń musi być zalogowany!")

	def showMyCourses() = {
	  //poprawić na wyszukiwanie dla konkretnego ucznia
        ".courseItem" #> Course.findAll(("classList" -> ( "$in" -> List(user.classId.is)))).map(course => {
            "h2" #> <h2>{ course.title } </h2> &
            "h3" #> <h3>{course.subjectName}  
            			 <span class="text-muted classInfo">, Klasy: { course.classInfo } </span>
            		</h3> &
                ".courseInfo *" #> course.descript &
                ".courseLink [href]" #> ("/view/course/" + course._id.toString)
        })
	}
}