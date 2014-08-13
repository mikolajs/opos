package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._


object DocTemplateHead extends MongoDocumentMeta[DocTemplateHead] {
  override def collectionName = "doctemplatehead"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = DocTemplateHead(ObjectId.get, "", "", "",  new ObjectId("000000000000000000000000"))
}

case class DocTemplateHead(var _id: ObjectId, var title:String,
							var comment:String,	var template:String, var content:ObjectId) 
							extends MongoDocument[DocTemplateHead] {
  def meta = DocTemplateHead
}