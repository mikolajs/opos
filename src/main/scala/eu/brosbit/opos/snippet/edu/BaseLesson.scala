package eu.brosbit.opos.snippet.edu

import eu.brosbit.opos._
import model._
import edu._
import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import common._
import util._
import http.{S, SHtml}
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

trait BaseLesson {

  val user = User.currentUser.openOrThrowException("Niezalogowany nauczyciel")
  val levList = List(("1", "podstawowy"), ("2", "średni"), ("3", "rozszerzony"))
  val levMap = levList.toMap

  var idPar = S.param("id").openOr("0")
  var courseId = S.param("c").openOr("0")
  //parametr id jest _id lekcji gdy już była utworzona. Gdy mamy nową lekcję parametr ten jest id kursu
  val lesson = if (idPar == "0") LessonCourse.create else LessonCourse.find(idPar).getOrElse(LessonCourse.create)
  val notFoundLesson = (lesson.courseId.toString == "000000000000000000000000" || lesson.courseId.toString.length() < 20)
  val courseOption = if (idPar == "0") Course.find(courseId)
  else {
    if (notFoundLesson) None else Course.find(lesson.courseId.toString())
  }
  val subjectId = if (courseOption.isEmpty) 0L else courseOption.get.subjectId
  val chapters = if (courseOption.isEmpty) Nil else courseOption.get.chapters
  val chaptersList = chapters.map(ch => (ch, ch))


  def findChapterName(id: Int) = if (chapters.contains(id)) chapters(id) else "Brak nazwy"

  //println(">>>>>>>>>>>> lessonID + " + lesson._id.toString + " idPar = " + idPar + "  courseId = " + lesson.courseId.toString );
}