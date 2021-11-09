
package eu.brosbit.opos.snippet.view


import _root_.net.liftweb.util._
import Helpers._
import net.liftweb.json.JsonDSL._
import eu.brosbit.opos.lib.{Formater, ZeroObjectId}

import java.util.Date
import eu.brosbit.opos.model.edu.{Exam, ExamAnswer, Groups, Work, WorkAnswer}

class ExamsSn extends BaseSnippet  {

  private val groups = Groups.findAll.filter(gr => gr.students.exists(s => s.id == user.id.get)).map("_" + _._id.toString)

  def nowTime():CssSel = {"#nowDate *" #> Formater.formatTime(new Date())}

  def showExams():CssSel = {
    val now = new Date()
    val nowString = Formater.formatTime(now)
    val nowL = now.getTime
    //println(groups)
    val exams = Exam.findAll(("groupId" -> ("$in" -> groups))~( "start" -> ("$lt" -> nowL ))~("end" -> ("$gt" -> nowL)))

    "#contentHere" #> exams.map(ex => {
      mkExamDiv(ex)
    })
  }

  def showChecked():CssSel = {
    val now = new Date()
    val nowL = now.getTime
    val exams = Exam.findAll(("groupId" -> ("$in" -> groups))~( "end" -> ("$lt" -> nowL ))).sortWith((e1, e2) => e1.end > e2.end)
    val answers = ExamAnswer.findAll({"authorId" -> user.id.get})

    "#contentHere" #> exams.map(ex => {
        mkAnsDiv(ex, answers.find(a => a.exam == ex._id).getOrElse(ExamAnswer.create))
      })
  }



  private def mkExamDiv(ex:Exam) = {
    <div class="examBox">
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

  private def mkAnsDiv(ex: Exam, an: ExamAnswer) = {
    val points = an.answers.map(_.p).sum
    val percent = if(an.max == 0) 0.0F else (100.0F* points.toFloat) / an.max.toFloat
    <div class="examBox">
      <h4 class="text-info"> {ex.description} </h4>
      <p>Przedmiot: <strong>{ex.subjectName}</strong></p>
      <p><em>Start: </em> { Formater.strForDateTimePicker(new Date(ex.start))} <br/>
        <em>Koniec: </em> { Formater.strForDateTimePicker(new Date(ex.end))}</p>
        <div class="alert alert-info"> Wynik:  {points.toString + " / " +
          an.max.toString  + " : " + scala.math.round(percent).toString} %
          <a class="btn btn-warning" style="float:right;" href={"/view/showcheckedexam/"+an._id.toString}>Pokaż</a>
        </div>
    </div>
  }

}