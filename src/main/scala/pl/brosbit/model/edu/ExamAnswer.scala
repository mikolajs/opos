package pl.brosbit.model.edu


import _root_.net.liftweb.mongodb._
import org.bson.types.ObjectId

case class AnswerItem (var q:String, var a: String, var p: Int) {
  def json = """ {"q": "%s", "a": "%s" , "p" : "%d" } """.format(q, a, p)
}



object ExamAnswer extends MongoDocumentMeta[ExamAnswer] {
  override def collectionName = "examanswers"

  override def formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new ExamAnswer(ObjectId.get, ObjectId.get, "", 0L,  Nil)
}

case class ExamAnswer(var _id: ObjectId, var exam:ObjectId, var code:String,
                      var authorId: Long,  var answers: List[AnswerItem]
                ) extends MongoDocument[ExamAnswer] {
  def meta = ExamAnswer

}