/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ImageSlides extends MongoDocumentMeta[ImageSlides] {
  override def collectionName = "imageSlides"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new ImageSlides(ObjectId.get, "", "", 0L,  0)
}

case class ImageSlides(val _id: ObjectId, var src: String, var code:String,
                       var author: Long, var order: Int)
  extends MongoDocument[ImageSlides] {
  def meta = ImageSlides
}

