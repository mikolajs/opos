package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.ZeroObjectId

object Work_old extends MongoDocumentMeta[Work_old] {
  override def collectionName = "Works_old"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Work_old(ObjectId.get, ZeroObjectId.get, ZeroObjectId.get, 0L,
    "", "", "", "", 0L, "", 0L)
}

case class Work_old(var _id: ObjectId, var lessonId: ObjectId, var courseId: ObjectId, var teacherId: Long,
                var description: String, var lessonTitle: String, var groupId: String, var groupName:String,
                var subjectId: Long, var subjectName: String, var start: Long) extends MongoDocument[Work_old] {
  def meta = Work_old
}
