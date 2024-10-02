package eu.brosbit.opos.snippet.register

import eu.brosbit.opos.lib.{Formater, PasswordRandom}
import eu.brosbit.opos.model.{ClassModel, User}
import net.liftweb.common.Full
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JE.JsFunc
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Alert
import net.liftweb.mapper.By
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

class PupilPasswordsSn extends BaseTeacher {


  def dataTable() = {
    val idClass = ClassChoose.get
    if (idClass == 0) S.redirectTo("teacher/index")
    val classModel = ClassModel.find(idClass) match {
      case Full(theClass) => theClass
      case _ => S.redirectTo("teacher/index")
    }
    val pupils = User.findAll(By(User.classId, classModel), By(User.role, "u"))

    "tr" #> pupils.map(pupil => {
        ".id *" #> pupil.id.get.toString &
        ".reversefullname *" #> pupil.getFullNameReverse &
        ".pesel *" #> pupil.pesel.get &
        ".password *" #> pupil.passwordGenerated.get
    })
  }

  def editAjax(): CssSel = {
    var id = ""
    var fullName = ""
    var password = ""
    var pesel = ""

    def save(): JsCmd = {
      User.find(id) match {
        case Full(pupil) => {
          password = PasswordRandom.create()
          pupil.password.set(password)
          pupil.passwordGenerated(password).validated(true).save
          pupil.validate
          id = pupil.id.toString
          JsFunc("passwordsAdd.setNewPass", id, password).cmd
        }
        case _ => Alert("Nie ma takiego ucznia")
      }
    }

    val form = "#id" #> SHtml.text(id, id = _) &
      "#pesel" #> SHtml.text(pesel, pesel = _) &
      "#reversefullname" #> SHtml.text(fullName, fullName = _) &
      "#password" #> SHtml.text(password, password = _) &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))
  }

}
