/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.snippet.register


import _root_.java.util.{Date, GregorianCalendar, TimeZone}
import _root_.scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{SHtml, S}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import _root_.pl.edu.osp.model.{User, ClassModel}
import _root_.pl.edu.osp.lib.Formater
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._

class PupilDataSn extends BaseTeacher {


  def dataTable() = {
    val idClass = ClassChoose.get
    if (idClass == 0) S.redirectTo("teacher/index")
    val classModel = ClassModel.find(idClass) match {
      case Full(theClass) => theClass
      case _ => S.redirectTo("teacher/index")
    }
    val pupils = User.findAll(By(User.classId, classModel), By(User.role, "u"))

    "tr" #> pupils.map(pupil => {
      "tr [class]" #> {
        if (pupil.scratched.get) "scratched" else ""
      } &
        ".id *" #> pupil.id.get.toString &
        ".number *" #> pupil.classNumber.get.toString &
        ".reversefullname *" #> pupil.getFullNameReverse &
        ".secondname *" #> pupil.secondName.get &
        ".email *" #> pupil.email.get &
        ".phone *" #> pupil.phone.get &
        ".pesel *" #> pupil.pesel.get &
        ".birthdate *" #> Formater.formatDate(pupil.birthDate.get) &
        ".birthplace *" #> pupil.birthPlace.get &
        ".birthdistrict *" #> pupil.birthDisctrict &
        ".address *" #> pupil.address.get

    })
  }

  def editAjax() = {
    var id = ""
    var secondName = ""
    var number = ""
    var email = ""
    var phone = ""
    var birthPlace = ""
    var birthDistrict = ""
    var address = ""
    var errorInfo = ""

    def save(): JsCmd = {
      User.find(id) match {
        case Full(pupil) => {
          pupil.address(address.trim).birthPlace(birthPlace.trim).
            secondName(secondName.trim).birthDisctrict(birthDistrict.trim).
            email(email.trim).classNumber(tryo(number.toInt).openOr(0)).phone(phone.trim).validated(true).save
          id = pupil.id.toString
          JsFunc("editForm.editRow", id).cmd
        }
        case _ => Alert("Nie ma takiego ucznia")
      }
    }

    val numbers = (1 to 40).toList.map(i => (i.toString, i.toString))

    val form = "#id" #> SHtml.text(id, id = _, "readonly" -> "readonly") &
      "#number" #> SHtml.select(numbers, Full("40"), number = _) &
      "#secondname" #> SHtml.text(secondName, secondName = _) &
      "#email" #> SHtml.text(email, email = _) &
      "#phone" #> SHtml.text(phone, phone = _) &
      "#birthplace" #> SHtml.text(birthPlace, birthPlace = _) &
      "#birthdistrict" #> SHtml.text(birthDistrict, birthDistrict = _) &
      "#address" #> SHtml.text(address, address = _) &
      "#addInfo *" #> errorInfo &
      "#save" #> SHtml.ajaxSubmit("Zapisz", save, "type" -> "image",
        "onclick" -> "return validateForm();") andThen SHtml.makeFormsAjax

    "form" #> (in => form(in))
  }


}

