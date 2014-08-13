package pl.brosbit.model.edu

import _root_.net.liftweb.mongodb._
import java.util.Date
import org.bson.types.ObjectId



//authors - last is older
object QuizQuestion extends MongoDocumentMeta[QuizQuestion] {
  override def collectionName = "questions"
  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer
  def create = new QuizQuestion(ObjectId.get, 0L, 0, 0, 0L, "", "", "", "",  Nil)
}

case class QuizQuestion(var _id: ObjectId,  var authorId: Long,  var dificult:Int, var level:Int,
				  var subjectId:Long, var subjectName:String, 
				var question:String, var answer:String, var department:String,
				var fake:List[String])  extends MongoDocument[QuizQuestion] {
  def meta = QuizQuestion
}
