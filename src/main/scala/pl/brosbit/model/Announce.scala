package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId


object Announce extends MongoDocumentMeta[Announce] {
  override def collectionName = "Announce"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Announce(ObjectId.get, 0L, "", false, 0L,  "", "", "", null)
}

case class Announce(var _id: ObjectId,  var authorId:Long,  var authorName:String,
                           var pupils:Boolean, var classId: Long, var eventId:String,
                           var title:String, var body: String, var comments:ObjectId )
					extends MongoDocument[Announce] {
  def meta = Announce
}