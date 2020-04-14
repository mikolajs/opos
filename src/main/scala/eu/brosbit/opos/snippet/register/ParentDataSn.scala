/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package eu.brosbit.opos.snippet.register

import _root_.java.util.{Date, GregorianCalendar, TimeZone}
import _root_.scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{SHtml, S}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import _root_.eu.brosbit.opos.model._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class ParentDataSn extends BaseTeacher {

  def dataTable() = {
    val idClass = ClassChoose.get
    if (idClass == 0) S.redirectTo("/teacher/index")
    val classModel = ClassModel.find(idClass) match {
      case Full(theClass) => theClass
      case _ => S.redirectTo("/teacher/index")
    }
    val pupils = User.findAll(By(User.classId, classModel), By(User.role, "u"))

    "tr" #> pupils.map(pupil => {
      val father = pupil.father.obj.getOrElse(User.create)
      val mather = pupil.mather.obj.getOrElse(User.create)
      "tr [class]" #> {
        if (pupil.scratched.get) "scratched" else ""
      } &
        ".id *" #> pupil.id.get.toString &
        ".number *" #> pupil.classNumber.get.toString &
        ".reversefullname *" #> pupil.getFullNameReverse &
        ".lastnamefather *" #> father.lastName.get &
        ".firstnamefather *" #> father.firstName.get &
        ".emailfather *" #> father.email.get &
        ".phonefather *" #> father.phone.get &
        ".addressfather *" #> father.address.get &
        ".lastnamemather *" #> mather.lastName.get &
        ".firstnamemather *" #> mather.firstName.get &
        ".emailmather *" #> mather.email.get &
        ".phonemather *" #> mather.phone.get &
        ".addressmather *" #> mather.address.get

    })
  }

  def editAjax() = {
    var id = ""
    var number = ""
    var lastNameFather = ""
    var firstNameFather = ""
    var emailFather = ""
    var phoneFather = ""
    var addressFather = ""
    var lastNameMather = ""
    var firstNameMather = ""
    var emailMather = ""
    var phoneMather = ""
    var addressMather = ""
    var errorInfo = ""

    def save(): JsCmd = {
      if (isBriningUp) {
        User.find(id) match {
          case Full(pupil) => {
            val father = pupil.father.obj.getOrElse(User.create)
            val mather = pupil.mather.obj.getOrElse(User.create)
            father.address(addressFather.trim).phone(phoneFather.trim).
              email(emailFather.trim).lastName(lastNameFather.trim).
              firstName(firstNameFather.trim).role("r").
              scratched(false).save
            mather.address(addressMather.trim).phone(phoneMather.trim).
              email(emailMather.trim).lastName(lastNameMather.trim).
              firstName(firstNameMather.trim).role("r").
              scratched(false).save
            pupil.father(father.id.get).mather(mather.id.get).save
            JsFunc("editForm.editRow", id).cmd
          }
          case _ => Alert("Nie ma takiego ucznia")
        }
      }
      else {
        Alert("Zmiany może dokonać tylko wychowawca")
      }

    }


    val form = "#id" #> SHtml.text(id, id = _, "readonly" -> "readonly") &
      "#lastnamefather" #> SHtml.text(lastNameFather, lastNameFather = _) &
      "#firstnamefather" #> SHtml.text(firstNameFather, firstNameFather = _) &
      "#emailfather" #> SHtml.text(emailFather, emailFather = _) &
      "#phonefather" #> SHtml.text(phoneFather, phoneFather = _) &
      "#addressfather" #> SHtml.text(addressFather, addressFather = _) &
      "#lastnamemather" #> SHtml.text(lastNameMather, lastNameMather = _) &
      "#firstnamemather" #> SHtml.text(firstNameMather, firstNameMather = _) &
      "#emailmather" #> SHtml.text(emailMather, emailMather = _) &
      "#phonemather" #> SHtml.text(phoneMather, phoneMather = _) &
      "#addressmather" #> SHtml.text(addressMather, addressMather = _) &
      "#addInfo *" #> errorInfo &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }
}

