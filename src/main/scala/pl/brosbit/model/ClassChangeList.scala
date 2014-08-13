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

package net.brosbit4u {
  package model {

    import net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._

    class ClassChangeList extends LongKeyedMapper[ClassChangeList] with IdPK {
      def getSingleton = ClassChangeList

      object level extends MappedInt(this)
      object division extends MappedString(this, 2)
      object descript extends MappedString(this, 80)
      object date extends MappedDate(this)

      def info(): String = level.is.toString + division.is + " " + date.is.toString

    }

    object ClassChangeList extends ClassChangeList with LongKeyedMetaMapper[ClassChangeList] {

    }

  }
}
