package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._


object Opinions extends MongoDocumentMeta[Opinions] {
  override def collectionName = "opinions"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Opinions(ObjectId.get,0L, 0L, "", 0L, "", Nil)
}

case class Opinions(var _id: ObjectId, var classId:Long, var pupilId:Long, var pupilName:String, 
						var teacherId:Long, var teacherName:String,	var content:List[String]) 
							extends MongoDocument[Opinions] {
  def meta = Opinions
}