package pl.edu.osp.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

object Exam extends MongoDocumentMeta[Exam] {
  override def collectionName = "Exams"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Exam(ObjectId.get, 0L, "", 0L, "", 0L, "",  Nil, Nil, 0L, 0L, false)
}

case class Exam(var _id: ObjectId, var authorId: Long,  var description: String,
                    var subjectId: Long, var subjectName: String,
                    var classId: Long, var className:String, var quizzes: List[ObjectId],
                    var keys: List[String], var start: Long, var end: Long, var multi: Boolean)
  extends MongoDocument[Exam] {
  def meta = Exam
}