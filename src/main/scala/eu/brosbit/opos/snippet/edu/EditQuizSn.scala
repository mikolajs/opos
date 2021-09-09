package eu.brosbit.opos.snippet.edu

import scala.xml.{Text, Unparsed, NodeSeq}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import eu.brosbit.opos.model._
import edu._
import Helpers._
import json.JsonDSL._
import org.bson.types.ObjectId
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JE.JsRaw
import eu.brosbit.opos.lib.Formater
import java.util.Date



//TODO: DELETE SNIPPET after change all code
class EditQuizSn extends BaseResourceSn {

  var quizId = S.param("id").openOr("0")
  val userId = user.id.get
  val quiz = Quiz.find(quizId).getOrElse(Quiz.create)

  def questionList() = {
    val data = mkQuestBoxWithPoints(quiz.questions)
    ".quizElement" #> data
  }

  //working ....
  def editQuiz() = {

    var questions = ""
    var public = false
    var description = quiz.description
    var title = quiz.title
    val userId = user.id.get

    def save()  {
      println("========= save quiz ========")
      if (quiz.authorId != 0L && quiz.authorId != userId) return
      if(title == "" || questions.trim() == "") return
      quiz.description = description
      if (quiz.authorId == 0L) quiz.authorId = userId
      quiz.title = title
      quiz.subjectId = tryo(subjectId).openOr(0L)
      quiz.subjectName = findSubjectName(quiz.subjectId)
      quiz.questions = questions.split(';').toList.distinct.map(qe => {
        val elem = qe.split(',')
        QuestElem(new ObjectId(elem(0)), tryo(elem(1).toInt).getOrElse(1))
      })
      quiz.save
      S.redirectTo("/educontent/editquiz/" + quiz._id.toString)
    }

    def delete() {
      println("========= delete quiz ========")
      if (quiz.authorId != 0L || userId == quiz.authorId) {
            //dodać wyszukiwanie sprawdzianów i informację o konieczności ich skasowania
            quiz.delete
            S.redirectTo("/educontent/quizzes")
      } else S.warning("Nie jesteś autorem lub testu jeszcze nie ma.")
    }

      "#titleQuiz" #> SHtml.text(title, x => title = x.trim) &
        "#descriptionQuiz" #> SHtml.textarea(description, x => description = x.trim) &
        "#questionsQuiz" #> SHtml.text(questions, x => questions = x.trim) &
        "#publicQuiz" #> SHtml.checkbox(public, public = _, "id" -> "publicQuest") &
        "#saveQuiz" #> SHtml.submit("Zapisz", save) &
        "#deleteQuiz" #> SHtml.submit("Usuń", delete)
  }

  def choiceDepart() = {
    val departs = subjectNow.departments.map(d => (d, d))
    var depart = if(subjectNow.departments.isEmpty) "" else subjectNow.departments.head

//    def getData():JsCmd = {
//      println("======= getData depart: " + depart )
//      val data = mkQuestBox(QuizQuestion.findAll(
//        ("authorId" -> userId) ~ ("subjectId" -> subjectId) ~ ("department" -> depart)
//      ))
//       SetHtml("allquestions", data) & Run("editQuiz.removeDuplicate();")
//    }

    def getDataNew = {
      val lookingQuest =  ("authorId" -> userId) ~ ("subjectId" -> subjectId) ~ ("department" -> depart)
      val str = QuizQuestion.findAll(lookingQuest)
        .map(q => "[ '" + q._id.toString + "',  '" + q.nr.toString + "', '" +
           q.question + "',  '" + q.info + "',  '" +
            q.lev + "',  '"  + q.dificult + "']")
        .mkString(",")
      "[" + str + "]"
    }

    def refreshData(): JsCmd = {

      SetValById("jsonForDataTable", getDataNew) & Run("refreshTab();")
    }

    val form = "#departments" #> SHtml.select(departs, Full(depart), depart = _) &
    "#getDeparts" #> SHtml.ajaxSubmit("Wybierz", refreshData ) andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))
  }

  def showInfo = "span *" #> subjectNow.name

  def showAllUserQuizzes() = {

    "tr" #> Quiz.findAll(("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id)).map(quiz => {
      <tr>
        <td> {quiz.title}   </td>
        <td>{quiz.description} </td>
        <td> {quiz.questions.length.toString}</td>
        <td>
          <a class="btn btn-success" href={"/educontent/editquiz/" + quiz._id.toString}>
            <span class="glyphicon glyphicon-pencil"></span></a>
        </td>
      </tr>
    })
  }

  def subjectChoice() = super.subjectChoice("/educontent/quizzes")

  def subjectForNew() =
    "a [href]" #> ("/educontent/editquiz/0?s=" + subjectNow.id.toString)


  //sort not work???????
  def showExams() = {
    ".col-lg-4"  #> Exam.findAll(("authorId" -> userId), ("start" -> -1)).map(ex => {
      mkExamDiv(ex)
    })
  }

  private def mkExamDiv(ex:Exam) = {

    <div class="col-lg-4">
      <h4 class="text-info"> {ex.description} </h4>

      <p>Grupa: <strong> {ex.groupName} </strong> rzędy: <strong> {ex.quizzes.length.toString} </strong> </p>
      <p><em>Start: </em> { Formater.strForDateTimePicker(new Date(ex.start))} <br/>
        <em>Koniec: </em> { Formater.strForDateTimePicker(new Date(ex.end))}</p>
      <p>
        <a href={"/educontent/showexams/" + ex._id.toString}> <span
        class="btn btn-info" > <span
        class="glyphicon glyphicon-edit"></span> Sprawdź
        </span></a>
        <a href={"/educontent/editexam/" + ex._id.toString}> <span
        class="btn btn-warning" > <span
        class="glyphicon glyphicon-pencil"></span> Edytuj
        </span></a>
      </p>
    </div>

  }




  private def mkQuestBox(questions:List[QuizQuestion]) = {
    println("EditQuiz.mkQuestBox: questions length " + questions.length.toString)

    questions.map(quest => mkBox(quest, 1))
  }

  private def mkQuestBoxWithPoints(questElems:List[QuestElem]) = {
    val qes = questElems.map(qe => qe.q.toString)
    val quests = QuizQuestion.findAll(( "_id" -> ("$in" -> qes )))
    questElems.map(qe => mkBox(quests.find(q => q._id.toString == qe.q.toString).getOrElse(QuizQuestion.create), qe.p))
  }


  private def mkBox(quest: QuizQuestion, p:Int) = {
    <li id={quest._id.toString}>
      <div class="infoText">{"[" + quest.info + "]"}</div>
      <div class="question">
        {Unparsed(quest.question)}
      </div>
      <div class="questInfo">
        <span class="nr">{quest.nr.toString} </span>  |
        <span class="department">
          {quest.department}
        </span> |
        <span class="level">
          {(quest.lev match {
          case 1 => "podstawowy"
          case 2 => "rozszerzony"
          case 3 => "konkursowy"
          case _ => "-"
        }) + " " +
        (quest.dificult match {
          case 3 => "★★"
          case 2 => "★"
          case _ => " "})}</span>
         | Pukty:
        <input class="points" type="number" value={p.toString} />
        <span class="btn btn-sm btn-danger" onclick="editQuiz.removeLi(this);">
          <span class="glyphicon glyphicon-remove"></span>
        </span>
      </div>
    </li>
  }



}