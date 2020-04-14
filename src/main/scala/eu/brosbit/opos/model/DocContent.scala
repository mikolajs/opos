package eu.brosbit.opos.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId


object DocContent extends MongoDocumentMeta[DocContent] {
  override def collectionName = "doccontent"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer


  def create = DocContent(ObjectId.get, null, 0L, "", "", 0)
}

case class DocContent( _id: ObjectId, var template:ObjectId, var userId: Long, var userName:String,
                              var content: String, var nr: Int)
  extends MongoDocument[DocContent] {
  def meta = DocContent
}