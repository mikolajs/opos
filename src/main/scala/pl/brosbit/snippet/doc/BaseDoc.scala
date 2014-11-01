/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.snippet.doc 

import pl.brosbit.model._

trait BaseDoc  {
   val user = User.currentUser.openOrThrowException("Nauczyciel musi być zalogowany!")
   
	
	protected def isAdmin = user.role.is == "a"
	
}


