package pl.edu.osp.snippet.edu


import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import pl.edu.osp.model._
import edu._
import pl.edu.osp.model.edu._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._


//only for resources
class CoursesSn extends BaseResourceSn {


  def showMyCourses() = {
    ".courseItem" #> Course.findAll("authorId" -> user.id.get).map(course => {
      ".courseId" #> SHtml.text(course._id.toString, x => Unit) &
        "h2" #> <h2>
          {course.title}
        </h2> &
        "h3" #> <h3>
          {course.subjectName},<span class="subjectId" style="display:none;">
            {course.subjectId.toString}
          </span>
          <span class="text-muted classInfo"> Klasy:
            {course.classInfo}
          </span>
          <span class="classIds" style="display:none;">
            {course.classList.mkString(";")}
          </span>
        </h3> &
        ".courseInfo *" #> course.descript &
        ".editLessonButton [href]" #> ("/educontent/course/" + course._id.toString) &
        ".isPublic *" #> (if(course.pub) "Tak" else "Nie" )
      // ".img-responsive [src]" #> course.img
    })
  }

  def add() = {

    var id = ""
    // var img = ""
    var title = ""
    var descript = ""
    var subjectId = ""
    var subjectName = ""
    var public = false
    var classInfo = ""
    var classesList: List[String] = List()

    val classesWithLong = ClassModel.findAll().filter(!_.scratched.get).map(theClass => (theClass.id.get, theClass.classString()))
    val classes = classesWithLong.map(c => (c._1.toString(), c._2))

    def save() {
      val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")

      val sub = SubjectTeach.find(subjectId).getOrElse(SubjectTeach.create)

      val course = Course.find(id).getOrElse(Course.create)

      // course.img = img
      course.title = title
      course.subjectId = tryo(subjectId.toLong).openOr(0L)
      course.subjectName = findSubjectName(course.subjectId)
      course.descript = descript
      course.pub = public
      course.classList = classesList.map(s => tryo(s.toLong).openOr(0L))
      course.classInfo = classesWithLong.filter(cl => course.classList.exists(c => (c == cl._1))).map(c => c._2).mkString(", ")
      course.authorId = user.id.get
      course.save
    }

    def delete() {
      Course.find(id) match {
        case Some(course) => {
          val lessonsInCourse = LessonCourse.findAll("courseId" -> course._id.toString)
          if (lessonsInCourse.length == 0) {
            course.delete
          }
        }
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
      "#classesList" #> SHtml.multiSelect(classes, classesList, classesList = _) &
      "#descript" #> SHtml.textarea(descript, x => descript = x.trim) &
      "#save" #> SHtml.submit("Zapisz", save) &
      "#delete" #> SHtml.submit("Usuń", delete)

  }
}