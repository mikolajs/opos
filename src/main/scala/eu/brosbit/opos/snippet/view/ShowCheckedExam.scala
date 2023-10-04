package eu.brosbit.opos.snippet.view

import eu.brosbit.opos.lib.{Formater, ZeroObjectId}
import _root_.net.liftweb.util._
import Helpers._
import eu.brosbit.opos.model.edu.{AnswerItem, AnswerWorkItem, Exam, ExamAnswer, QuizQuestion, Work, WorkAnswer}
import net.liftweb.common.Empty
import net.liftweb.http.{S, SHtml}
import net.liftweb.json.JsonDSL._
import net.liftweb.util.CssSel

import java.util.Date
import scala.xml.{NodeSeq, Unparsed}

class ShowCheckedExam extends BaseSnippet {
  var nr = 0
  private val id = S.param("id").getOrElse("0")
  private val exAns = ExamAnswer.findAll(("_id" -> id) ~ ("authorId" -> user.id.get)).headOption
    .getOrElse(ExamAnswer.create)
  private val exam = Exam.find(exAns.exam.toString).getOrElse(Exam.create)


  def showExamAnswer():CssSel = {


      val quizzes = if(exam.quizzes.isEmpty) Nil else exam.quizzes(getGroupInt)
      //println("=========== quizzes length " + quizzes.length)
      //val quiz = Quiz.find(idQuiz).getOrElse(Quiz.create)
      // println("=========  quiz: " + quiz.title + " ; length:  " + quiz.questions.length.toString)
      if(quizzes.length < 1) S.redirectTo("/view/exams?Error")
      val questMap = quizzes.map(qi =>  qi.q.toString -> qi.p).toMap
      val answerMapA = exAns.answers.map(a => a.q -> a.a).toMap
      val questions = QuizQuestion.findAll("_id" -> ("$in" -> questMap.keySet.toList))
      val questionsItems = questions.map(qu =>  {
        val id = "_"+qu._id.toString
        val answerIt = exAns.answers.find(_.q == id).getOrElse(AnswerItem("", "", 0))
        (qu, questMap(qu._id.toString), answerIt.a, answerIt.p)
      })
        "#descript *" #> exam.description &
        "#subject *" #> exam.subjectName &
        "#endTime *" #> Formater.formatDate(new Date(exam.end)) &
        "#info *" #> exAns.info &
        "#test" #> questionsItems.map( q => "div" #> mkQuestHTML(q._1, q._2, q._3, q._4))
  }

  protected def mkQuestHTML(question:QuizQuestion, pkt:Int, ans:String, getPkt:Int) = {

    nr += 1
    <section class="question" id={"_" + question._id.toString } name={"zad_" + nr.toString}>
      <div class="panel panel-info">
        <div class="panel-heading">
          <span class="quizNr">{nr.toString}</span>
          Zadanie  <span class="badge" title="Punkty">{pkt.toString} pkt.</span>
          <span class="badge leftPkt" title="Uzyskane">Uzyskane: {getPkt} pkt</span>
        </div>
        <div class="panel-body">
          <div class="questionText">
            {Unparsed(question.question)}
          </div>
          <div>{createAnswers(question.answers, question.fake)}</div>
          <div class="yourAns"><h4>Twoja odpowiedź:</h4>
            <p><pre>{Unparsed(ans.split(";#;;#;").mkString("<br/>"))}</pre></p></div>
        </div>
      </div>
    </section>
  }

  protected def createAnswers(good:List[String], fake:List[String]) = {
//todo: show test answer possible and answer added by user, show points acquired
    val name = "quest_" + nr.toString
    if(fake.isEmpty)  <span></span>
    else {
      val all = (fake.map(s => <li style="color:red;">{Unparsed(s)}</li>) ++ good.map(s => <li style="color:green;">{Unparsed(s)}</li>))
      <div class="answerBox"><ul>{all}</ul></div>
    }

  }

  private def getGroupInt = {
    if(exAns.code.isEmpty) 0
    else {
      exAns.code.charAt(0).toInt -'A'.toInt
    }
  }

}
