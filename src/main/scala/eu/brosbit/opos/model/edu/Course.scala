package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId
import java.util.Date

object EduLevels extends Enumeration(1) {
  type EduLevels = Value
  val Elementary, Extended, Competition = Value
  val polishNames = Array("podstawowy", "rozszerzony", "konkursowy")

  def getMaped = EduLevels.values.map(lev => (lev.id -> EduLevels.polishNames(lev.id - 1))).toMap

  def getName(lev: Int) = polishNames(lev - 1)
}


object Course extends MongoDocumentMeta[Course] {
  override def collectionName = "courses"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Course(ObjectId.get, "", 0L, Nil, false, "", "", "", "", Nil, 0L)
}

case class Course(var _id: ObjectId, var title: String, var subjectId: Long,
                  var chapters: List[String], var pub: Boolean, var subjectName: String,
                  var descript: String, var img: String, var classInfo: String,
                  var classList: List[Long], var authorId: Long) extends MongoDocument[Course] {
  def meta = Course

  def getInfo = title + " - " + subjectName + ", klasa " + classInfo

}
