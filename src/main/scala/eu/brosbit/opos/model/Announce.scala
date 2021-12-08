package eu.brosbit.opos.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

object Announce extends MongoDocumentMeta[Announce] {
  override def collectionName = "Announce"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = Announce(ObjectId.get, 0L, "", 0L, "", false, "")
}

case class Announce(var _id: ObjectId, var teacherId: Long, var teacherName: String,
                    var classId: Long, var date: String, var mailed: Boolean, var body: String)
  extends MongoDocument[Announce] {
  def meta = Announce
}