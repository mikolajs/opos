package eu.brosbit.opos.model.page

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

object FlashTile extends MongoDocumentMeta[FlashTile] {
  override def collectionName = "flashtile"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = FlashTile(ObjectId.get, "", "", "", "")
}

case class FlashTile(var _id: ObjectId, var page:String, var link: String,
                          var img: String, var info: String)
  extends MongoDocument[FlashTile] {
  def meta = FlashTile
}
