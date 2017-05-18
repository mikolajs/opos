package pl.edu.osp.model.page


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

object LinkTilesMainPage extends MongoDocumentMeta[LinkTilesMainPage] {
  override def collectionName = "linktilesmainpage"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = LinkTilesMainPage(ObjectId.get, "", "", 0)
}

case class LinkTilesMainPage(var _id: ObjectId, var img: String, var url: String,
                     var order: Int)
  extends MongoDocument[LinkTilesMainPage] {
  def meta = LinkTilesMainPage
}
