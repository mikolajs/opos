package eu.brosbit.opos.snippet.edu


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.S
import eu.brosbit.opos.model.edu.{Exam, ExamAnswer, LessonWork, LessonWorkAnswer, Quiz}
import eu.brosbit.opos.lib.Formater
import java.util.Date

import eu.brosbit.opos.model.User

class ShowWorksSn extends BaseResourceSn {

  val exId = S.param("id").openOr("")
  if(exId.isEmpty) S.redirectTo("/educontent/works")
  val work = LessonWork.find(exId).getOrElse(LessonWork.create)

  def showInfo() = {
    "span *" #> work.className &
    "h3 *" #> work.lessonTitle &
      "small *" #> ("od " + Formater.formatTime(new Date(work.start)) +
        " do " + Formater.formatTime(new Date(work.end)))
  }

  //TODO: change to Works
  def showExamAnswers() = {
    val workAns = LessonWorkAnswer.findAll("exam" -> work._id.toString)

    "tr" #>  workAns.map(ea => {
      val gain = if(!ea.answers.isEmpty) ea.answers.map(_.p).reduce((f, n) => f + n) else 0
      val percent = if(ea.max == 0) 0.0F else ((100.0F* gain.toFloat) / ea.max.toFloat)
      ".col1 *" #> ea.authorName &
        ".col2 *" #> ea.code &
        ".col3 *" #> (gain.toString + " / " + ea.max.toString  + " : " +
          scala.math.round(percent).toString  + "%") &
        ".col4 *" #> <a href={"/educontent/checkexam/" + ea._id.toString}
                        class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Sprawdź</a>

    })

  }

  def showWorks() = {
    val userId = User.currentUser.map(_.id.get).getOrElse(-1L)
    ".col-lg-4"  #> LessonWork.findAll(("teacherId" -> userId), ("start" -> -1)).map(work => {
      ".col1 *" #> work.lessonTitle &
        ".col2 *" #> work.className &
        ".col3 *" #> Formater.formatTime(new Date(work.start)) &
        ".col4 *" #> Formater.formatTime(new Date(work.end)) &
        ".col5 *" #> <a href={"/educontent/showworks/" + work._id.toString}
                        class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Edytuj</a>
    })
  }


}
