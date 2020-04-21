package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import eu.brosbit.opos.lib.ZeroObjectId

object LessonWork extends MongoDocumentMeta[LessonWork] {
  override def collectionName = "Works"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new LessonWork(ObjectId.get, ZeroObjectId.get,0L,
    "", "", 0L, "", 0L, 0L, 0L)
}

case class LessonWork( var _id: ObjectId, var lessonId: ObjectId, var teacherId: Long, var description: String,
                var lessonTitle: String, var classId: Long, var className:String, subjectId: Long,
                var start: Long, var end: Long) extends MongoDocument[LessonWork] {
  def meta = LessonWork
}
