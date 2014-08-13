/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.page

import _root_.pl.brosbit.model.page._
import pl.brosbit.model._
import net.liftweb.http._
import scala.xml.Text
import net.liftweb.common._
import net.liftweb.util.Helpers._

class RegisterSwitcherSn {
  def redir() = {
    User.currentUser match {
      case Full(user) => user.role.is match {
        case char: String if (char == "a" || char == "n") => S.redirectTo("/teacher/index")
        case "s" => S.redirectTo("/secretariat/index")
        case char: String if (char == "u" || char == "r") => S.redirectTo("/view/")
        case _ => S.redirectTo("/user_mgt/login")
      }
      case _ => S.redirectTo("/user_mgt/login")
    }
    "#info" #> Text("Błąd")
  }
}

