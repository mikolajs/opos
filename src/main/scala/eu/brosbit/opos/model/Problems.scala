package eu.brosbit.opos.model

import eu.brosbit.opos.lib.ZeroObjectId
import net.liftweb.mongodb.{DateSerializer, MongoDocument, MongoDocumentMeta, ObjectIdSerializer}
import org.bson.types.ObjectId

object Problems extends MongoDocumentMeta[Problems] {
  override def collectionName = "Problems"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Problems(ObjectId.get, "", "", "", 0L, Nil, Nil)
}

case class Problems(var _id: ObjectId, var description: String, var title: String, var info: String, var author: Long,
                    var inputs: List[String], var expectedOutputs: List[String]) extends MongoDocument[Problems] {
  def meta = Problems
}