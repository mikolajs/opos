/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.model 

    import net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._

    class UserChangeList extends LongKeyedMapper[UserChangeList] with IdPK {
      def getSingleton = UserChangeList

      object firstName extends MappedString(this, 30)
      object lastName extends MappedString(this, 40)
      object email extends MappedEmail(this, 48)
      object passStr extends MappedString(this, 12)
      object phone extends MappedString(this, 12)
      object date extends MappedDate(this)
      object user extends MappedLongForeignKey(this, User)

    }

    object UserChangeList extends UserChangeList with LongKeyedMetaMapper[UserChangeList] {
    }


