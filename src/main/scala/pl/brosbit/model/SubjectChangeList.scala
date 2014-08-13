/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.model 

    import net.liftweb.mapper._
    import _root_.net.liftweb.util._
    import _root_.net.liftweb.common._

    class SubjectChangeList extends LongKeyedMapper[SubjectChangeList] with IdPK {
      def getSingleton = SubjectChangeList

      object name extends MappedString(this, 40)
      object short extends MappedString(this, 5)
      object nr extends MappedInt(this)
      object date extends MappedDate(this)
    }

    object SubjectChangeList extends SubjectChangeList with LongKeyedMetaMapper[SubjectChangeList] {

    }
