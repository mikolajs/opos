package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

object LessonWork extends MongoDocumentMeta[LessonWork] {
  override def collectionName = "Works"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new LessonWork(ObjectId.get, new ObjectId("000000000000000000000000"), 0L, "", 0L, "",
    0L, 0L)
}

case class LessonWork( var _id: ObjectId, var lessonId: ObjectId, var teacherId: Long,
                var lessonTitle: String, var classId: Long, var className:String,
                var start: Long, var end: Long) extends MongoDocument[LessonWork] {
  def meta = LessonWork
}
