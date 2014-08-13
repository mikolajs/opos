package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

object ThemesPlan extends MongoDocumentMeta[ThemesPlan] {
  override def collectionName = "themesplan"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = ThemesPlan(ObjectId.get, Nil, "", "", "", 0L)
}

case class ThemesPlan(var _id: ObjectId, var classes:List[String], 
								var subjectStr:String, var urlLink:String,
								var teacherName:String, var teacherId:Long) 
							extends MongoDocument[ThemesPlan] {
  def meta = ThemesPlan
  def isValid = this.urlLink.length > 20
}