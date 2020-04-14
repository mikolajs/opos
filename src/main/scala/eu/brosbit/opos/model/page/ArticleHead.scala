/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package eu.brosbit.opos.model.page

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId


object ArticleHead extends MongoDocumentMeta[ArticleHead] {
  override def collectionName = "articlehead"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new ArticleHead(ObjectId.get, 9999, "", "", 0L, Nil, "", "", true,
    new ObjectId("000000000000000000000000"), new ObjectId("000000000000000000000000"), "")
}

case class ArticleHead(var _id: ObjectId, var prior:Int, var title: String,
                       var authorName: String, var authorId: Long, var tags: List[String],
                       var thumbnailLink: String, var introduction: String, var news: Boolean,
                       var content: ObjectId, var departmentId: ObjectId, var departmentName: String)
  extends MongoDocument[ArticleHead] {
  def meta = ArticleHead
}

// for perform events info:
// var events: Boolean, var when: Long (date), var teacherOnly: Boolean