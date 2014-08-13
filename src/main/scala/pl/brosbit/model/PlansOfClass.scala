/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model 

    
import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Lesson(subject:String, info:String, room:String)
    
  object PlansOfClass extends MongoDocumentMeta[PlansOfClass] {
	override def collectionName = "plansofclass"
	override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
	def create = new PlansOfClass(ObjectId.get, 0L, 0L, 0L, "", Nil, Nil, Nil, Nil, Nil)
}

case class PlansOfClass(var _id: ObjectId, var classId:Long, var dateStart:Long, 
			var dateEnd:Long, var classStr:String, 
			var day1:List[Lesson],var day2:List[Lesson], var day3:List[Lesson],
			var day4:List[Lesson], var day5:List[Lesson]) extends MongoDocument[PlansOfClass] {
  def meta = PlansOfClass
}


