package eu.brosbit.opos.snippet.edu

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

import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.{Run, SetHtml, SetValById}

import scala.xml.Unparsed


class EditWorkSn extends BaseResourceSn {

  val userId = user.id.get
  val courses = Course.findAll("authorId"->userId).map(co => (co._id.toString, co.title))
  val classes = ClassModel.findAll().map(cl => (cl.id.toString, cl.classString()))

  val workId = S.param("id").openOr("0")
  val work = Work.find(workId).getOrElse(Work.create)

  var courseId = if( work.teacherId == 0L) courses.head._1 else  work.courseId.toString
  val lessons = LessonCourse.findAll("courseId"-> courseId.toString).map(le => (le._id.toString, le.title))
  var lessonId = if(work.teacherId == 0L) lessons.head._1 else work.lessonId.toString
//  val lesson = LessonCourse.find(lessonId).getOrElse(LessonCourse.create)

  //working ....
  def editWork() = {

    var startWork = Formater.strForDateTimePicker(if(work.start == 0L) new Date() else new Date(work.start))
    var endWork = Formater.strForDateTimePicker(if(work.end == 0L) new Date() else new Date(work.end))
    var classId = work.classId.toString
    var info = work.description

    def save()  {
      //println("========= save lesson work ========")
      if (work.teacherId != 0L && work.teacherId != userId) return
      if (work.teacherId == 0L) work.teacherId = userId
      work.lessonId = new ObjectId(lessonId)
      work.lessonTitle = LessonCourse.find(work.lessonId).map(_.title).getOrElse("Nie ma takiej lekcji!")
      work.courseId = new ObjectId(courseId)
      val (id, name) = Course.find(courseId).map(l => (l.subjectId, l.subjectName)).getOrElse((0L, ""))
      work.subjectId = id
      work.subjectName = name
      work.description = info
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
      //println("========= delete work ========")
      if (work.teacherId != 0L || userId == work.teacherId) {
        //dodać wyszukiwanie prac uczniów i informację o konieczności ich skasowania
        WorkAnswer.findAll("work" -> work._id.toString).foreach(workAn => {
//          println(exAn.authorName)
          workAn.delete
        })
        work.delete
        S.redirectTo("/educontent/works")
      } else S.warning("Nie jesteś autorem prac lub pracy jeszcze nie ma.")
    }

      "#coursesWork" #> SHtml.select(courses, Full(courseId), courseId = _, "onchange" -> "editWork.refreshedCourse();") &
      "#lessonWorkSelect" #> SHtml.select(lessons, Full(lessonId), (l) => Unit , "onchange" -> "editWork.refreshedLesson();") &
      "#lessonWorkId" #> SHtml.text(lessonId, lessonId = _) &
      "#classWork" #> SHtml.select(classes, Full(classId), classId = _ ) &
      "#infoWork" #> SHtml.textarea(info, x => info = x.trim ) &
      "#startWork" #> SHtml.text(startWork, x =>  startWork = x.trim) &
      "#endWork" #> SHtml.text(endWork, x =>  endWork = x.trim) &
      "#saveWork" #> SHtml.submit("Zapisz", save) &
      "#deleteWork" #> SHtml.submit("Usuń", delete)

  }

  def showLessons(): CssSel = {

    def process(cId: String):JsCmd = {
      val less = LessonCourse.findAll("courseId" -> cId).map(l => (l._id.toString, l.title))
      val html = less.map(l => <option value={l._1}>{l._2}</option>)
      SetHtml("lessonWorkSelect", html) & Run("editWork.refreshedLesson();")
    }

    "#courseWorkId" #> SHtml.ajaxText(courseId, process(_))
  }

  def showQuestions(): CssSel = {

    def process(lessId: String): JsCmd = {
      val less  = LessonCourse.find(lessId).map(_.contents.filter(p => p.what == "q")).getOrElse(Nil)
      val html = <div>{less.map(q => <div id={q.id} class="msg-grp msg-blue"> {q.title} <br/> {Unparsed(q.descript)} </div>)}</div>
      //println(s"set quizzes in lessonId = $lessId:\n" + html.toString())
      SetHtml("questList", html)
    }
    "#triggerLesson" #> SHtml.ajaxText(lessonId, process(_))
  }


}