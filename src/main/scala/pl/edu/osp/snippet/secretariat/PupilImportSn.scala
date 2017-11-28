/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.snippet.secretariat

import java.io.{ByteArrayInputStream, StringReader}

import _root_.net.liftweb.util._
import net.liftweb.http.{FileParamHolder, S, SHtml, SessionVar}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.By
import Helpers._
import _root_.pl.edu.osp.model._
import _root_.pl.edu.osp.lib.{Formater, Validator}
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JE._
import pl.edu.osp.model.User


class PupilImportSn {

  if(ClassChoose.get == 0L)  {
    ClassChoose(S.param("id").getOrElse(ClassModel.findAll().head.id.toString()).toLong)
  }
  val classMod = ClassModel.find(By(ClassModel.id, ClassChoose.get)).getOrElse(ClassModel.create)

  def pupilList() = {
    val pupils = User.findAll(By(User.role, "u"), By(User.classId, ClassChoose.get))
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
        </td> &
        ".email" #> <td>
          {pupil.email.get}
        </td>
    })
  }



  def insertPupils() = {
    var fileHold: Box[FileParamHolder] = Empty

    def read(): Unit ={
      def isCorrect = fileHold match {
        case Full(FileParamHolder(_, mime, fileNameIn, data)) => {
          if(mime.toString != "text/csv") false
          else true
        }
        case Full(_) => {
          S.error("Nieprawidłowy format pliku!")
          false
        }
        case _ => {
          S.error("Brak pliku?")
          false
        }
      }

      val csv = new String( fileHold.openOrThrowException("File holder not Found").file)
      csv.split("\n").map(s => s.trim).map(s => {
        val array = s.split(";").map(_.trim)
        if(validateArray(array)) {
          User.find(By(User.pesel, array(3))) match {
            case Full(u) => println("Uczeń " + u.shortInfo +  " istniej!!!!!!!!!!")
            case Empty => {
              val u = User.create
              u.firstName(array(0)).lastName(array(1)).
                email(array(2)).pesel(array(3)).classId(classMod.id.get).
                classNumber(classMod.level.get).classInfo(classMod.classString)
                .role("u").birthDate(Validator.fromPeselToBithDate(array(3)))
              if (array.length == 5) u.phone(array(4))
              u.save
            }

          }
        }
        else println("Nieprawidłowa linia")

      })

    }
    "#classInfo *" #> classMod.classString() &
    "#fileAdd" #> SHtml.fileUpload(x => fileHold = Full(x)) &
    "#add" #> SHtml.submit("Wczytaj CSV", read)
  }

  private def validateArray(arr:Array[String]) = {
    arr match {
      case a if a.length < 4 => {println ("długość") ; false}
      case a if a(0).length < 3 => {println ("imie"); false}
      case a if a(1).length < 3 => {println ("nazwisko"); false}
      case a if !Validator.isEmail(a(2))  => {println ("email"); false}
      case a if !Validator.validatePesel(a(3)) => {println ("poprawność pesel"); false}
      case _ => true
    }
  }



}

