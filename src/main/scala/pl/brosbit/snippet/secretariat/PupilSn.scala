/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.secretariat

import _root_.net.liftweb.util._
import _root_.net.liftweb.http.SHtml
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.By
import Helpers._
import _root_.pl.brosbit.model._
import _root_.pl.brosbit.lib.Formater
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._

class PupilSn {

  def pupilList() = {

    val pupils = User.findAll(By(User.role, "u"))
    "tr" #> pupils.map(pupil => {
      "tr [class]" #> {
        if (pupil.scratched.get) "scratched" else ""
      } &
        ".id" #> <td>
          {pupil.id.get.toString}
        </td> &
        ".firstname" #> <td>
          {pupil.firstName.get}
        </td> &
        ".lastname" #> <td>
          {pupil.lastName.get}
        </td> &
        ".birthdate" #> <td>
          {Formater.formatDate(pupil.birthDate.get)}
        </td> &
        ".classInfo" #> <td>
          {pupil.classInfo.get}
        </td> &
        ".pesel" #> <td>
          {pupil.pesel.get}
        </td>
    })
  }

  def editAjax() = {
    var id = ""
    var firstName = ""
    var lastName = ""
    var birthDate = ""
    var pesel = ""
    var classId = ""
    var errorInfo = ""

    def save() = {
      val pupil = User.find(id).openOr(User.create)
      ClassModel.find(classId) match {
        case Full(classModel) =>
          pupil.classInfo(classModel.classString()).
            classId(classModel.id.get)

        case _ =>
      }
      pupil.birthDate(Formater.fromStringToDate(birthDate)).firstName(firstName).
        lastName(lastName).pesel(pesel).scratched(false).role("u").
        password(Helpers.randomString(10)).save
      if (id == "") {
        id = pupil.id.toString
        JsFunc("editForm.insertRowAndClear", id).cmd
      }
      else {
        id = pupil.id.toString
        JsFunc("editForm.insertRowAndClose", id).cmd
      }

    }

    def delete() = {
      User.find(id) match {
        case Full(user) => {
          user.scratched(true).email("").save
          JsFunc("editForm.scratchRow", id).cmd
        }
        case _ => Alert("Nie ma takiego ucznia")
      }
    }
    val classList = ClassModel.findAll.map(classModel => (classModel.id.toString, classModel.classString()))

    val form = "#id" #> SHtml.text(id, x => id = x.trim, "readonly" -> "readonly") &
      "#lastname" #> SHtml.text(lastName, x => lastName = x.trim) &
      "#firstname" #> SHtml.text(firstName, x => firstName = x.trim) &
      "#birthdate" #> SHtml.text(birthDate, x => birthDate = x.trim) &
      "#pesel" #> SHtml.text(pesel, x => pesel = x.trim) &
      "#classInfo" #> SHtml.select(classList, Full(""), classId = _) &
      "#addInfo *" #> errorInfo &
      "#delete" #> SHtml.ajaxSubmit("Usuń", delete, "type" -> "image",
        "onclick" -> "return confirm('Na pewno usunąć klasę?')") &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }

}

