
package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object ImagesResource extends MongoDocumentMeta[ImagesResource] {
  override def collectionName = "departments"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create(fileId: ObjectId, subjectId: ObjectId, departmentId: String, authorId: Long)
  = new ImagesResource(ObjectId.get, fileId, subjectId, departmentId, authorId, "", "")
}

case class ImagesResource(var _id: ObjectId, var fileId: ObjectId, var subjectId: ObjectId,
                          var department: String, authorId: Long, authorName: String, text: String)
  extends MongoDocument[ImagesResource] {
  def meta = ImagesResource
}
