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


object ExtraLessons extends MongoDocumentMeta[ExtraLessons] {
  override def collectionName = "extralessons"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = ExtraLessons(ObjectId.get, "", "", "", "", 0L)
  
  
}

case class ExtraLessons(var _id: ObjectId, var title:String, var description:String,
					 var when:String, var teacherName:String, var teacherId:Long ) extends MongoDocument[ExtraLessons] {
  def meta = ExtraLessons
}

