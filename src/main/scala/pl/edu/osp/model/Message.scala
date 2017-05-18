package pl.edu.osp.model


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import net.liftweb.json.JsonDSL._

case class MessageChunk(var authorId: String, var author: String, var date: String, var body: String) {
  //def forJSONStr = "{\"authorId\":\"" + authorId + "\",\"author\":\"" + author + "\",\"date\":\"" +
  //  date + "\",\"body\":\"" + body+ "\"}"
  //def toQuest = ("authorId"->authorId)~("author"->author)~("date"->date)~("body"->body)
  def toMap = Map("authorId" -> authorId, "author" -> author, "date" -> date, "body" -> body)
}

object Message extends MongoDocumentMeta[Message] {
  override def collectionName = "Message"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = Message(ObjectId.get, false, Nil, "", Nil, false, 0L)
}

case class Message(var _id: ObjectId,
                   var all: Boolean, var who: List[Long], var people: String,
                   var body: List[MessageChunk], var mailed: Boolean, var lastDate: Long
                    ) extends MongoDocument[Message] {
  def meta = Message
}