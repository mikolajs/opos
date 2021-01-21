package eu.brosbit.opos.snippet.view

import _root_.net.liftweb._
import util._
import eu.brosbit.opos.model._
import eu.brosbit.opos.model.edu._
import json.JsonDSL._
import Helpers._

//only for resources
class CoursesSn {

  private val user = User.currentUser.openOrThrowException("Uczeń musi być zalogowany!")
  private val groups = Groups.findAll.filter(gr => gr.students.exists(s => s.id == user.id.get)).map("_" + _._id)
  def showMyCourses():CssSel = {
    ".courseItem" #> Course.findAll("groupsList" -> ("$in" -> groups)).map(course => {
      "h2" #> <h2>
        {course.title}
      </h2> &
        "h3" #> <h3>
          {course.subjectName}<span class="text-muted classInfo">, Grupy:
            {course.groupsInfo}
          </span>
        </h3> &
        ".courseInfo *" #> course.descript &
        ".courseLink [href]" #> ("/view/course/" + course._id.toString)
    })
  }
}