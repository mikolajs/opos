package pl.edu.osp.model.edu

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

object HeadWord extends MongoDocumentMeta[HeadWord] {
  override def collectionName = "HeadWords"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new HeadWord(ObjectId.get, 0L, "", "", 0L, "", 0, "")
}

case class HeadWord(var _id: ObjectId, var authorId: Long, var department: String,
                    var title: String, var subjectId: Long, var subjectName: String, var lev: Int, var content: String)
  extends MongoDocument[HeadWord] {
  def meta = HeadWord
}
