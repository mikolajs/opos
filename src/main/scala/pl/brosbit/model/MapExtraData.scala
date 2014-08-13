/*
 * Copyright (C) 2011   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register - Wirtualny Dziennik)
 *   LICENCE: GNU AFFERO GENERAL PUBLIC LICENS Version 3 (AGPLv3)
 *   See: <http://www.gnu.org/licenses/>.
 */
package pl.brosbit.model

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._


object MapExtraData extends MongoDocumentMeta[MapExtraData] {
  override def collectionName = "mapextradata"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = MapExtraData(ObjectId.get, "", Map())
  
   def getMapData(key: String): Map[String,String] = {
        MapExtraData.findAll(("key" -> key))  match {
          case Nil => Map()
          case list  => list.head.data
        }
      }
  def setMapData(key : String, data:Map[String,String]) {
    val keyTrimmed = key.trim
    val mapExtraData = MapExtraData.findAll(("key" -> keyTrimmed))  match {
          case Nil => MapExtraData.create
          case list  => list.head
        }
    mapExtraData.key = keyTrimmed
    mapExtraData.data = data
    mapExtraData.save
  }
  
}

case class MapExtraData(var _id: ObjectId, var key:String,
					 var data:Map[String,String] ) extends MongoDocument[MapExtraData] {
  def meta = MapExtraData
}

