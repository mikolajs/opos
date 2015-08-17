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
      val gain = ea.answers.map(_.p).reduce((f, n) => f + n)
      val max = pointsMaxList(getGroupFromCode(ea.code))
      ".col1 *" #> ea.authorName &
      ".col2 *" #> ea.code &
      ".col3 *" #> (gain.toString + " / " + max.toString  + " : " +
        scala.math.round((100.0F* gain.toFloat) / max.toFloat).toString  + "%") &
      ".col4 *" #> <a href={"/educontent/checkexam/" + ea._id.toString}
                      class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Sprawdź</a>

    })

  }

  private def getGroupFromCode(code:String) = {
    if(code.isEmpty) 0
    else {
      val nr = code.charAt(0).toInt -'A'.toInt
      if(nr >= 1 && nr <= 4) nr else 0
    }
  }


}
