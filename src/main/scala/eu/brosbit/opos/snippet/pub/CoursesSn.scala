package eu.brosbit.opos.snippet.pub

import eu.brosbit.opos.model.edu._
import net.liftweb.json.JsonDSL._
import _root_.net.liftweb.util.Helpers._

class CoursesSn {

  def showAllowedCourses() = {
    ".courseItem" #> Course.findAll((("pub" -> true))).map(course => {
      "h2" #> <h2>
        {course.title}
      </h2> &
        "h3" #> <h3>{course.subjectName}</h3> &
        ".courseInfo *" #> course.descript &
        ".courseLink [href]" #> ("/public/course/" + course._id.toString)
    })
  }

}