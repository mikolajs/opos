package eu.brosbit.opos.snippet.register

import java.util.Date
import scala.xml.Unparsed
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.eu.brosbit.opos.model._
import _root_.eu.brosbit.opos.lib.Formater
import _root_.net.liftweb.http.{S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._
import _root_.net.liftweb.mapper._


class OpinionsSn extends BaseTeacher {

  val classId = ClassChoose.is

  def dataTable() = {
    val opinions = MessagePupil.findAll(("classId" -> classId) ~ ("opinion" -> true), ("_id") -> 1)
    "tr" #> opinions.map(opinion => {
      ".id *" #> opinion._id.toString &
        ".dateIn *" #> Formater.formatDate(opinion._id.getDate()) &
        ".pupil *" #> opinion.pupilName &
        ".contentData *" #> Unparsed(opinion.body) &
        ".teacher *" #> opinion.teacherName

    })
  }

  def saveData() = {

    var id = ""
    var content = ""
    var pupilId = ""

    def save(): JsCmd = {
      val opinion = MessagePupil.find(id).getOrElse(MessagePupil.create)

      if (opinion.teacherId == 0L || user.id.get == opinion.teacherId) {
        opinion.body = content
        opinion.teacherId = user.id.get
        opinion.teacherName = user.getFullName
        val pupil = User.find(pupilId).getOrElse(User.create)
        opinion.pupilName = pupil.shortInfo
        opinion.pupilId = pupil.id.get
        opinion.classId = ClassChoose.is
        opinion.opinion = true
        opinion.dateStr = Formater.formatDate(new Date)
        opinion.save
        JsFunc("editForm.insertRowAndClose", opinion._id.toString).cmd

      }
      else Alert("Tylko właściciel może zmienić wpis!")
    }

    val pupils = User.findAll(By(User.classId, classId), By(User.role, "u")).filter(!_.scratched.get).map(user => {
      (user.id.get.toString, user.shortInfo)
    })

    val form = "#id" #> SHtml.text(id, id = _, "style" -> "display:none;") &
      "#pupil" #> SHtml.select(pupils, Full(""), pupilId = _) &
      "#contentData" #> SHtml.textarea(content, content = _) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image") andThen SHtml.makeFormsAjax


    "#loggedTeacher" #> <input id="loggedTeacher" value={User.currentUser.openOrThrowException("No user").getFullName}
                               type="text" style="display:none;"/> &
      "form" #> (in => form(in))
  }

}
