package pl.brosbit.snippet.view

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import pl.brosbit.lib.Formater
import java.util.Date

import pl.brosbit.model.edu.{QuestElem, QuizQuestion, Quiz, Exam}
import net.liftweb.http.S
import scala.xml.Unparsed

class PerformExamSn {

  var nr = 0
  val id = S.param("id").getOrElse("")
  val exam = Exam.find(id).getOrElse(Exam.create)
  val multi = exam.multi

  def oneExam() = {

    val quiz = Quiz.find(exam.quizzes.head.toString).getOrElse(Quiz.create)
    println("=========  quiz: " + quiz.title + " ; length:  " + quiz.questions.length.toString)
    val questMap = quiz.questions.map(qi => ( qi.q.toString -> qi.p)).toMap

    val questions = QuizQuestion.findAll("_id" -> ("$in" -> questMap.keySet.toList))
    val questionsItems = questions.map(qu =>  (qu, questMap(qu._id.toString)))
    println("=========  questions: " + questions.length.toString)
    "#descript *" #> exam.description &
      "#subject *" #> exam.subjectName &
      "#endTime *" #> Formater.formatDate(new Date(exam.end)) &
      "#test" #> questionsItems.map( q => "div" #> mkQuestHTML(q._1, q._2))

  }


  protected def mkQuestHTML(question:QuizQuestion, pkt:Int) = {

    nr += 1
    <section class="question">
      <div class="panel panel-info">
        <div class="panel-heading">
          <span class="quizNr">{nr.toString}</span>
          Zadanie  <span class="badge" title="Punkty">{pkt.toString} pkt.</span></div>
        <div class="panel-body">
          <div class="questionText">
            {Unparsed(question.question)}
          </div>
          <div class="form-group">{createAnswers(question.answers, question.fake)}</div>
        </div>
      </div>
    </section>
  }

  protected def createAnswers(good:List[String], fake:List[String]) = {

    val name = "quest_" + nr.toString

    if(fake.length == 0) {
      if(good.length > 0)  <input type="text" class="form-control" value=""  name={name} />
      else  <textarea class="form-control" name={name}></textarea>
    }
    else {
      val aType = if(good.length > 1 || multi) "checkbox" else "radio"

      val all = (fake ++ good).sortWith(_ > _) .map(s => <li>
        <input class="form-control" type={aType} value={s} name={name}/> <label>
          {Unparsed(s)} </label> </li>)

      <ul>{all}</ul>
    }



  }

}
