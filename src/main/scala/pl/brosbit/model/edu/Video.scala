package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object Video extends MongoDocumentMeta[Video] {
  override def collectionName = "videos"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Video(ObjectId.get, "", "", "", 1, 0L, 0L, "", "", "", "", false)
}

case class Video(var _id: ObjectId, var link: String, var oldPath: String, var mime: String, var lev: Int,
  var authorId: Long, var subjectId: Long, var subjectName: String, var title: String, var department: String,
  var descript: String, var onServer: Boolean) extends MongoDocument[Video] {
  def meta = Video
}
