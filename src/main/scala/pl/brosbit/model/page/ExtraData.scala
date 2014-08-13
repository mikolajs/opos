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
import _root_.net.liftweb.json.JsonDSL._

object ExtraDataKeys extends Enumeration {
  val startPageKey = Value("startpagebip")
}

object ExtraData extends MongoDocumentMeta[ExtraData] {
  override def collectionName = "extradata"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = ExtraData(ObjectId.get, "", "")
  
  def getData(key: String): String = {
        ExtraData.findAll(("key" -> key))  match {
          case Nil => ""
          case list  => list.head.data
        }
      }
}

case class ExtraData(var _id: ObjectId, var key:String,
					 var data:String ) extends MongoDocument[ExtraData] {
  def meta = ExtraData
}

