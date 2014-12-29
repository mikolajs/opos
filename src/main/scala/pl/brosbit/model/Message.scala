package pl.brosbit.model


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

case class MessageChunk(var authorId:Long, var author: String, var date:String, var body:String)

object Message extends MongoDocumentMeta[Message] {
  override def collectionName = "Message"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Message(ObjectId.get, false, Nil, Nil,  false, 0L)
}

case class Message(var _id: ObjectId,
                   var all:Boolean, var who:List[Long], var body: List[MessageChunk],
                   var mailed:Boolean, var lastDate:Long
                   ) extends MongoDocument[Message] {
  def meta = Message
}