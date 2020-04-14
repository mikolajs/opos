/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package eu.brosbit.opos.snippet.doc

import eu.brosbit.opos.model._

trait BaseDoc {
  val user = User.currentUser.openOrThrowException("Nauczyciel musi być zalogowany!")


  protected def isAdmin = user.role.get == "a"

  protected def isTeacher = user.role.get == "d" || user.role.get == "t" || isAdmin

}


