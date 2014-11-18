package pl.brosbit.model


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

//dest: t - teachers, p - pupils, i - indywidual, c - from course
object Message extends MongoDocumentMeta[Message] {
  override def collectionName = "Message"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Message(ObjectId.get, 0L, "", "", Nil, 0L, "", "")
}

case class Message(var _id: ObjectId, var authorId:Long,  var authorName: String,
                   var  dest: String, var who: List[Long],  var cl: Long, var body: String,
                   var date: String
                   ) extends MongoDocument[Message] {
  def meta = Message
  def forAllTechers = dest == "t"
  def forAllPupils = dest == "p"
  def forIndywiduals = dest == "i"
}