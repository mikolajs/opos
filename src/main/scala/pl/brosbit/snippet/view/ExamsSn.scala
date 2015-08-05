
package pl.brosbit.snippet.view


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import pl.brosbit.lib.Formater
import java.util.Date

import pl.brosbit.model.edu.{QuizQuestion, Quiz, Exam}
import net.liftweb.http.S

class ExamsSn extends BaseSnippet with BaseCreateQuest {

  def showExams() = {
    val now = new Date()
    val nowString = Formater.formatTime(now)
    val nowL = now.getTime
    val exams = Exam.findAll(("classId" -> user.classId.get)~( "start" -> ("$lt" -> nowL ))~("end" -> ("$gt" -> nowL)))

    println("============== exmas found: " + exams.length.toString)
    "#nowDate *" #> nowString &
    ".col-lg-4" #> exams.map(ex => {
      mkExamDiv(ex)
    })
  }

  def oneExam() = {
    val id = S.param("id").getOrElse("")
    val exam = Exam.find(id).getOrElse(Exam.create)
    if(exam.authorId == 0L) S.redirectTo("/view/exams")


    val quiz = Quiz.find(exam.quizzes.head.toString).getOrElse(Quiz.create)
    println("=========  quiz: " + quiz.title + " ; length:  " + quiz.questions.length.toString)
    val questions = QuizQuestion.findAll("_id" -> ("$in" -> quiz.questions.map(qi => qi.q.toString)))
    println("=========  questions: " + questions.length.toString)
    "#descript *" #> exam.description &
    "#subject *" #> exam.subjectName &
    "#endTime *" #> Formater.formatDate(new Date(exam.end)) &
    "#test" #> questions.map( q => "div" #> createQuest(q))

  }

  private def mkExamDiv(ex:Exam) = {
    <div class="col-lg-4">
      <h4 class="text-info"> {ex.description} </h4>
      <p>Przedmiot: <strong>{ex.subjectName}</strong></p>
      <p>kod: <strong> {if(ex.quizzes.length > 1) "TAK" else "NIE"} </strong> </p>
      <p><em>Start: </em> { Formater.strForDateTimePicker(new Date(ex.start))} <br/>
        <em>Koniec: </em> { Formater.strForDateTimePicker(new Date(ex.end))}</p>
      <p>
        <a href={"/view/showquiz/" + ex._id.toString}> <span
        class="btn btn-info" > <span
        class="glyphicon glyphicon-edit"></span> Otwórz
        </span></a>
      </p>
    </div>
  }


}