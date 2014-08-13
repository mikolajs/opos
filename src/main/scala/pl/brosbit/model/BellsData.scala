package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

object BellsData extends MongoDocumentMeta[BellsData] {
  override def collectionName = "bellsdata"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = BellsData(ObjectId.get, Nil, Nil)
  
  def getOrCreate = BellsData.findAll match {
    case bell::list => {
      if(!list.isEmpty) list.head.delete
      bell
    }
    case _ => BellsData.create
  }
}

case class BellsData(var _id: ObjectId, var beginLesson:List[String],
								var endLesson:List[String]) 
							extends MongoDocument[BellsData] {
  def meta = BellsData
  
  def getLessonTime(number:Int) = this.beginLesson(number) + " - " + this.endLesson(number)
}