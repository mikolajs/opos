package eu.brosbit.opos.snippet.edu

import java.util.Date
import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.edu.{AnswerWorkItem, LessonCourse, Work, WorkAnswer}
import eu.brosbit.opos.snippet.common.WorkCommon
import eu.brosbit.opos.snippet.view.BaseSnippet
import net.liftweb.json.JsonDSL._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers._
import net.liftweb.util.CssSel

import scala.xml.{NodeSeq, Unparsed}

class CheckWorkSn extends BaseSnippet with WorkCommon {

  protected val studentWorkId:String = S.param("id").getOrElse("0")
  protected val workId:String  = S.param("workId").getOrElse("0")
  protected val answer:WorkAnswer = WorkAnswer.find(studentWorkId).getOrElse(S.redirectTo("/educontent/showworks/"+workId))
  protected val work:Work = Work.find(answer.work).getOrElse(S.redirectTo("/educontent/showworks/"+workId))
  protected val lesson:LessonCourse = LessonCourse.find(work.lessonId).getOrElse(LessonCourse.create)


 def showInfo():CssSel = {
    WorkAnswer.update("_id"->answer._id.toString, "$set" -> ("pupilChanged" -> false))
   "#lesson *" #> work.lessonTitle &
   "a [href]" #> s"/educontent/showworks/$workId" &
   "#descript *" #> Unparsed(mkDescription(work.description)) &
   "#subject *" #> work.subjectName &
   "#namePupil *" #> answer.authorName &
   "#classInfo *" #> work.className &
   "#questions" #> createQuestions(lesson.contents, answer)
 }

  def addMessage():CssSel = {
    var link = ""
    var messageStr = ""
    var quizId = ""
    def send():JsCmd = {
      if(link.trim.isEmpty && messageStr.trim.nonEmpty){
        val ansItem = AnswerWorkItem(Formater.formatTime(new Date()), user.getFullName,
          t = false, quizId, messageStr, l = false)
        WorkAnswer.update("_id"->answer._id.toString,
          ("$set" -> ("teacherChanged" -> true)) ~ ("$addToSet" -> ("answers" -> ansItem.toJson)))
      } else if(link.nonEmpty) {
        val ansItem = AnswerWorkItem(Formater.formatTime(new Date()), user.getFullName, t = false, quizId, link, l = true)
        WorkAnswer.update("_id"->answer._id.toString,
          ("$set" -> ("teacherChanged" -> true)) ~ ("$addToSet" -> ("answers" -> ansItem.toJson)))
      }
      Run("checkWork.messageSuccess()")

    }
    val ajaxForm = "#date" #> <span></span> &
      "#quizId" #> SHtml.text(quizId , quizId = _) &
      "#comment" #> SHtml.textarea(messageStr, t => messageStr = t.trim) &
      "#link" #> SHtml.text(link, b => link = b.trim) &
      "#send" #> SHtml.ajaxSubmit("Wyślij", send) andThen SHtml.makeFormsAjax
    "form" #> ((in: NodeSeq) => ajaxForm(in))
  }


}
