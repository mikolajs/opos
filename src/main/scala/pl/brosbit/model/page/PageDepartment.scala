/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
 package pl.brosbit.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


object PageDepartment extends MongoDocumentMeta[PageDepartment] {
  override def collectionName = "pagedepartment"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = PageDepartment(ObjectId.get, "", 99)
}

case class PageDepartment(var _id: ObjectId, var name:String, var nr:Int)
					extends MongoDocument[PageDepartment] {
  def meta = PageDepartment
}