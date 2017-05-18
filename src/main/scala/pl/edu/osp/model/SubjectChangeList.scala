/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model

import net.liftweb.mongodb.{MongoDocument, DateSerializer, ObjectIdSerializer, MongoDocumentMeta}
import org.bson.types.ObjectId


object SubjectChangeList extends  MongoDocumentMeta[SubjectChangeList] {
  override def collectionName = "subjectchangelist"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = SubjectChangeList(ObjectId.get, "", "", 0, 0L)
}

case class SubjectChangeList(var _id: ObjectId, name: String, short: String,
                            nr: Int, date: Long
                  ) extends MongoDocument[SubjectChangeList] {
  def meta = SubjectChangeList
}

