
package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId


//authors - last is older
object Quiz extends MongoDocumentMeta[Quiz] {
  override def collectionName = "quizes"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new Quiz(ObjectId.get, 0L,"", "", "", "", 0,  0L, Nil)
}
case class Quiz(var _id: ObjectId,  var authorId: Long, var description: String, 
				var title: String, var department: String, 
				var subjectName: String, var subjectLev:Int, var subjectId:Long,
				var questions: List[ObjectId])  extends MongoDocument[Quiz] {
  def meta = Quiz
}
