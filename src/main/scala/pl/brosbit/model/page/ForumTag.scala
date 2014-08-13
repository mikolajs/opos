/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ForumTag extends MongoDocumentMeta[ForumTag] {
  override def collectionName = "forumtag"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ForumTag(ObjectId.get, "", 0)
}

case class ForumTag(var _id: ObjectId, var tag:String, var count:Int ) 
							extends MongoDocument[ForumTag] {
  def meta = ForumTag
}
