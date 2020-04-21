package eu.brosbit.opos.snippet.edu

import java.util.Date

import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.edu.{LessonWork, LessonWorkAnswer}
import net.liftweb.http.S
import _root_.net.liftweb.util.Helpers._

class ShowLessonWorksSn {
  val lessonWorkId:String = S.param("id").openOr("")
  if(lessonWorkId.isEmpty) S.redirectTo("/educontent/works")
  private val work = LessonWork.find(lessonWorkId).getOrElse(LessonWork.create)

  def showInfo() = {
    "span *" #> work.className &
      "h3 *" #> work.lessonTitle &
      "small *" #> ("od " + Formater.formatTime(new Date(work.start)) +
        " do " + Formater.formatTime(new Date(work.end)))
  }
  //TODO: change to Works -> move to ShowLessonWork.scala
//  def showExamAnswers() = {
//    val workAns = LessonWorkAnswer.findAll("exam" -> work._id.toString)
//
//    "tr" #>  workAns.map(ea => {
//      val gain = if(!ea.answers.isEmpty) ea.answers.map(_.p).reduce((f, n) => f + n) else 0
//      val percent = if(ea.max == 0) 0.0F else ((100.0F* gain.toFloat) / ea.max.toFloat)
//      ".col1 *" #> ea.authorName &
//        ".col2 *" #> ea.code &
//        ".col3 *" #> (gain.toString + " / " + ea.max.toString  + " : " +
//          scala.math.round(percent).toString  + "%") &
//        ".col4 *" #> <a href={"/educontent/checkexam/" + ea._id.toString}
//                        class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Sprawdź</a>
//
//    })
//
//  }

}
