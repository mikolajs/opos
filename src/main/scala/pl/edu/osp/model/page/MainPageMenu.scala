/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */

package pl.edu.osp.model.page

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId
import _root_.pl.edu.osp.model._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.util.Helpers._

case class Link(url: String, title: String)

object MainPageMenu extends MongoDocumentMeta[MainPageMenu] {
  override def collectionName = "mainpagemenu"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = MainPageMenu(ObjectId.get, "", Nil)
}

case class MainPageMenu(_id: ObjectId, var name:String, var links: List[Link])
  extends MongoDocument[MainPageMenu] {
  def meta = MainPageMenu
}


							



