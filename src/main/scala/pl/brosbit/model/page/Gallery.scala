/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Photo(val thumbnail:String, val full:String)

object Gallery extends MongoDocumentMeta[Gallery] {
  override def collectionName = "galleries"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Gallery(ObjectId.get, "Brak galerii", "", Nil)
}

case class Gallery(var _id: ObjectId, var title:String, var description:String,
					 var photos:List[Photo] )
					extends MongoDocument[Gallery] {
  def meta = Gallery
}

