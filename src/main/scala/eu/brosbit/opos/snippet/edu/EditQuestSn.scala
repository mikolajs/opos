package eu.brosbit.opos.snippet.edu

import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import http.SHtml
import common._
import util._
import eu.brosbit.opos.model._
import edu._
import Helpers._
import json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class EditQuestSn extends BaseResourceSn {


  def subjectChoice() = {
    super.subjectChoice("/educontent/questions")
  }

  def subjectAndDepartmentChoice() = {
    super.subjectAndDepartmentChoice("/educontent/questions")
  }

  def showQuests() = {
    val userId = user.id.get
    val questionsList =
      QuizQuestion.findAll(
        query,
        ("nr" -> 1)
      )

    "tr" #> questionsList.map(quest => {
      <tr id={quest._id.toString}>
        <td>{quest.nr.toString}</td>
        <td>
          {Unparsed(quest.question)}
        </td>
        <td>
          {Unparsed(quest.info)}
        </td>
        <td>
          {quest.answers.map(f => <span class="good">
          {f}
        </span>)}
        </td> <td>
        {quest.fake.map(f => <span class="wrong">
          {f}
        </span>)}
      </td>
        <td>
          {levMap(quest.lev.toString)}
        </td>
        <td>
          {quest.dificult}
        </td>
        <td>
          <button class="btn btn-success" onclick="editQuest.editQuestion(this);">
            <span class="glyphicon glyphicon-edit"></span>
          </button>
        </td>
      </tr>
    })

  }

  //working ....
  def editQuest() = {
    var id = ""
    var nr = 0
    var question = ""
    var level = ""
    var answer = ""
    var wrongAnswers = ""
    var department = ""
    var difficult = "1"
    var info = ""

    def save(): JsCmd = {
      //add nr of quest
     // QuizQuestion.findAll(("subjectId" -> subjectId)~ ("department" -> department)
    //  ~ ("authorId" -> userId)).map(qq => qq.nr).max
      val userId = user.id.get
      val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
      if (quest.authorId != 0L && quest.authorId != userId) return Alert("To nie twoje pytanie!")
      if (subjectNow.departments.isEmpty) return Alert("Musisz najpierw utworzyc dział w ustawieniach")
      quest.authorId = userId
      if(nr == 0) {
        val qi = QuestIndex.find(("authorId" ->  userId) ~ ("subjectId" -> subjectNow.id))
          .getOrElse(QuestIndex.create(userId, subjectNow.id))
        if(qi.nr == 0) {
          nr = 1
          qi.nr = 1
        } else {
          nr = qi.nr + 1
          qi.nr = nr
        }
        qi.save
      }
      quest.nr = nr
      quest.answers = answer.split(getSeparator).toList.map(a => a.trim).filterNot(a => a.length() == 0)
      quest.fake = wrongAnswers.split(getSeparator).toList.map(a => a.trim).filterNot(a => a.length() == 0)
      quest.question = question.trim
      quest.info = info.trim
      quest.subjectId = subjectNow.id
      quest.subjectName = subjectNow.name
      quest.department = department.trim
      quest.dificult = tryo(difficult.toInt).openOr(1)
      quest.lev = tryo(level.toInt).openOr(1)
      quest.save
      JsFunc("editQuest.insertQuestion", quest._id.toString).cmd
    }

    def delete(): JsCmd = {
      println("+++++++++++++++++++ Del QUEST ")
      val userId = user.id.get
      QuizQuestion.find(id) match {
        case Some(quest) => {
          if (quest.authorId == userId) {
            quest.delete
            JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
          }
          else Alert("To nie twoje pytanie!")
        }
        case _ => Alert("Nie znaleziono pytania!")
      }
    }

    val difficultList = List(("1", "Normalne"),("2", "Trudniejsze"),("3", "Bardzo trudne"))
    val departments = subjectNow.departments.map(d => (d, d))

    val form = "#idQuest" #> SHtml.text(id, id = _) &
      "#nrQuest" #> SHtml.text(nr.toString, x => nr = x.toInt) &
      "#infoQuest" #> SHtml.text(info, info = _) &
      "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
      "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
      "#subjectQuest" #> SHtml.text(subjectNow.name, x => Unit, "readonly" -> "readOonly") &
      "#levelQuest" #> SHtml.select(levList, Full(subjectNow.lev.toString), level = _) &
      "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
      "#departmentQuest" #> SHtml.select(departments, Full(departName), department = _) &
      "#dificultQuest" #> SHtml.select(difficultList, Full(difficult), difficult = _) &
      "#saveQuest" #> SHtml.ajaxSubmit("Zapisz", save) &
      "#deleteQuest" #> SHtml.ajaxSubmit("Usuń", delete) andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))

  }


  //private def printParam = println("subjectId="+ subjectId + " level=" + level)

}