
package eu.brosbit.opos.model.edu

import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

case class QuestElem(q:ObjectId, p:Int)
//authors - last is older
object Quiz extends MongoDocumentMeta[Quiz] {
  override def collectionName = "quizzes"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new Quiz(ObjectId.get, 0L, "", "", "", 0L, Nil)
}

case class Quiz(var _id: ObjectId, var authorId: Long, var description: String,
                var title: String, var subjectName: String, var subjectId: Long,
                var questions: List[QuestElem]) extends MongoDocument[Quiz] {
  def meta = Quiz
}
