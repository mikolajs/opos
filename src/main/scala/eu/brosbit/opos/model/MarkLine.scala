package eu.brosbit.opos.model

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

case class Mark(var t: Long, var T: String, var m: String, var w:Int, var c:String, var i:String) {
  def mapString = Map[String, String](("time" -> t.toString), ("teacher" -> T),
    ("mark" -> m), ("weight" -> w.toString), ("color" -> c), ("info" -> i))
  override def toString() = "%s : %s : %s  : %s ".format(m, T, new Date(t).toLocaleString(), i )
}


object MarkLine extends MongoDocumentMeta[MarkLine] {
  override def collectionName = "MarkLine"

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



