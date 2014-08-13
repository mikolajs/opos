/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object ForumThreadHead extends MongoDocumentMeta[ForumThreadHead] {
  override def collectionName = "forumthreadhead"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ForumThreadHead(ObjectId.get,  new ObjectId("000000000000000000000000"),
		  								0, "", "", Nil, "", 0L)
}

case class ForumThreadHead(var _id: ObjectId, 	var content:ObjectId, 
							var count:Int, var lastInfo:String,
							var title:String, var tags:List[String],
							var authorName:String, var authorId:Long) 
							extends MongoDocument[ForumThreadHead] {
  def meta = ForumThreadHead
}


