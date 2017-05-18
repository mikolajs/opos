package pl.edu.osp.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import _root_.net.liftweb.json.JsonDSL._

case class Mark(var time: Long, var teacher: String, var mark: String) {
  def mapString = Map[String, String](("time" -> time.toString), ("teacher" -> teacher),
    ("mark" -> mark))
}


object MarkLine extends MongoDocumentMeta[MarkLine] {
  override def collectionName = "MarkLine"
  override def connectionIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def mongoIdentifier = pl.edu.osp.config.MyMongoIdentifier
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = {
    var array1: Array[List[Mark]] = new Array(25)
    var array2: Array[List[Mark]] = new Array(25)
    array1 = array1.map(a => Nil)
    array2 = array2.map(a => Nil)
    MarkLine(ObjectId.get, 0L, 0L, array1, array2, Nil, Nil)
  }
}

case class MarkLine(var _id: ObjectId, var subjectId: Long, var pupilId: Long,
                    var marksSem1: Array[List[Mark]], var marksSem2: Array[List[Mark]],
                     var sem1End: List[Mark], var sem2End: List[Mark])
  extends MongoDocument[MarkLine] {
  def meta = MarkLine
}


