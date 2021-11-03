package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import eu.brosbit.opos.lib.ZeroObjectId
import eu.brosbit.opos.model.edu
import net.liftweb.json.{Formats, JsonAST}
import org.bson.types.ObjectId

case class MessageItem (date: String,  a: String, t: Boolean, m: String, l: Boolean) {
  import net.liftweb.json.JsonDSL._
  def toJson :JsonAST.JObject = ("date" -> date)  ~ ("a"->a) ~ ("t"->t) ~ ("m" -> m) ~ ("l" -> l)
}

object  MessagesWork extends MongoDocumentMeta[MessagesWork] {
  override def collectionName = "messageswork"

  override def formats:Formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new MessagesWork(ObjectId.get, ZeroObjectId.get, Nil)
}

case class MessagesWork(var _id: ObjectId, var work:ObjectId,
                      var messages: List[MessageItem]) extends MongoDocument[MessagesWork] {
  def meta: edu.MessagesWork.type = MessagesWork

}