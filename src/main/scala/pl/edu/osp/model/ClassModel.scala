/* Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model


import net.liftweb.mongodb.{MongoDocument, DateSerializer, ObjectIdSerializer, MongoDocumentMeta}
import org.bson.types.ObjectId


object ClassModel extends  MongoDocumentMeta[ClassModel] {
  override def collectionName = "classmodel"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = ClassModel(ObjectId.get, 0, "", "", 0L, false)
}

case class ClassModel(var _id: ObjectId, level: Int, division: String, descr: String,
                     teacher: Long, scratched: Boolean
                  ) extends MongoDocument[ClassModel] {
  def meta = ClassModel
  def classString(): String = level + division

  def shortInfo(): String = classString() + " [" + _id.toString + "]"

}


