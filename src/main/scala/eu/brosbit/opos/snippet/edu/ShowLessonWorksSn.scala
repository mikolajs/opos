package eu.brosbit.opos.snippet.edu

import java.util.Date
import eu.brosbit.opos.lib.Formater
import eu.brosbit.opos.model.edu.Work
import net.liftweb.http.S
import _root_.net.liftweb.util.Helpers._
import net.liftweb.util.CssSel

class ShowLessonWorksSn {
  val workId:String = S.param("id").openOr("")
  if(workId.isEmpty) S.redirectTo("/educontent/works")
  private val work = Work.find(workId).getOrElse(Work.create)

  def showInfo(): CssSel = {
    "span *" #> work.groupName &
      "h3 *" #> work.lessonTitle &
      "small *" #> ("od " + Formater.formatTime(new Date(work.start)) +
        " do " + Formater.formatTime(new Date(work.end)))
  }
}
