package eu.brosbit.opos.snippet.edu


import _root_.net.liftweb._
import http.SHtml
import common._
import util._
import eu.brosbit.opos.model.edu._
import json.JsonDSL._
import Helpers._


//only for resources
class CoursesSn extends BaseResourceSn {

  def showMyCourses(): CssSel = {
    ".courseItem" #> Course.findAll("authorId" -> user.id.get).sortWith(_.title < _.title).map(course => {
      ".courseId" #> SHtml.text(course._id.toString, x => Unit) &
        "h2" #> <h2>
          {course.title}
        </h2> &
        "h3" #> <h3>
          {course.subjectName},<span class="subjectId" style="display:none;">
            {course.subjectId.toString}
          </span>
          <span class="text-muted classInfo"> Klasy:
            {course.groupsInfo}
          </span>
          <span class="classIds" style="display:none;">
            {course.groupsList.mkString(";")}
          </span>
        </h3> &
        ".courseInfo *" #> course.descript &
        ".editLessonButton [href]" #> ("/educontent/course/" + course._id.toString) &
        ".isPublic *" #> (if(course.pub) "Tak" else "Nie" )
      // ".img-responsive [src]" #> course.img
    })
  }

  def add(): CssSel = {

    var id = ""
    var title = ""
    var descript = ""
    var subjectId = ""
    var public = false
    var groupsList: List[String] = List()

    val groups = Groups.findAll("authorId" -> user.id.get).map(gr => ("_" + gr._id.toString, gr.name))


    def save() {
      //val sub = SubjectTeach.find(subjectId).getOrElse(SubjectTeach.create)
      val course = Course.find(id).getOrElse(Course.create)
      // course.img = img
      course.title = title
      course.subjectId = tryo(subjectId.toLong).openOr(0L)
      course.subjectName = findSubjectName(course.subjectId)
      course.descript = descript
      course.pub = public
      course.groupsList = groupsList
      course.groupsInfo = groups.filter(gr => groupsList.exists(gl => (gl == gr._1))).map(g => g._2).mkString(", ")
      course.authorId = user.id.get
      println(course.groupsList)
      course.save
    }

    def delete() {
      Course.find(id) match {
        case Some(course) =>
          val lessonsInCourse = LessonCourse.findAll("courseId" -> course._id.toString)
          if (lessonsInCourse.isEmpty) course.delete
        case _ =>
      }
    }

    val subjects = subjectTeach.map(sub => (sub.id.toString, sub.name))

    "#courseId" #> SHtml.text(id, id = _) &
      "#title" #> SHtml.text(title, x => title = x.trim) &
      "#subjects" #> SHtml.select(subjects, Full(subjects.head._1), subjectId = _, "autocomplete" -> "off") &
      // "#thumbnail" #> SHtml.text(img,  img = _) &
      // "#thumbnailPreview [src]" #> img &
      "#public" #> SHtml.checkbox_id(public, (x: Boolean) => public = x, Full("public")) &
      "#classesList" #> SHtml.multiSelect(groups, groupsList, groupsList = _) &
      "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
      "#save" #> SHtml.submit("Zapisz", save) &
      "#delete" #> SHtml.submit("Usuń", delete)

  }
}