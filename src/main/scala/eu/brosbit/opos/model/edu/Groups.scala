package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

case class StudentInfo(id:Long, cl:String)

object Groups extends MongoDocumentMeta[Groups] {
  override def collectionName = "Groups"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Groups(ObjectId.get, 0L, "", "", Nil)
}
case class Groups( _id: ObjectId, var authorId: Long,  var name: String, var description: String,
                  var students: List[StudentInfo]) extends MongoDocument[Groups] {
  def meta = Groups
}
