package pl.edu.osp.model.page

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId


object MainPageSlide extends MongoDocumentMeta[MainPageSlide] {
  override def collectionName = "MainPageSlide"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = MainPageSlide(ObjectId.get, "", "", "")
}

case class MainPageSlide(var _id: ObjectId,
                         var desc: String, var html: String, var img: String)
  extends MongoDocument[MainPageSlide] {
  def meta = MainPageSlide
}
