package pl.brosbit.snippet.edu

import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import pl.brosbit.model._
import edu._
import Helpers._
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class EditQuizSn extends BaseResourceSn {

  var subjectId = S.param("sub").openOr("0")
  var level = S.param("lev").openOr("3")
  if (subjectId == "0") subjectTeach.head.id

  def choiseQuest() = {
    val subjects = subjectTeach.map(s => (s.id.toString, s.name))
    def makeChoise() {
      S.redirectTo("/resources/editquiz?sub=" + subjectId + "&lev=" + level)
    }

    "#subjects" #> SHtml.select(subjects, Full(subjectId), subjectId = _) &
      "#levels" #> SHtml.select(levList, Full(level), level = _) &
      "#choise" #> SHtml.submit("Wybierz", makeChoise)
  }

  def questionList() = {
    val userId = user.id.get

    val questions = QuizQuestion.findAll(("authorId" -> userId) ~ ("subjectId" -> subjectId) ~
      ("level" -> level.toInt))

    ///dodać wyszukiwanie pytań z quizu i podział na dwie listy oraz wyświetlenie osobne

    ".questLiAll" #> questions.map(quest => <li id={quest._id.toString}>
      <span class="question">
        {Unparsed(quest.question)}
      </span>
      <span class="rightAnswer">
        {quest.answers.mkString("; ")}
      </span>{quest.fake.map(f => <span class="wrong">
        {f}
      </span>)}<span class="department">
        {quest.department}
      </span>
      <strong title="poziom trudności">
        {quest.dificult}
      </strong>
    </li>)
  }

  //working ....
  def editQuiz() = {

    val idQuiz = S.param("id").openOr("0")
    var id = idQuiz
    var questions = ""
    var public = false
    var description = ""
    var department = ""
    var title = ""
    val userId = user.id.get

    Quiz.find(idQuiz) match {
      case Some(quiz) => {
        questions = quiz.questions.mkString(";")
        department = quiz.department
        description = quiz.description
        title = quiz.title
      }
      case _ =>
    }

    def save(): JsCmd = {
      println("========= save quiz ========")
      val quiz = Quiz.find(id).getOrElse(Quiz.create)
      if (quiz.authorId != 0L && quiz.authorId != userId) return Alert("To nie jest Twój test!")

      quiz.description = description
      if (quiz.authorId == 0L) quiz.authorId = userId
      quiz.title = title
      quiz.subjectLev = tryo(level.toInt).openOr(0)
      quiz.department = department
      quiz.subjectId = tryo(subjectId.toLong).openOr(0L)
      quiz.subjectName = findSubjectName(quiz.subjectId)
      quiz.questions = questions.split(";").toList.map(q => new ObjectId(q))
      quiz.save
      S.redirectTo("/resources/quizes")
      Alert("Zapisano!")
    }

    def delete(): JsCmd = {
      println("========= delete quiz ========")
      Quiz.find(id) match {
        case Some(quiz) => {
          if (quiz.authorId != 0L || userId == quiz.authorId ||
            User.currentUser.openOrThrowException("Niezalogowany nauczyciel").superUser.is) {
            quiz.delete
            S.redirectTo("/resources/quizes")
            Run("")
          } else Alert("To nie jest Twój test!")
        }
        case _ => Alert("Nie znaleziono testu!")
      }
    }

    val form =
      "#titleQuiz" #> SHtml.text(title, x => title = x.trim) &
        "#descriptionQuiz" #> SHtml.textarea(description, x => description = x.trim) &
        "#questionsQuiz" #> SHtml.text(questions, x => questions = x.trim) &
        "#publicQuiz" #> SHtml.checkbox(public, public = _, "id" -> "publicQuest") &
        "#saveQuiz" #> SHtml.ajaxSubmit("Zapisz", save) &
        "#deleteQuiz" #> SHtml.ajaxSubmit("Usuń", delete) andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))

  }

  private def printParam =
    println("subjectId=" + subjectId + " level=" + level)

}