package pl.brosbit.model


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._
import pl.brosbit.model.page.Comment


object Messages extends MongoDocumentMeta[Messages] {
  override def collectionName = "Messages"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Messages(ObjectId.get, 0L, Nil)
}

case class Messages(var _id: ObjectId, var ownerId:Long, var mes: List[Comment]) 
							extends MongoDocument[Messages] {
  def meta = Messages
}