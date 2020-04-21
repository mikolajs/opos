package eu.brosbit.opos.snippet.edu

//import scala.xml.{Text, Unparsed, NodeSeq}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import eu.brosbit.opos.model._
import edu._
import Helpers._
import json.JsonDSL._
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.{Formater, ZeroObjectId}
import java.util.Date


class EditWorkSn extends BaseResourceSn {

  var workId = S.param("id").openOr("0")
  val userId = user.id.get
  val work = LessonWork.find(workId).getOrElse(LessonWork.create)
  val lessonOpt = LessonCourse.find(work.lessonId)
  val classes = ClassModel.findAll().map(cl => (cl.id.toString, cl.classString()))
  val courses = Course.findAll("authorId"->userId).map(co => (co._id.toString, co.title))
  var courseId =lessonOpt.map(_.courseId).getOrElse(ZeroObjectId.get).toString
  var lessonId = lessonOpt.map(_._id).getOrElse(ZeroObjectId.get).toString
  val lessons = LessonCourse.findAll("courseId"-> courseId.toString).map(le => (le._id.toString, le.title))

  //working ....
  def editWork() = {

    var quizzes = lessonOpt.map(lesson => lesson.contents.filter(p => p.what == "q").map(p => p.id)).mkString(";")
    var startWork = Formater.strForDateTimePicker(if(work.start == 0L) new Date() else new Date(work.start))
    var endWork = Formater.strForDateTimePicker(if(work.end == 0L) new Date() else new Date(work.end))
    var classId = work.classId.toString
    var lessonTitle = lessonOpt.map(lesson => lesson.title).getOrElse("")
    var lessonId = lessonOpt.map(_._id).getOrElse(ZeroObjectId.get).toString
    var courseId =lessonOpt.map(_.courseId).getOrElse(ZeroObjectId.get).toString



    def save()  {
      println("========= save lesson work ========")
      if (work.teacherId != 0L && work.teacherId != userId) return
      if (work.teacherId == 0L) work.teacherId = userId
      work.lessonId = new ObjectId(lessonId)
      work.lessonTitle = lessonTitle
      work.start = Formater.fromStringDataTimeToDate(startWork).getTime
      work.end = Formater.fromStringDataTimeToDate(endWork).getTime
      work.classId = tryo(classId.toLong).getOrElse(0)
      work.className = classes.filter(c => c._1 == classId) match {
        case cl :: rest => cl._2
        case _ => "Brak"
      }
      work.save
      S.redirectTo("/educontent/editwork/" + work._id.toString)
    }

    def delete() {
      println("========= delete work ========")
      if (work.teacherId != 0L || userId == work.teacherId) {
        //dodać wyszukiwanie prac uczniów i informację o konieczności ich skasowania
//        LessonWorkAnswer.findAll(("exam" -> examId)).foreach(exAn => {
//          println(exAn.authorName)
//          exAn.delete
//        })
//        work.delete
        S.redirectTo("/educontent/works")
      } else S.warning("Nie jesteś autorem prac lub pracy jeszcze nie ma.")
    }

      "#coursesId" #> SHtml.select(courses, Full(courseId), courseId = _) &
      "#lessonId" #> SHtml.select(lessons, Full(lessonId), lessonId = _ ) &
      "#classWork" #> SHtml.select(classes, Full(classId), classId = _ ) &
      "#startWork" #> SHtml.text(startWork, x =>  startWork = x.trim) &
      "#endWork" #> SHtml.text(endWork, x =>  endWork = x.trim) &
      "#saveWork" #> SHtml.submit("Zapisz", save) &
      "#deleteWork" #> SHtml.submit("Usuń", delete)

  }

  def choiceLesson() = {
    def choice(): Unit = {
      S.redirectTo(s"/educontent/editwork/0?c=$courseId")
    }
      ".choiceCourse" #> SHtml.select(courses, Full(courseId), courseId = _) &
      ".changeLesson" #> SHtml.submit("Wybierz", choice)


  }


  def showQuestions() = {

    ".questList" #> QuizQuestion.findAll(("authorId" -> user.id.get) ~ ("_id" -> ("$in" ->
      lessonOpt.map(lesson => lesson.contents.filter(p => p.what == "q")
        .map(p => p.id)).map(_.toString)))).map(quest => {
      "div [id]" #> quest._id.toString &
        ".contentQuest * " #> quest.question
    })
  }


}