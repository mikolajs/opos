package pl.brosbit.snippet.edu


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.S
import pl.brosbit.model.edu.{ExamAnswer, Exam}


class ShowExamsSn extends BaseResourceSn {

  val exId = S.param("id").openOr("")
  if(exId.isEmpty) S.redirectTo("/educontent/exams")
  val exam = Exam.find(exId).getOrElse(Exam.create)

  def showInfo() = {
    "span *" #> exam.description
  }

  def showExamAnswers() = {
    val exAns = ExamAnswer.findAll("exam" -> exam._id.toString)

    "tr" #>  exAns.map(ea => {
      ".col1 *" #> ea.authorName &
      ".col2 *" #> ea.code &
      ".col3 *" #> (ea.answers.map(_.p).reduce((f, n) => f + n)).toString &
      ".col4 *" #> <a href={"/educontent/checkexam" + ea._id.toString}
                      class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Sprawdź</a>

    })

  }


}
