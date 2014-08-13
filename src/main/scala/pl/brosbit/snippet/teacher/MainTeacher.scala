/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.snippet.teacher 

import _root_.java.util.{ Date, GregorianCalendar, TimeZone }
import _root_.scala.xml.{ NodeSeq, Text, XML }
import _root_.net.liftweb.util._
import _root_.net.liftweb.http.{ SHtml, S, SessionVar }
import _root_.net.liftweb.common._
import _root_.net.liftweb.mapper.{ By, OrderBy, Ascending }
import Helpers._
import pl.brosbit.model._

object ClassChoose extends SessionVar[Long](0L)
object ClassString extends SessionVar[String]("Nie wybrano!")

class MainTeacher {
  
	  
  
	def classList() = {
		val classParamStr = S.param("class").openOr("0")
		val paramClass = tryo(classParamStr.toInt).getOrElse(0)
		val classes = ClassModel.findAll(OrderBy(ClassModel.level, Ascending)).filter(!_.scratched.is)
		if(paramClass != 0) {
		   val choosenClass = classes.filter(theClass => theClass.id.is == paramClass)
		   choosenClass match {
		     case Nil => 
		     case list => {
		       val theClass = list.head
		       ClassChoose.set(theClass.id.is)
		       ClassString.set(theClass.classString)
		     }
		   }
		}
		
		"a" #> classes.map(classItem => {
				"a" #> <a href={"/teacher/index/"+ classItem.id.toString}>{classItem.classString}</a>
			}) &
		"#choosenclass *" #> {if(ClassChoose.is == 0) "wybierz!"
								else ClassString.is }
		}
}

