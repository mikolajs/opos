package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.ZeroObjectId

object Work extends MongoDocumentMeta[Work] {
  override def collectionName = "Works"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Work(ObjectId.get, ZeroObjectId.get, ZeroObjectId.get, 0L,
    "", "", 0L, "", 0L, "", 0L, 0L)
}

case class Work(var _id: ObjectId, var lessonId: ObjectId, var courseId: ObjectId, var teacherId: Long,
                var description: String, var lessonTitle: String, var classId: Long, var className:String,
                var subjectId: Long, var subjectName: String, var start: Long, var end: Long) extends MongoDocument[Work] {
  def meta = Work
}
