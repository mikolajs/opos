package pl.brosbit.snippet.edu


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.S
import pl.brosbit.model.edu.{Quiz, ExamAnswer, Exam}
import pl.brosbit.lib.Formater
import java.util.Date

class ShowExamsSn extends BaseResourceSn {

  val exId = S.param("id").openOr("")
  if(exId.isEmpty) S.redirectTo("/educontent/exams")
  val exam = Exam.find(exId).getOrElse(Exam.create)
  val quizes = Quiz.findAll("_id" -> ("$in" -> exam.quizzes.map(q => q.toString)))
  var pointsMaxList = quizes.map(qz => qz.questions.map(q => q.p).reduce(_ + _))
  if(pointsMaxList.length != exam.quizzes.length) pointsMaxList = (-5 to (exam.quizzes.length - 5)).toList

  def showInfo() = {
    "span *" #> exam.description &
    "small *" #> ("od " + Formater.formatTime(new Date(exam.start)) +
      " do " + Formater.formatTime(new Date(exam.end)))
  }

  def showExamAnswers() = {
    val exAns = ExamAnswer.findAll("exam" -> exam._id.toString)

    "tr" #>  exAns.map(ea => {
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


}
