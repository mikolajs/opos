package eu.brosbit.opos.snippet.edu

//import scala.xml.{Text, Unparsed, NodeSeq}
import _root_.net.liftweb._
import http.{S, SHtml}
import common._
import util._
import eu.brosbit.opos.model._
import edu._
import Helpers._
import json.JsonDSL._
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.Formater
import java.util.Date

class EditExamSn extends BaseResourceSn {

  private val examId = S.param("id").openOr("0")
  private val examCopyId = S.param("c").openOr("0")
  private val userId = user.id.get
  private val exam = if(examCopyId == "0") Exam.find(examId).getOrElse(Exam.create) else mkExamCopy()
  private val groups = Groups.findAll.filter( gr => gr.authorId == userId).map(gr => ("_" + gr._id.toString, gr.name))

  def editExam(): CssSel = {
    var quizzes = exam.quizzes.map(groupRows => groupRows.map(q => q.q.toString + "," + q.p.toString).mkString(";")).mkString("|")
    var multi = exam.multi
    var attach = exam.attach
    var description = exam.description
    var keys = exam.keys.mkString(";")
    var startExam = Formater.strForDateTimePicker(if(exam.start == 0L) new Date() else new Date(exam.start))
    var endExam =  Formater.strForDateTimePicker(if(exam.end == 0L) new Date() else new Date(exam.end))
    var groupId = exam.groupId

    def save()  {
      //println("========= save quiz ========")
      if (exam.authorId != 0L && exam.authorId != userId) return
      exam.description = description
      if (exam.authorId == 0L) exam.authorId = userId
      exam.subjectId = tryo(subjectId).openOr(0L)
      exam.subjectName = findSubjectName(exam.subjectId)
      exam.multi = multi
      exam.attach = attach
      exam.quizzes = quizzes.split('|').map(_.trim).filter(_.nonEmpty).map(groupRows =>
        groupRows.split(';').map(_.trim).filter(_.nonEmpty).map(q => {
         val elem = q.split(',')
          if(elem.size == 2) QuestElem(new ObjectId(elem(0)), tryo(elem(1).toInt).getOrElse(1))
          else QuestElem(new ObjectId(""), -1)
       }).filter(_.p > 0).toList).toList
      exam.keys = keys.split(';').toList
      exam.start = Formater.fromStringDataTimeToDate(startExam).getTime
      exam.end = Formater.fromStringDataTimeToDate(endExam).getTime
      exam.groupId = groupId
      exam.groupName = groups.find(gr => gr._1 == groupId).map(_._2).getOrElse("Brak")
      exam.save
      S.redirectTo("/educontent/editexam/" + exam._id.toString)
    }

    def delete() {
      //println("========= delete quiz ========")
      if (exam.authorId != 0L || userId == exam.authorId) {
        //dodać wyszukiwanie sprawdzianów  uczniów i informację o konieczności ich skasowania
        ExamAnswer.findAll("exam" -> examId).foreach(exAn => {
          println(exAn.authorName)
          exAn.delete
        })
        exam.delete
        S.redirectTo("/educontent/exams")
      } else S.warning("Nie jesteś autorem lub sprawdzianu jeszcze nie ma.")
    }

      "#descriptionExam" #> SHtml.textarea(description, x => description = x.trim) &
      "#testsList" #> SHtml.text(quizzes, x =>  quizzes = x.trim) &
      "#questsJson" #> SHtml.text(questionsListJson(), _ => Unit ) &
      "#keysList" #> SHtml.text(keys, x =>  keys = x.trim) &
      "#classExam" #> SHtml.select(groups, Full(groupId), groupId = _ ) &
      "#multiExam" #> SHtml.checkbox(multi, multi = _) &
      "#attachExam" #> SHtml.checkbox(attach, attach = _) &
      "#startExam" #> SHtml.text(startExam, x =>  startExam = x.trim) &
      "#endExam" #> SHtml.text(endExam, x =>  endExam = x.trim) &
      "#saveExam" #> SHtml.submit("Zapisz", save) &
      "#deleteExam" #> SHtml.submit("Usuń", delete)
  }

