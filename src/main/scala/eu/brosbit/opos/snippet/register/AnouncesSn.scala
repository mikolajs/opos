package eu.brosbit.opos.snippet.register

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model.page._
import _root_.eu.brosbit.opos.model._
import _root_.eu.brosbit.opos.lib.Formater
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._


class AnouncesSn extends BaseTeacher {

  val classId = ClassChoose.is

  def dataTable() = {
    val anounces = MessagePupil.findAll(("classId" -> classId) ~ ("opinion" -> false), ("_id" -> 1))
    "tr" #> anounces.map(anounce => {
      ".id *" #> anounce._id.toString &
        ".dateIn *" #> Formater.formatDate(new Date(anounce._id.getTime())) &
        ".contentData *" #> Unparsed(anounce.body) &
        ".teacher *" #> anounce.teacherName
    })
  }

  def saveData() = {
    var id = ""
    var content = ""

    def save(): JsCmd = {
      val anounce = MessagePupil.find(id).getOrElse(MessagePupil.create)
      if (id == "" || user.id.get == anounce.teacherId) {
        if (anounce.classId == 0L) anounce.classId = classId
        anounce.body = content

        if (id == "") {
          anounce.teacherId = user.id.get
          anounce.teacherName = user.getFullName
          anounce.dateStr = Formater.formatDate(new Date)
          anounce.save
          JsFunc("editForm.insertRowAndClear", anounce._id.toString).cmd
        }
        else {
          anounce.save
          JsFunc("editForm.insertRowAndClose", anounce._id.toString).cmd
        }

      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }


    val form = "#id" #> SHtml.text(id, id = _, "style" -> "display:none;") &
      "#contentData" #> SHtml.textarea(content, content = _) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image") andThen SHtml.makeFormsAjax


    "#loggedTeacher" #> <input id="loggedTeacher" value={user.getFullName}
                               type="text" style="display:none;"/> &
      "form" #> (in => form(in))
  }

}
