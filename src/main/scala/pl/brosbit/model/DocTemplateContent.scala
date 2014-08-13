package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

case class TemplatePiece(userId:String, userName:String, template:String) {
  def mapString = Map[String,String](("userId"-> userId.toString),("userName"->userName),
		  			("template"-> template))
}

object DocTemplateContent extends MongoDocumentMeta[DocTemplateContent] {
  override def collectionName = "doctemplatecontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = DocTemplateContent(ObjectId.get, Nil)
}

case class DocTemplateContent(var _id: ObjectId,
							var content:List[TemplatePiece]) 
							extends MongoDocument[DocTemplateContent] {
  def meta = DocTemplateContent
}