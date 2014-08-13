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
import _root_.pl.brosbit.model._
import _root_.net.liftweb.json.JsonDSL._
import _root_.net.liftweb.util.Helpers._

case class Link(url:String, title:String)
case class LinkDepartment(name:String, links:List[Link])

object MainPageLinks extends MongoDocumentMeta[MainPageLinks] {
  override def collectionName = "mainpagelinks"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MainPageLinks(ObjectId.get, Nil)
}

case class MainPageLinks(_id: ObjectId, var links:List[LinkDepartment])
						extends MongoDocument[MainPageLinks] {
  def meta = MainPageLinks
}


							



