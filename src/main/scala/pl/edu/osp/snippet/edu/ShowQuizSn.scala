package pl.edu.osp.snippet.edu

import scala.xml.Unparsed
import _root_.net.liftweb._
import http.{S, SHtml, RequestVar}
import util._
import pl.edu.osp.model._
import edu._
import Helpers._
import http.js.JsCmds._
import http.js.JsCmd
import http.js.JE._

/*
 old work for show questions in quiz
 */
object correct extends RequestVar[Int](0)

object questNow extends RequestVar[Int](0)

class ShowQuizSn {
  /*
  val quizId = S.param("id").getOrElse("0")
  val quiz = Quiz.find(quizId).getOrElse(Quiz.create)

  def title() = {
    if (quiz.title == "") S.redirectTo("/")
    "#title *" #> quiz.title &
      "#subject *" #> quiz.subjectName &
      "#questionsNr *" #> quiz.questions.length.toString
  }

  def firstQuest() = {

    val contentQuest = QuizQuestion.find(quiz.questions.head) match {
      case Some(quest) => quest.question
      case _ => "Błąd: brak pytania"
    }
    "u" #> <div>
      {Unparsed(contentQuest)}
    </div>
  }

  def possibleAnswers() = {
    var answers = List("")
    quiz.questions.map(id => {
      QuizQuestion.find(id) match {
        case Some(quest) => {
          answers = quest.answers ::: answers //bez sensu!!
          answers = quest.fake ::: answers
        }
        case _ =>
      }
    })
    answers = answers.sortWith((b, n) => b.toLowerCase < n.toLowerCase)
    "ul" #> <ul>
      {answers.map(a => <li>
        {a}
      </li>)}
    </ul>
  }


  def getAnswer() = {

    var answer = ""

    def proceed(): JsCmd = {
      if (questNow.is >= quiz.questions.length) return Alert("Test jest już zakończony \n Zamknij kartę")

      val questOld = QuizQuestion.find(quiz.questions(questNow.is))
      val wasCorrect = if (!questOld.isEmpty) {
        if (answer.trim == questOld.get.answers.mkString("")) true; //bez sensu!!!
        else false
      } else true

      questNow.set(questNow.is + 1)
      if (wasCorrect) correct.set(correct.is + 1)
      if (questNow.is >= quiz.questions.length) proceedEndQuiz(wasCorrect)
      else proceedNextQuiz(wasCorrect)
    }

    def proceedEndQuiz(wasCorrect: Boolean): JsCmd = {

      val percentCorrect = (correct * 100) / quiz.questions.length

      SetHtml("questionContent", <div>
        <h1>KONIEC</h1>
        <h2>Twój wynik to:
          {percentCorrect.toString()}
          %</h2>
      </div>) & JsFunc("afterAnswer", wasCorrect.toString).cmd
    }

    def proceedNextQuiz(wasCorrect: Boolean): JsCmd = {

      val questNew = QuizQuestion.find(quiz.questions(questNow.is))

      val question = questNew match {
        case Some(q) => q.question
        case _ => "Błąd: brak pytania"
      }
      SetHtml("questionContent", <div>
        {Unparsed(question)}
      </div>) &
        JsFunc("afterAnswer", wasCorrect).cmd

    }

    val form = "#answerContent" #> SHtml.text(answer, answer = _) &
      "#giveAnswer" #> SHtml.ajaxSubmit("OK", proceed) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }
  */

}