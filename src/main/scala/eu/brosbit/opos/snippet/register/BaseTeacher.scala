/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package eu.brosbit.opos.snippet.register

import _root_.java.util.{Date, GregorianCalendar, TimeZone}
import _root_.scala.xml.{NodeSeq, Text, XML}
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{SHtml, S, SessionVar}
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{By, OrderBy, Ascending}
import Helpers._
import eu.brosbit.opos.model._

class BaseTeacher {
  val user = User.currentUser.openOrThrowException("Nauczyciel musi być zalogowany!")

  def choosenClass() = {
    if (ClassChoose.is == 0) S.redirectTo("/register/index")
    else "#choosenclass *" #> ClassString.is
  }


  protected def isBriningUp = {
    val theClass = ClassModel.find(ClassChoose.get).openOr(ClassModel.create)
    if (user.id.get == theClass.teacher.obj.get.id.get || user.role == "a") true
    else false
  }

  protected def isAdmin = user.role.get == "a"

}


