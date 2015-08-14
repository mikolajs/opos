package pl.brosbit.snippet.edu

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.http.S
import _root_.net.liftweb.json.JsonDSL._
import pl.brosbit.model.edu._
import scala.xml.Unparsed



class CheckExamSn {
  val ansId = S.param("id").openOr("")
  if(ansId.isEmpty) S.redirectTo("/educontent/exams")

  val ansEx = ExamAnswer.find(ansId).getOrElse(ExamAnswer.create)
  val exam = Exam.find(ansEx.exam.toString).getOrElse(Exam.create)
  val group = getGroupInt
  val quiz = Quiz.find(exam.quizzes(group)).getOrElse(Quiz.create)
  val questList = quiz.questions.map(qi => qi.q.toString )

  val questions = QuizQuestion.findAll("_id" -> ("$in" -> questList))


  //question, answers, points gained, points max
  val questItems = questions.map(quest => {
   val idStr = quest._id.toString
   val itemAns = findAnswerItem(ansEx.answers, idStr)

    (quest, itemAns._1, itemAns._2 , findPointsFromQuiz(quiz.questions, idStr))
  })

  var nr = 0

 def showAnswers() = {
   "div" #>  questItems.map(q =>
   "section" #> mkQuestHTML(q._1, q._2, q._3, q._4) )
 }

 def showPupil() = {
   "span *" #>  ansEx.authorName &
   "small *" #> exam.className
 }

  private def getGroupInt = {
    if(ansEx.code.isEmpty) 0
    else {
      ansEx.code.charAt(0).toInt -'A'.toInt
    }
  }

  protected def mkQuestHTML(question:QuizQuestion, ans:String, pktG:Int, pktA:Int) = {

    nr += 1
    <section class="question" id={"_" + question._id.toString } name={"zad_" + nr.toString}>
      <div class="panel panel-info">
        <div class="panel-heading">
          <span class="quizNr">{nr.toString}</span>
          Zadanie  <span class="badge" title="Punkty">{pktA.toString} pkt.</span>
          <input type="number" min="0" maxlength="5" max={pktA.toString} step="0.5" name={"zad_" + nr.toString}
          value={pktG.toString} /></div>
        <div class="panel-body">
          <div class="questionText">
            {Unparsed(question.question)}
          </div>
          <div class="form-group">{createAnswers(question.answers, question.fake, ans,  nr.toString)}</div>
        </div>
      </div>
    </section>
  }

  protected def createAnswers(good:List[String], fake:List[String], ans:String, nr:String) = {

    val name = "quest_" + nr.toString

    if(fake.length == 0) {
      if(good.length > 0)  <input type="text" readonly="readonly" class="form-control" value={ans}  name={name} />
      else  <textarea class="form-control" name={name} readonly="readonly">{ans}</textarea>
    }
    else {

      val all = fake.map(s => <li class="list-group-item">
        <span class="glyphicon"></span>{Unparsed(s)} </li>) ++
        good.map(s => <li class="list-group-item">
          <span class="glyphicon"></span>{Unparsed(s)} </li>)

      <div class="answerBox"><ul class="list-group" name={name}>{all}</ul></div>
    }

  }

  private def findAnswerItem(answs: List[AnswerItem], idStr:String) =
    answs.find(it => (it.q == "_" + idStr)) match {
      case Some(ai) => (ai.a, ai.p)
      case _ => ("Błąd", 0)
    }

  private def findPointsFromQuiz(quests: List[QuestElem], idStr:String) =
    quests.find(q => q.q.toString == idStr) match {
      case Some(q) => q.p
      case _ => -1
    }

}
