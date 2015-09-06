package pl.brosbit.snippet.view

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import pl.brosbit.lib.Formater
import java.util.Date

import pl.brosbit.model.edu._
import net.liftweb.http.{SHtml, S}
import scala.xml.Unparsed
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._

class PerformExamSn extends BaseSnippet {

  var nr = 0
  val id = S.param("id").getOrElse("")
  val exam = Exam.find(id).getOrElse(Exam.create)
  val multi = exam.multi
  val exAnsList = ExamAnswer.findAll(("exam" -> exam._id.toString) ~ ("authorId" -> user.id.get))
  val exAns = if(exAnsList.isEmpty) ExamAnswer.create
  else exAnsList.head

  def oneExam():CssSel = {
    var enterCode = ""

    def saveCode() {

      if(checkCode(enterCode)) {
        exAns.code = enterCode
        exAns.exam = exam._id
        exAns.authorId = user.id.get
        exAns.authorName = user.getFullName
        exAns.save
      }
      else {
        S.notice("Nieprawidłowy kod")
        S.redirectTo("/view/showquiz/" + exam._id.toString)
      }


    }

    if(exam.quizzes.length > 1 && exAns.code.isEmpty) {

         "#code" #> SHtml.text(enterCode, enterCode = _) &
          "#descript" #> <span style="display:none;"></span> &
          "#subject" #> <span style="display:none;"></span> &
          "#endTime *" #> Formater.formatDate(new Date(exam.end)) &
          "#test" #> <span style="display:none;"></span> &
          "#saveCode" #> SHtml.submit("Zatwierdź", saveCode)
    }
    else
    {
    val idQuiz = if(exam.quizzes.isEmpty) "0" else exam.quizzes.head.toString
    val quiz = Quiz.find(idQuiz).getOrElse(Quiz.create)
    if(quiz.questions.length < 1) S.redirectTo("/view/exams?Error")
    println("=========  quiz: " + quiz.title + " ; length:  " + quiz.questions.length.toString)
    val questMap = quiz.questions.map(qi => ( qi.q.toString -> qi.p)).toMap

    val questions = QuizQuestion.findAll("_id" -> ("$in" -> questMap.keySet.toList))
    val questionsItems = questions.map(qu =>  (qu, questMap(qu._id.toString)))
    println("=========  questions: " + questions.length.toString)
    "form" #> <span style="display:none;"></span> &
      "#descript *" #> exam.description &
      "#subject *" #> exam.subjectName &
      "#endTime *" #> Formater.formatDate(new Date(exam.end)) &
      "#test" #> questionsItems.map( q => "div" #> mkQuestHTML(q._1, q._2))
    }
  }


  def getAnswers() = {


    val answers = "[" + exAns.answers.map(_.json).mkString(",") + "]"

   "#answers" #> SHtml.ajaxText(answers, (data) => {
      println("========== get Answers acctions: " + data)
      exAns.answers = createFromJsonList(data)
      exAns.exam = exam._id
      exAns.authorId = user.id.get
      if( (exam.end + 30000L) >= new Date().getTime) exAns.save
  })
 }


  def setTime() = {
    val now = new Date().getTime
    val end = (exam.end - now) / 1000L

    "#secondsToEnd *" #> end.toString
  }


  protected def mkQuestHTML(question:QuizQuestion, pkt:Int) = {

    nr += 1
    <section class="question" id={"_" + question._id.toString } name={"zad_" + nr.toString}>
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

      val all = (fake ++ good).sortWith(_ < _) .map(s => <div class={aType}>
        <label><input type={aType} value={s} name={name}/>
          {Unparsed(s)} </label> </div>)

      <div class="answerBox">{all}</div>
    }

  }

  private def createFromJsonList(jsonStr: String) = {

    var data:List[AnswerItem] = Nil
    implicit val formats = DefaultFormats
    try {
    val json = parse(jsonStr)
    data = json.extract[List[AnswerItem]]
    } catch {case _ : Throwable => println("ERROR PARSER JSON PERFORMEXAMS")}
    data
  }

  private def checkCode(c: String):Boolean = {

    if(findIfCodeExists(c))  false
    else exam.keys.exists(k => k == c)
  }

  private def findIfCodeExists(c:String) = !ExamAnswer.findAll("code" -> c).isEmpty

  private def getGroupNr(c:String) =  c.charAt(0).toInt - 'A'.toInt


}
