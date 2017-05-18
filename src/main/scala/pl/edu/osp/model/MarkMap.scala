/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *
 *   VRegister is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU AFFERO GENERAL PUBLIC LICENS Version 3
 *   as published by the Free Software Foundation
 *
 *   VRegister is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU AFFERO GENERAL PUBLIC LICENS
 *   along with VRegister.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model

import net.liftweb.mongodb.{MongoDocument, DateSerializer, ObjectIdSerializer, MongoDocumentMeta}
import org.bson.types.ObjectId


object MarkMap extends  MongoDocumentMeta[MarkMap] {
  override def collectionName = "markmap"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = MarkMap(ObjectId.get, "",  0.0)
}

case class MarkMap(var _id: ObjectId, sym: String, value: Double
                  ) extends MongoDocument[MarkMap] {
  def meta = MarkMap
}