  def showInfo: CssSel = "#subject *" #> subjectNow.name

  def questionsListJson() : String = {
    val questsElementsList = exam.quizzes
    var letter = 64
    val grString = questsElementsList.map(qel => {
      letter += 1
      val quests = QuizQuestion.findAll("_id" -> ("$in" -> qel.map(_.q.toString)))
      val questsStrings = quests.map(quest => {
        mkJsonStringQuest(quest, qel.find(qe => qe.q.toString == quest._id.toString).map(_.p).getOrElse(1))
      }).mkString(",")
      s""" {"gr":"${letter.toChar.toString}", "q":[$questsStrings] }"""
    }).mkString(",")
    s""" {"groups":[$grString] """
  }
/*
  def showAllQuizzes() = {

    "li" #> Quiz.findAll(("authorId" -> user.id.get) ~ ("subjectId" -> subjectNow.id)).map(quiz => {
      "li [id]" #> quiz._id.toString &
      ".titleQuiz *" #> quiz.title
    })
  }

 def showExamQuizzes() = {

   "li" #> Quiz.findAll(("authorId" -> user.id.get) ~ ("_id" -> ("$in" -> exam.quizzes.map(_.toString)))).map(quiz => {
      "li [id]" #> quiz._id.toString &
      ".titleQuiz * " #> quiz.title
    })

 }
 */


//  private def mkQuestBoxWithPoints(questElems:List[QuestElem]) = {
//    val qes = questElems.map(qe => qe.q.toString)
//    val quests = QuizQuestion.findAll(( "_id" -> ("$in" -> qes )))
//    questElems.map(qe => mkBox(quests.find(q => q._id.toString == qe.q.toString).getOrElse(QuizQuestion.create), qe.p))
//  }

  private  def mkJsonStringQuest(quest: QuizQuestion, p:Int):String ={
    import net.liftweb.json._
    val json = ("id" -> quest._id.toString) ~ ("nr" -> quest.nr) ~ ("depart" -> quest.department) ~("lev" -> getQuestLevelString(quest.lev)) ~
      ("dif" -> quest.dificult) ~ ("p" -> p) ~ ("q" -> quest.question) ~ ("info" -> quest.info)
    compact(render(json))
  }

  private def getQuestLevelString(l:Int) = {
    l match {
      case 1 => "podstawowy"
      case 2 => "rozszerzony"
      case 3 => "konkursowy"
      case _ => "-"
    }
  }

  private def mkExamCopy(): Exam = {
    val exOrigin = Exam.find(examCopyId).getOrElse(Exam.create)
    val e = Exam.create
    e.quizzes = exOrigin.quizzes
    e.attach = exOrigin.attach
    e.multi = exOrigin.multi
    e.authorId = exOrigin.authorId
    e.subjectId = exOrigin.subjectId
    e.subjectName = exOrigin.subjectName
    e
  }

//  private def mkBox(quest: QuizQuestion, p:Int) = {
//    <li id={quest._id.toString}>
//      <div class="infoText">{"[" + quest.info + "]"}</div>
//      <div class="question">
//        {Unparsed(quest.question)}
//      </div>
//      <div class="questInfo">
//        <span class="nr">{quest.nr.toString} </span>  |
//        <span class="department">
//          {quest.department}
//        </span> |
//        <span class="level">
//          {(quest.lev match {
//          case 1 => "podstawowy"
//          case 2 => "rozszerzony"
//          case 3 => "konkursowy"
//          case _ => "-"
//        }) + " " +
//          (quest.dificult match {
//            case 3 => "★★"
//            case 2 => "★"
//            case _ => " "})}</span>
//        | Pukty:
//        <input class="points" type="number" value={p.toString} />
//        <span class="btn btn-sm btn-danger" onclick="editQuiz.removeLi(this);">
//          <span class="glyphicon glyphicon-remove"></span>
//        </span>
//      </div>
//    </li>
//  }


}