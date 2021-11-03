package eu.brosbit.opos.model

import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId


object LessonThemes extends MongoDocumentMeta[LessonThemes] {
  override def collectionName = "lessonThemes"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = LessonThemes(ObjectId.get, 0L, 0L, 0L, 0L, 0L, "", "", "", "")
}

case class LessonThemes(var _id:ObjectId, var group: Long, var subjectId: Long, var teacherId: Long,
                        var classId: Long, var date:Long, var teacherName: String, var theme:String,
                        var directing: String, var description: String ) extends  MongoDocument[LessonThemes]{
  def meta = LessonThemes
}


