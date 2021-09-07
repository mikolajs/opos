package eu.brosbit.opos.snippet.doc

import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._


class ExtraLessonsSn extends BaseDoc {

  def dataTable() = {
    val extraLessons = ExtraLessons.findAll
    "tr" #> extraLessons.map(extraLesson => {
      ".id *" #> extraLesson._id.toString &
        ".title *" #> extraLesson.title &
        ".teacher *" #> extraLesson.teacherName &
        ".description *" #> Unparsed(extraLesson.description) &
        ".when *" #> extraLesson.when &
        "#meetButton [href]" #> ("/documents/addmeetting?id=0&el=" + extraLesson._id.toString)

    })
  }

  def saveAjaxDate() = {
    var id = ""
    var title = ""
    var description = ""
    var when = ""

    def save(): JsCmd = {
      val extraLesson = ExtraLessons.find(id).getOrElse(ExtraLessons.create)
      val isNew = extraLesson.teacherId == 0L
      val user = User.currentUser.get
      if (id == "" || user.id.get == extraLesson.teacherId) {
        extraLesson.title = title.trim()
        extraLesson.description = description.trim()
        extraLesson.when = when.trim()
        if (id == "") {
          extraLesson.teacherId = user.id.get
          extraLesson.teacherName = user.getFullName
        }
        extraLesson.save
        if (isNew) JsFunc("editForm.insertRowAndClear", extraLesson._id.toString).cmd
        else JsFunc("editForm.insertRowAndClose", extraLesson._id.toString).cmd
      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }

    def delete(): JsCmd = {
      if (isAdmin) {
        ExtraLessons.find(id) match {
          case Some(extraLesson) => {
            extraLesson.delete
            JsFunc("editForm.scratchRow", id).cmd
          }
          case _ => Alert("Brak wpisu!")
        }
      } else Alert("Tylko administrator może usunąć wpis!")
    }

    val form = "#id" #> SHtml.text(id, id = _, "style" -> "display:none;") &
      "#title" #> SHtml.text(title, title = _) &
      "#description" #> SHtml.textarea(description, description = _) &
      "#when" #> SHtml.text(when, when = _) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image") &
      "#delete" #> {
        if (isAdmin) SHtml.ajaxSubmit("USUŃ!", delete, "type" -> "image")
        else <span class=" "></span>
      } andThen SHtml.makeFormsAjax

    "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.get.getFullName}
                               type="text" style="display:none;"/> &
      "form" #> (in => form(in))
  }

}
