package eu.brosbit.opos.model.edu


import _root_.net.liftweb.mongodb._
import eu.brosbit.opos.lib.ZeroObjectId
import net.liftweb.json.{Formats, JsonAST}
import org.bson.types.ObjectId

case class AnswerWorkItem (date: String, a: String, t: Boolean, qId:String, m: String, l: Boolean) {
  import net.liftweb.json.JsonDSL._
  def toJson :JsonAST.JObject = ("date" -> date) ~ ("a"->a) ~ ("t"->t) ~ ("qId" -> qId) ~ ("m" -> m) ~ ("l" -> l)
}

object WorkAnswer extends MongoDocumentMeta[WorkAnswer] {
  override def collectionName = "workanswers"

  override def formats:Formats = super.formats + new ObjectIdSerializer + new DateSerializer

  def create = new WorkAnswer(ObjectId.get, ZeroObjectId.get, 0L, "", Nil,
    false, false)
}

case class WorkAnswer(var _id: ObjectId, var work:ObjectId, var authorId: Long, var authorName : String,
                      var answers: List[AnswerWorkItem], var pupilChanged: Boolean, var teacherChanged: Boolean
                      ) extends MongoDocument[WorkAnswer] {
  def meta: WorkAnswer.type = WorkAnswer

}
