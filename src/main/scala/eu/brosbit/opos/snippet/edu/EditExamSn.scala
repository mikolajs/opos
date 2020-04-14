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

  var examId = S.param("id").openOr("0")
  val userId = user.id.get
  val exam = Exam.find(examId).getOrElse(Exam.create)

  //working ....
  def editExam() = {

    var quizzes = exam.quizzes.map(q => q.toString).mkString(";")
    var multi = exam.multi
    var attach = exam.attach
    var description = exam.description
    var keys = exam.keys.mkString(";")
    var startExam = Formater.strForDateTimePicker(if(exam.start == 0L) new Date() else new Date(exam.start))
    var endExam =  Formater.strForDateTimePicker(if(exam.end == 0L) new Date() else new Date(exam.end))
    var classId = exam.classId.toString


    //find exam and intitate pools
    val classes = ClassModel.findAll().map(cl => (cl.id.toString, cl.classString()))


    def save()  {
      println("========= save quiz ========")
      if (exam.authorId != 0L && exam.authorId != userId) return
      exam.description = description
      if (exam.authorId == 0L) exam.authorId = userId
      exam.subjectId = tryo(subjectId).openOr(0L)
      exam.subjectName = findSubjectName(exam.subjectId)
      exam.multi = multi
      exam.attach = attach
      exam.quizzes = quizzes.split(';').map(q => new ObjectId(q)).toList
      exam.keys = keys.split(';').toList
      exam.start = Formater.fromStringDataTimeToDate(startExam).getTime
      exam.end = Formater.fromStringDataTimeToDate(endExam).getTime
      exam.classId = tryo(classId.toLong).getOrElse(0)
      exam.className = classes.filter(c => c._1 == classId) match {
        case cl :: rest => cl._2
        case _ => "Brak"
      }
      exam.save
      S.redirectTo("/educontent/editexam/" + exam._id.toString)
    }

    def delete() {
      println("========= delete quiz ========")
      if (exam.authorId != 0L || userId == exam.authorId) {
        //dodać wyszukiwanie sprawdzianów  uczniów i informację o konieczności ich skasowania
        ExamAnswer.findAll(("exam" -> examId)).foreach(exAn => {
          println(exAn.authorName)
          exAn.delete
        }
        )
        exam.delete
        S.redirectTo("/educontent/quizzes")
      } else S.warning("Nie jesteś autorem lub sprawdzianu jeszcze nie ma.")
    }



      "#descriptionExam" #> SHtml.textarea(description, x => description = x.trim) &
      "#testsList" #> SHtml.text(quizzes, x =>  quizzes = x.trim) &
      "#keysList" #> SHtml.text(keys, x =>  keys = x.trim) &
      "#classExam" #> SHtml.select(classes, Full(classId), classId = _ ) &
      "#multiExam" #> SHtml.checkbox(multi, multi = _) &
      "#attachExam" #> SHtml.checkbox(attach, attach = _) &
      "#startExam" #> SHtml.text(startExam, x =>  startExam = x.trim) &
      "#endExam" #> SHtml.text(endExam, x =>  endExam = x.trim) &
      "#saveExam" #> SHtml.submit("Zapisz", save) &
      "#deleteExam" #> SHtml.submit("Usuń", delete)


  }

  def showInfo = "span *" #> subjectNow.name

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


}