package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId


object MessagePupil extends MongoDocumentMeta[MessagePupil] {
  override def collectionName = "MessagePupil"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MessagePupil(ObjectId.get,0L, 0L, "", 0L, "", "", false, "", false)
}

case class MessagePupil(var _id: ObjectId, var classId:Long, var pupilId:Long, var pupilName:String, 
						var teacherId:Long, var teacherName:String,	var body:String, var opinion:Boolean, var dateStr: String,
            var mailed: Boolean) extends MongoDocument[MessagePupil] {
  def meta = MessagePupil
}