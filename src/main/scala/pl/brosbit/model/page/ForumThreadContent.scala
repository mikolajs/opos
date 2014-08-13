/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId

case class Comment(id:ObjectId, authorName:String, authorId:String, date:String, content:String) {
  def mapString = Map[String,String](("id"->id.toString),("authorName"->authorName),
		  			("authorId"-> authorId),("date"->date),("content"->content))
}

object ForumThreadContent extends MongoDocumentMeta[ForumThreadContent] {
  override def collectionName = "forumthreadcontent"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new ForumThreadContent(ObjectId.get, "", Nil)
}

case class ForumThreadContent(var _id: ObjectId,
					 		var content:String, var comments:List[Comment]) 
							extends MongoDocument[ForumThreadContent] {
  def meta = ForumThreadContent
  
}


