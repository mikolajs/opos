package pl.brosbit.snippet.edu

import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.http.{SHtml, S}
import _root_.net.liftweb.json.JsonDSL._
import pl.brosbit.model.edu._
import scala.xml.Unparsed
import net.liftweb.json.DefaultFormats
import net.liftweb.json.JsonParser._
import pl.brosbit.model.edu.AnswerItem
import scala.Some
import pl.brosbit.model.edu.QuestElem


class CheckExamSn {
  val ansId = S.param("id").openOr("")
  if(ansId.isEmpty) S.redirectTo("/educontent/exams")

  val ansEx = ExamAnswer.find(ansId).getOrElse(ExamAnswer.create)
  val exam = Exam.find(ansEx.exam.toString).getOrElse(Exam.create)
  val group = getGroupInt
  //println("GROUP CHeck exam: " + group)
  val quiz = Quiz.find(exam.quizzes(group)).getOrElse(Quiz.create)
  //println("quizID exam: " + quiz._id.toString + " " + quiz.title)
  val questList = quiz.questions.map(qi => qi.q.toString )
  //println("Quest List exam: " + questList.mkString(", "))
  val questions = QuizQuestion.findAll("_id" -> ("$in" -> questList))


  //question, answers, points gained, points max
  val questItems = questions.map(quest => {
   val idStr = quest._id.toString
   val itemAns = findAnswerItem(ansEx.answers, idStr)

    (quest, itemAns._1, itemAns._2 , findPointsFromQuiz(quiz.questions, idStr))
  })

  var nr = 0

  var maxPoints = questItems.map(_._4).foldLeft(0)((a,b) => a+b)

 def showAnswers() = {
   "div" #>  questItems.map(q =>
   "section" #> mkQuestHTML(q._1, q._2, q._3, q._4) )
 }

 def showPupil() = {

   "em *" #> exam.description &
   "a [href]" #> ("/educontent/showexams/" + exam._id.toString) &
   "#namePupil *" #>  ansEx.authorName &
   "#codeGroup *" #> ansEx.code &
   "small *" #> exam.className &
   "#max [value]" #> maxPoints.toString
 }

  def getPoints() = {
    val data = "[" + ansEx.answers.map(_.json).mkString(",") + "]"

    "#pointsGain" #> SHtml.ajaxText(data, (str) => {

      //println( "========== json data: " + str);
      val newData = createFromJsonList(str)
      val ansNew = ansEx.answers.map(a => {

        a.p = newData.find(ae => ae.q == a.q) match {
          case Some(ansIt) => ansIt.p
          case _ => a.p
        }
        a
      })

      ansEx.answers = ansNew
      ansEx.max = maxPoints
      ansEx.save
    })

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
          <input type="number" min="0" maxlength="5" max={pktA.toString} name={"zad_" + nr.toString}
          value={pktG.toString} onchange="checkExam.reCountData()"/></div>
        <div class="panel-body">
          <div class="questionText">
            {Unparsed(question.question)}
          </div>
          {createAnswers(question.answers, question.fake, ans)}
        </div>
      </div>
    </section>
  }

  protected def createAnswers(good:List[String], fake:List[String], ans:String) = {


    if(fake.length == 0) {
      if(good.length > 0)  {
        val addC = if(good.exists(_ == ans)) " alert-success" else " alert-danger"
          <div class={"answerBox alert" + addC}  name="one">{Unparsed(ans)}</div>
      }
      else  <div  name="open" class="well"><pre>{ans}</pre></div>
    }
    else {
      val ansList = ans.split(",;;,")
      val all = fake.map(s => {
        val isAns = ansList.exists(_ == s)
        val addC = if(isAns) " glyphicon-check" else " glyphicon-unchecked"
        val col = if(isAns) " list-group-item-danger" else " list-group-item-success"
        <li class={"list-group-item " + col}>
        <span class={"glyphicon" + addC}></span> {Unparsed(s)} </li>}) ++
        good.map(s => {
          val isAns = ansList.exists(_ == s)
          val addC = if(isAns) " glyphicon-check" else " glyphicon-unchecked"
          val col = if(isAns) " list-group-item-success" else " list-group-item-danger"
        <li class={"list-group-item" + col}>
          <span class={"glyphicon" + addC}></span> {Unparsed(s)} </li>})

      <div class="answerBox" name="test"><ul class="list-group">{all}</ul></div>
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

  private def createFromJsonList(jsonStr: String) = {

    var data:List[AnswerItem] = Nil
    implicit val formats = DefaultFormats
    try {
      val json = parse(jsonStr)
      data = json.extract[List[AnswerItem]]
    } catch {case _ : Throwable => println("ERROR PARSER JSON PERFORMEXAMS")}
    data
  }

}
