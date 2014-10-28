package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import pl.brosbit.model.page.Comment


object TeacherAnnounce extends MongoDocumentMeta[TeacherAnnounce] {
  override def collectionName = "TeacherAnnounce"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = TeacherAnnounce(ObjectId.get, 0L, "", "", Nil)
}

case class TeacherAnnounce(var _id: ObjectId,  var authorId:Long,
				  var authorName:String,	var body: String, var comments:List[Comment]) 
							extends MongoDocument[TeacherAnnounce] {
  def meta = TeacherAnnounce
}