package pl.edu.osp.snippet.edu

import java.util.Date
import scala.xml.{Text, XML, Unparsed}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import mapper.{OrderBy, Descending}
import pl.edu.osp.model._
import pl.edu.osp.model.edu._
import mapper.By
import http.js.JsCmds.SetHtml
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._
//import com.sun.org.omg.CORBA._IDLTypeStub

//pokazuje i edytuje quizy i pytania
class CreatePracticeSn {
  /*
  val user = User.currentUser.openOrThrowException("Nauczyciel musi być zalogowany!")

  def showPractices() = {
      val userId = user.id.get

      "#tr" #> Quiz.findAll("authorId"->userId).map(quiz => {
          <tr><td>{quiz.title}</td><td>{quiz.departmentInfo}</td><td>{quiz.subjectInfo}</td>
          <td>{quiz.questions.length.toString}</td><td>{quiz.subjectLev.toString}</td>
          <td>{quiz.description}</td>
          </tr>
      })
  }

  def showQuestion() = {
      val userId = user.id.get
      "#tr" #> QuizQuestion.findAll("authorId"->userId).map(quest => {
          <tr><td><img src="/images/putico.png" name={quest._id.toString}/></td>
          <td><div class="quest"></div>
<span class="goodAnswer"></span><div class="wrong"><span class="wrongAnswer"></span></div></td>
          <td><img src="/images/penico.png" /></td></tr>
      })
  }

  //not implemented!!!!!!!!!
  def editQuiz() = {
      "#deleteQuiz" #> <span>Tymczasowa</span>
  }

  def editQuest() = {

      val idParam = S.param("qu").openOr("0")
      var id = idParam
      val questData = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
      var question = questData.question
      var answer = questData.answer
      var wrongAnswers = questData.fake.join("|||")
      var subjectId = if(questData.subjectId != null) questData.subjectId.toString else ""
      var departmentId = if(questData.departmentId != null) questData.departmentId.toString else ""
      var dificult = questData.dificult.toString
      var level = questData.level.toString

      def save():JsCmd = {
          ///dodać test uprawnień
          val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
          quest.answer = answer
          quest.fake = wrongAnswers.split("|||").toList
          quest.answer = answer
          quest.answer = question
          Subject.find(subjectId) match {
              case Some(sub) => {
                  quest.subjectId = sub._id
              }
              case _ => return Alert("Błędny przedmiot nie zapisano testu!")
          }
          Department.find(departmentId) match {
              case Some(dep) => {
                  quest.departmentId = dep._id
                  JsFunc("saveQuestion", quest._id.toString).cmd
              }
              case _ => return Alert("Błędny dział przedmiot nie zapisano testu!")
          }
          quest.dificult = tryo(dificult.toInt).openOr(10)
          quest.level = tryo(level.toInt).openOr(1)
          quest.save
      }

      def delete():JsCmd = {
          QuizQuestion.find(id) match {
              case Some(quest) => {
                  quest.delete
                  JsFunc("deleteQuestion", quest._id.toString).cmd
              }
              case _ => Alert("Nie znaleziono pytania!")
          }
      }

      val subjects = Subject.findAll.map(sub => (sub._id.toString, sub.full))
      val departments = Department.findAll.map(dep => (dep._id.toString, dep.name))
      val dificults = 1 to 10 map( i => {val iS = i.toString;(iS, iS)})
      val levels = List(("1","I"),("2","II"),("3","III"),("4","IV"),("5","V"))


     val form = "#idQuest" #> SHtml.text(id, id = _) &
      "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
     "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
     "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
     "#subjectQuest" #> SHtml.select(subjects,Full(subjectId), subjectId = _) &
     "#departmentQuest" #> SHtml.select(departments, Full(departmentId), departmentId = _ ) &
     "#levelQuest" #> SHtml.select(levels, Full("1"), level = _) &
     "#dificultQuest" #> SHtml.select(dificults, Full(dificult), dificult = _) &
     "#saveQuest" #> SHtml.submit("Zapisz",save) &
     "#deleteQuest" #> SHtml.submit("Usuń",save) andThen SHtml.makeFormsAjax

      "form" #> (in => form(in))
       }
     */

}
