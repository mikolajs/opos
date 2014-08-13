/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.model

import net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._

class ClassModel extends LongKeyedMapper[ClassModel] with IdPK {
    def getSingleton = ClassModel

    object level extends MappedInt(this)
    object division extends MappedString(this, 2)
    object descript extends MappedString(this, 50)
    object teacher extends MappedLongForeignKey(this, User)
    object scratched extends MappedBoolean(this)

    def classString(): String = level.is.toString + division.is
    def shortInfo(): String = classString() + " [" + id.is.toString + "]"
}

object ClassModel extends ClassModel with LongKeyedMetaMapper[ClassModel] {

}

